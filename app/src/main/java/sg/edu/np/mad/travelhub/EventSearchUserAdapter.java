package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventSearchUserAdapter extends RecyclerView.Adapter<EventViewHolder>{
    Context context;
    List<Event> events;
    private FirebaseUser firebaseUser;

    public EventSearchUserAdapter(Context context, ArrayList<Event> events){
        this.context = context;
        this.events = events;
    }

    public void updateList(List<Event> filteredList) {
        this.events = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.user, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        // Assign values to views according to position of view
        Event event = events.get(position);
        holder.id.setText(event.getEventDetails().getEventName());
        holder.name.setText(event.getEventDetails().getCategory());
        Glide.with(context)
                .load(event.getEventDetails().getDate()) // Assuming image URL is stored here
                .into(holder.profImage);

        isFollowing(holder, event); // To keep updating the following status
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    private void isFollowing(EventViewHolder holder, Event event) {
        // You might need to adjust the logic here depending on how the "following" status is stored
        DatabaseReference followRef = FirebaseDatabase.getInstance().getReference("EventFollow")
                .child(firebaseUser.getUid()).child(event.getEventDetails().getEventName());

        followRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.followstatus.setText("Following");
                } else {
                    holder.followstatus.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("isFollowing", "Error checking following status", error.toException());
            }
        });
    }
}