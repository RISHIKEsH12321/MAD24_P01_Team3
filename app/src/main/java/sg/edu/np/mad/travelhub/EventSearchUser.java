package sg.edu.np.mad.travelhub;

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
                int position = viewHolder.getAbsoluteAdapterPosition();
                User user = usersList.get(position);
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();
                    copyEventForUser(user, userId);
                } else {
                    Toast.makeText(EventSearchUser.this, "Failed to copy event", Toast.LENGTH_SHORT).show();
                }

                // Reset the swiped item position
                adapter.notifyItemChanged(position);
            }
        }

        @Override
        public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
            return 0.40f;
        }

        @Override
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
            if (user.getId().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(user);
            }
        }
        adapter.updateList(filteredList);
    }

    private void copyEventForUser(User originalUser, String newUserId) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Event");

        eventRef.orderByChild("users").equalTo(originalUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                        CompleteEvent originalEvent = eventSnapshot.child("eventDetails").getValue(CompleteEvent.class);
                        if (originalEvent != null) {
                            // Create a new CompleteEvent with the same details but new user ID
                            CompleteEvent newEvent = new CompleteEvent(
                                    originalEvent.attachmentImageList,
                                    originalEvent.itineraryEventList,
                                    originalEvent.toBringItems,
                                    originalEvent.notesList,
                                    originalEvent.reminderList,
                                    originalEvent.date,
                                    originalEvent.category,
                                    originalEvent.eventName
                            );
                            // Use the same eventID as the original event
                            newEvent.eventID = originalEvent.eventID;
                            // Add the new event to the local database
                            try {
                                dbHandler.addEvent(EventSearchUser.this,newEvent);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                            // Push the new event to Firebase
                            pushEventToFirebase(newEvent, newUserId);

                            Toast.makeText(EventSearchUser.this, "User Added", Toast.LENGTH_SHORT).show();
                            return; // Copy only the first event
                        }
                    }
                } else {
                    Toast.makeText(EventSearchUser.this, "User Removed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EventSearchUser.this, "Error Adding/Removing: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pushEventToFirebase(CompleteEvent event, String userID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("EventName", event.eventName);
        eventMap.put("Date", event.date);
        eventMap.put("Category", event.category);
        eventMap.put("Notes", event.notesList);
        eventMap.put("Reminders", event.reminderList);
        eventMap.put("ToBringItems", event.toBringItems);
        eventMap.put("ItineraryEventList", event.itineraryEventList);
        eventMap.put("AttachmentImageList", event.attachmentImageList);

        String key = databaseReference.child("Event").push().getKey();
        if (key != null) {
            databaseReference.child("Event").child(key).child("users").setValue(userID);
            databaseReference.child("Event").child(key).child("eventDetails").setValue(eventMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("TOFIREBASE", "Event pushed to Firebase successfully.");
                        } else {
                            Log.e("TOFIREBASE", "Failed to push event to Firebase.", task.getException());
                        }
                    });
        } else {
            Log.e("TOFIREBASE", "Failed to create a unique key for the event.");
        }
    }
}