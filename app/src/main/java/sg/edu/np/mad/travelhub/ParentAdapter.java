package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sg.edu.np.mad.travelhub.ParentItem;
import sg.edu.np.mad.travelhub.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
//        Collections.sort(postIds, new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                // Extract the numeric part of the keys
//                int num1 = Integer.parseInt(o1.replaceAll("[^0-9]", ""));
//                int num2 = Integer.parseInt(o2.replaceAll("[^0-9]", ""));
//                return Integer.compare(num1, num2);
//            }
//        });
        notifyDataSetChanged();
    }

    public void updateList(Map<String, ParentItem> parentItemMap) {
        this.parentItemMap = parentItemMap;
        notifyDataSetChanged();
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

    public interface CommentCountCallback {
        void onCommentCountRetrieved(int count);
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
        // Check if parentItem is null
        if (parentItem == null) {
            Log.w("TimestampCheck", "ParentItem is null for postId: " + postId);
            return;
        }

        Long parentTime = parentItem.getTimeStamp();
        holder.postDate.setText(formatTimestamp(parentTime));
        holder.parentName.setText(parentItem.getParentName());
        Glide.with(holder.itemView.getContext()).load(parentItem.getParentImage())
                .into(holder.parentImage);

//        holder.postDate.setText(formatTimestamp(parentItem.getTimeStamp()));
        // Retrieve the timestamp and add logs
//        Object timeStamp = parentItem.getTimeStamp();
//        Log.d("TimestampCheck", "Timestamp type: " + (timeStamp == null ? "null" : timeStamp.getClass().getName()));
//        Log.d("TimestampCheck", "Timestamp value: " + timeStamp);
//
////        holder.postDate.setText(timestampToString((Long)parentItem.getTimeStamp()));
//        // Handle timestamp conversion and display
//        if (timeStamp != null) {
//            if (timeStamp instanceof Long) {
//                long timeStampLong = (Long) timeStamp;
//                holder.postDate.setText(timestampToString(timeStampLong) + " yes");
//            } else if (timeStamp instanceof Map) {
//                // Handle ServerValue.TIMESTAMP placeholder
//                Map<String, String> timeStampMap = (Map<String, String>) timeStamp;
//                if (timeStampMap.containsKey(".sv")) {
//                    holder.postDate.setText("Pending date");
//                } else {
//                    holder.postDate.setText("Invalid date");
//                }
//            } else {
//                Log.e("TimestampCheck", "Unsupported timestamp type: " + timeStamp.getClass().getName());
//                holder.postDate.setText("Invalid date");
//            }
//        } else {
//            holder.postDate.setText("Unknown date");
//        }

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
                        String imageUrl = userObject.getImageUrl();
                        holder.parentUser.setText(userid);
                        Glide.with(holder.itemView.getContext())
                                .load(imageUrl)
                                .transform(new CircleCrop()) // Apply the CircleCrop transformation
                                .skipMemoryCache(true) // Disable memory cache
                                .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable disk cache
                                .into(holder.userImage);
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

        getNoComment(postId, new CommentCountCallback() {
            @Override
            public void onCommentCountRetrieved(int count) {
                holder.commentNo.setText(String.valueOf(count));
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


    private String formatTimestamp(Long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }
    public void getNoComment(String postId, CommentCountCallback callback) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference commentReference = firebaseDatabase.getReference("Comment").child(postId);

        commentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get the number of comments
                long commentCount = snapshot.getChildrenCount();
                Log.d("CommentCount", "Number of comments: " + commentCount);

                // Use the callback to return the count
                callback.onCommentCountRetrieved((int) commentCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("CommentCount", "Failed to read comments.", error.toException());
            }
        });
    }


    public class ParentViewHolder extends RecyclerView.ViewHolder{
        private String postId, creatorUid, currentUid;
        private TextView parentName, parentUser, commentNo, postDate;
        private ImageView parentImage, userImage;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            parentName = itemView.findViewById(R.id.eachParentName);
            parentUser = itemView.findViewById(R.id.eachParentUser);
            parentImage = itemView.findViewById(R.id.eachParentIV);
            userImage = itemView.findViewById(R.id.profileImage);
            commentNo = itemView.findViewById(R.id.commentNo);
            postDate = itemView.findViewById(R.id.postDate);
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
