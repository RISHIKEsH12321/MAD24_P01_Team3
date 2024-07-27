package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class EventSearchUser extends AppCompatActivity {

    DatabaseReference ref;
    String displayStr;
    RecyclerView recyclerView;
    SearchView searchBar;
    List<User> usersList;
    ValueEventListener eventListener;
    private Loading_Dialog loadingDialog;
    UserAdapter adapter;
    FirebaseUser firebaseUser;
    DatabaseHandler dbHandler;
    String formattedUserIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_search_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        displayStr = "Add/Remove";

        loadingDialog = new Loading_Dialog(this);
        dbHandler = new DatabaseHandler(this, null, null, 1);

        searchBar = findViewById(R.id.ESearch);
        ref = FirebaseDatabase.getInstance().getReference("Users");
        recyclerView = findViewById(R.id.EList);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(EventSearchUser.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        usersList = new ArrayList<>();
        adapter = new UserAdapter(EventSearchUser.this, (ArrayList<User>) usersList);
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
                adapter.notifyDataSetChanged();
                loadingDialog.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        searchBar.clearFocus();
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
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.LEFT) {
                // Get the position of the swiped item
                int position = viewHolder.getAbsoluteAdapterPosition();

                // Retrieve the User object from the list
                User user = usersList.get(position);

                // Get current Firebase user ID
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if (firebaseUser != null) {
                    String currentUserId = firebaseUser.getUid();
                    String eventId = getEventIdFromPreferences();
                    Log.d("EventSearchUser", "Event Owner User ID: " + currentUserId);
                    Log.d("EventSearchUser", "Event ID: " + eventId);
                    Log.d("EventSearchUser", "Recipient User's ID: " + user.getUid());

                    if (eventId != null) {
                        // Create the formatted user ID
                        String formattedUserIds = user.getName() + "," + user.getEmail();

                        // Push the formatted user ID to the database under "users"
                        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference()
                                .child("Event")
                                .child(eventId);

                        // Get the existing users string
                        eventRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String existingUsers = dataSnapshot.getValue(String.class);
//                                String newUser = formattedUserIds;
                                String newUser = user.getUid();
                                // Update the users string
                                eventRef.child("users").setValue(newUser, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError == null) {
                                            Toast.makeText(EventSearchUser.this, "User added successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(EventSearchUser.this, "Failed to add user: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(EventSearchUser.this, "Failed to read users: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Push complete event to Firebase
                        try {
                            CompleteEventUser completeEvent = getCompleteEvent(eventId);
                            if (completeEvent != null) {
                                DatabaseReference completeEventRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Events")
                                        .child(currentUserId)
                                        .child(eventId)
                                        .child("eventDetails");

                                completeEventRef.setValue(completeEvent, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError == null) {
                                            Toast.makeText(EventSearchUser.this, "Event saved successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(EventSearchUser.this, "Failed to save event: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(EventSearchUser.this, "Error parsing date: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EventSearchUser.this, "Event ID not found in preferences", Toast.LENGTH_SHORT).show();
                    }
                    // Reset the swiped item position
                    adapter.notifyItemChanged(position);
                }
            }
        }

        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(EventSearchUser.this, R.color.main_orange))
                    .addSwipeLeftActionIcon(R.drawable.ic_profile)
                    .addSwipeLeftLabel(displayStr)
                    .create()
                    .decorate();
            float maxSwipeDistance = recyclerView.getWidth() * 0.4f;
            float clampedDx = Math.max(-maxSwipeDistance, Math.min(dX, maxSwipeDistance));
            super.onChildDraw(c, recyclerView, viewHolder, clampedDx, dY, actionState, isCurrentlyActive);
        }
    };

    private void filterList(String text, UserAdapter adapter) {
        List<User> filteredList = new ArrayList<>();
        for (User user : usersList) {
            if (user.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(user);
            }
        }
        adapter.updateList(filteredList);
    }

    private CompleteEventUser getCompleteEvent(String eventId) throws ParseException {
        SharedPreferences sharedPreferences = getSharedPreferences("EventPrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(eventId, "");
        if (!json.isEmpty()) {
            Gson gson = new Gson();
            return gson.fromJson(json, CompleteEventUser.class);
        }
        return null;
    }

    private String getEventIdFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("EventPreferences", MODE_PRIVATE);
        return sharedPreferences.getString("clickedEvent", null);
    }
}