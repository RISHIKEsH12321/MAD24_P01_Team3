package sg.edu.np.mad.travelhub;

import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SearchUser extends AppCompatActivity {

    DatabaseReference ref;
    String dbPath;
    FirebaseDatabase db;
    DatabaseReference updatingUserRef;
    private RecyclerView recyclerView;
    FloatingActionButton fabSearch;
    private SearchView searchBar;
    List<User> usersList;
    ValueEventListener eventListener;
    private Loading_Dialog loadingDialog;
    UserAdapter adapter;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




        loadingDialog = new Loading_Dialog(this);

        //get views
        searchBar = findViewById(R.id.SUsvSearch);
        ref = FirebaseDatabase.getInstance().getReference("Users");
        recyclerView = findViewById(R.id.SUrvList);

        //swipe menu
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //GridLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchUser.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        //Alert Dialog when user presses the recyclerview

        //list
        usersList = new ArrayList<>();
        adapter = new UserAdapter(SearchUser.this, (ArrayList<User>) usersList);
        recyclerView.setAdapter(adapter);
        loadingDialog.startLoadingDialog();
        eventListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot userSnapshot: snapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    usersList.add(user);
                }
                adapter.notifyDataSetChanged();
                loadingDialog.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //something
            }
        });

        //searchBar logic
        searchBar.clearFocus(); //incase focus is default on the search bar
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText, adapter);
                return true;
            }
        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false; //dont use, as this is for up down movement of each recyclerview item
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.LEFT) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                User user = usersList.get(position);
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference("Follow")
                        .child(user.getUid()).child("followers").child(firebaseUser.getUid());
                DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("Follow")
                        .child(firebaseUser.getUid()).child("following").child(user.getUid());

                followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            //user is followed, so unfollow them
                            followersRef.removeValue();
                            followingRef.removeValue();
                            Toast.makeText(SearchUser.this, "User unfollowed", Toast.LENGTH_SHORT).show();
                        } else {
                            //user is not followed, so follow them
                            followersRef.setValue(true);
                            followingRef.setValue(true);
                            Toast.makeText(SearchUser.this, "User followed", Toast.LENGTH_SHORT).show();
                        }
                        //reset the swiped item position (so its not stuck there)
                        adapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("onSwiped", "Error updating follow status", error.toException());
                        //reset the swiped item position (so its not stuck there)
                        adapter.notifyItemChanged(position);
                    }
                });
            }
        }
        @Override
        public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
            // Return 0.5 to allow the action to trigger when swiped halfway
            return 0.40f;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(SearchUser.this, R.color.main_orange))
                    .addSwipeLeftActionIcon(R.drawable.ic_profile)
                    .addSwipeLeftLabel("Follow/Unfollow")
                    .create()
                    .decorate();
            //lock the swiping so users only need to swipe halfway
            float maxSwipeDistance = recyclerView.getWidth() * 0.4f; //40% of the width
            float clampedDx = Math.max(-maxSwipeDistance, Math.min(dX, maxSwipeDistance));
            super.onChildDraw(c, recyclerView, viewHolder, clampedDx, dY, actionState, isCurrentlyActive);
        }
    };

    //to filter the list of results in search bar
    private void filterList(String text, UserAdapter adapter) {
        List<User> filteredList = new ArrayList<>();
        for (User user : usersList) {
            if (user.getId().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(user);
            }
        }
        adapter.updateList(filteredList);
    }
}