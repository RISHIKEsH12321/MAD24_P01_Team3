package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sg.edu.np.mad.travelhub.ParentItem;
import sg.edu.np.mad.travelhub.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ParentViewHolder> {

    private Map<String, ParentItem> parentItemMap;
    private List<String> postIds;
    private DatabaseReference userRef;

    //private List<ParentItem> parentItemList;
    public ParentAdapter() {
        this.parentItemMap = new HashMap<>();
        this.postIds = new ArrayList<>();
    }

    public void setParentItemMap(Map<String, ParentItem> parentItemMap){
        this.parentItemMap = parentItemMap;
        this.postIds = new ArrayList<>(this.parentItemMap.keySet());

        // Sort the postIds list based on the numeric value of the keys
        Collections.sort(postIds, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                // Extract the numeric part of the keys
                int num1 = Integer.parseInt(o1.replaceAll("[^0-9]", ""));
                int num2 = Integer.parseInt(o2.replaceAll("[^0-9]", ""));
                return Integer.compare(num1, num2);
            }
        });
        //notifyDataSetChanged();
    }

    public Map<String, ParentItem> getParentItemMap() {
        return parentItemMap;
    }

    public List<String> getPostIds() {
        return postIds;
    }

    public void setPostIds(List<String> postIds) {
        this.postIds = postIds;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_parent_item , parent , false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {

        String postId = postIds.get(position);
        holder.postId = postId;
        ParentItem parentItem = parentItemMap.get(postId);
        holder.parentName.setText(parentItem.getParentName());
        Glide.with(holder.itemView.getContext()).load(parentItem.getParentImage())
                .into(holder.parentImage);


        // Current user of the app
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = currentUser.getUid();
        holder.currentUid = currentUid;

        // User that created the post
        holder.creatorUid = parentItem.getParentUser();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(parentItem.getParentUser());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User userObject = snapshot.getValue(User.class); // Assuming your User class exists
                    if (userObject != null) {
                        String userid = userObject.getName();
                        holder.parentUser.setText(userid);

                        // Update UI elements with retrieved name and description
                    } else {
                        Log.w("TAG", "User object not found in database");
                    }
                } else {
                    Log.d("TAG", "No user data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Error retrieving user data", error.toException());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (parentItemMap != null){
            return parentItemMap.size();
        }else{
            return 0;
        }

    }

    public class ParentViewHolder extends RecyclerView.ViewHolder{
        private String postId, creatorUid, currentUid;
        private TextView parentName, parentUser;
        private ImageView parentImage;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            parentName = itemView.findViewById(R.id.eachParentName);
            parentUser = itemView.findViewById(R.id.eachParentUser);
            parentImage = itemView.findViewById(R.id.eachParentIV);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!Objects.equals(creatorUid, currentUid)) {
                        Intent postIntent = new Intent(itemView.getContext(), Post.class);
                        postIntent.putExtra("postId", postId);
                        itemView.getContext().startActivity(postIntent);
                    }
                    else {
                        Intent postEditIntent = new Intent(itemView.getContext(), PostEdit.class);
                        postEditIntent.putExtra("postId", postId);
                        itemView.getContext().startActivity(postEditIntent);
                    }
                }
            });
        }
    }
}
