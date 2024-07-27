package sg.edu.np.mad.travelhub;

import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class usersearch extends Fragment {

    DatabaseReference ref;
    String displayStr;
    RecyclerView recyclerView;
    SearchView searchBar;
    List<User> usersList;
    ValueEventListener eventListener;
    private Loading_Dialog loadingDialog;
    boolean isFollowing;
    UserAdapter adapter;
    FirebaseUser firebaseUser;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchBar = view.findViewById(R.id.SUsvSearch);
        recyclerView = view.findViewById(R.id.SUrvList);

        displayStr = "Follow/Unfollow";

        loadingDialog = new Loading_Dialog(getActivity());

        //get views

        ref = FirebaseDatabase.getInstance().getReference("Users");

        //swipe menu
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //GridLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        //Alert Dialog when user presses the recyclerview

        //list
        usersList = new ArrayList<>();
        adapter = new UserAdapter(getContext(), (ArrayList<User>) usersList);
        recyclerView.setAdapter(adapter);
        loadingDialog.startLoadingDialog();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = firebaseUser.getUid();
        eventListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot userSnapshot: snapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    if (user != null && firebaseUser != null) {
                        if (!user.getUid().equals(currentUid)) {
                            usersList.add(user);
                        }
                    }
                }
                categorizeUsersByFollowStatus();
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                            Toast.makeText(getContext(), "User unfollowed", Toast.LENGTH_SHORT).show();
                        } else {
                            //user is not followed, so follow them
                            followersRef.setValue(true);
                            followingRef.setValue(true);
                            Toast.makeText(getContext(), "User followed", Toast.LENGTH_SHORT).show();
                        }
                        categorizeUsersByFollowStatus();
                        adapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("onSwiped", "Error updating follow status", error.toException());
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
                    .addBackgroundColor(ContextCompat.getColor(getContext(), R.color.main_orange))
                    .addSwipeLeftActionIcon(R.drawable.ic_profile)
                    .addSwipeLeftLabel(displayStr)
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

    private void categorizeUsersByFollowStatus() {
        List<User> followedUsers = new ArrayList<>();
        List<User> unfollowedUsers = new ArrayList<>();

        for (User user : usersList) {
            DatabaseReference followRef = FirebaseDatabase.getInstance().getReference("Follow")
                    .child(user.getUid()).child("followers").child(firebaseUser.getUid());

            followRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        followedUsers.add(user);
                    } else {
                        unfollowedUsers.add(user);
                    }
                    updateUsersList(followedUsers, unfollowedUsers);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("categorizeUsers", "Error categorizing users", error.toException());
                }
            });
        }
    }

    private void updateUsersList(List<User> followedUsers, List<User> unfollowedUsers) {
        usersList.clear();
        usersList.addAll(followedUsers);
        usersList.addAll(unfollowedUsers);
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_usersearch, container, false);
    }
}
