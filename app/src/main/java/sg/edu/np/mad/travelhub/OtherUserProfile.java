package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OtherUserProfile extends AppCompatActivity {
    Button currentActiveBtn;
    String dbPath;
    FirebaseDatabase db;
    DatabaseReference myRef;
    private FirebaseUser firebaseUser;
    ImageView profilePic;
    ImageButton backButton;
    TextView name, description, followingCount, followerCount;
    Button followBtn, messageBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_other_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.OUPmain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //get current user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //define views
        profilePic = findViewById(R.id.profilePic);
        name = findViewById(R.id.nameHeader);
        description = findViewById(R.id.descriptionHeader);
        followBtn = findViewById(R.id.followButton);
        followingCount = findViewById(R.id.followingCount);
        followerCount = findViewById(R.id.followerCount);
        backButton = findViewById(R.id.backButton);
        messageBtn = findViewById(R.id.messageButton);

        //get intent extra
        Intent intent = getIntent();
        String userUid = intent.getStringExtra("userUid");

        //set logic for back button first to go back to search
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBack = new Intent(OtherUserProfile.this, SearchUser.class);
                startActivity(goBack);
            }
        });

        //set logic for message button to go to message page
        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent message = new Intent(OtherUserProfile.this, Message.class);
                message.putExtra("userUid", userUid);
                startActivity(message);
            }
        });

        //redirect to profilestats page when user presses followers or following
        followerCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileStats = new Intent(OtherUserProfile.this, ProfileStats.class);
                profileStats.putExtra("startingFragment", "followers");
                profileStats.putExtra("userUid", userUid);
                startActivity(profileStats);
            }
        });
        followingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileStats = new Intent(OtherUserProfile.this, ProfileStats.class);
                profileStats.putExtra("startingFragment", "following");
                profileStats.putExtra("userUid", userUid);
                startActivity(profileStats);
            }
        });



        //get reference to the user
        db = FirebaseDatabase.getInstance();
        dbPath = "Users/" + userUid;
        myRef = db.getReference(dbPath);
        //retrieve user info and set it
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User userObject = snapshot.getValue(User.class); // Assuming your User class exists
                    if (userObject != null) {
                        //set onclick for follow btn
                        followBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(followBtn.getText().toString().equals("Follow")) {
                                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                            .child("following").child(userObject.getUid()).setValue(true);
                                    FirebaseDatabase.getInstance().getReference().child("Follow").child(userObject.getUid())
                                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                                    Toast.makeText(OtherUserProfile.this, "User followed",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                            .child("following").child(userObject.getUid()).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("Follow").child(userObject.getUid())
                                            .child("followers").child(firebaseUser.getUid()).removeValue();
                                    Toast.makeText(OtherUserProfile.this, "User unfollowed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        //check if currently logged in is same as the profile he views. if so, hide the follow btn
                        if (userObject.getUid().equals(firebaseUser.getUid())) {
                            followBtn.setVisibility(View.GONE);
                            messageBtn.setVisibility(View.GONE);
                        }
                        String userName = userObject.getName();
                        name.setText(userName);
                        String userDesc = userObject.getDescription();
                        description.setText(userDesc);
                        Glide.with(getApplicationContext())
                                .load(userObject.getImageUrl())
                                .transform(new CircleCrop())
                                .into(profilePic);
                        isFollowing(userUid, followBtn);
                        countFollowersAndFollowing(userObject.getUid());

                        // Update UI elements with retrieved name and description
                    } else {
                        Log.w("UserNotFound", "User object not found in database");
                    }
                } else {
                    Log.d("NoUserDataFound", "No user data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Error retrieving user data", error.toException());
            }
        });

    }

    //check if user is following and change button
    private void isFollowing(String userUid, Button followBtn) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userUid).exists()) {
                    followBtn.setText("Unfollow");
                } else{
                    followBtn.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //show follower and following count
    private void countFollowersAndFollowing(String uid) {
        DatabaseReference ref = db.getReference();
        DatabaseReference userRef = ref.child("Follow").child(uid);
        DatabaseReference followersRef = userRef.child("followers");
        DatabaseReference followingRef = userRef.child("following");
        followersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int followerCount = 0;
                if (snapshot.exists()) {
                    followerCount = (int) snapshot.getChildrenCount();
                }
                TextView followersCount = findViewById(R.id.followerCount);
                followersCount.setText(String.valueOf(followerCount)); // Use String.valueOf for TextView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //errors
            }
        });

        followingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int followingCount = 0;
                if (snapshot.exists()) {
                    followingCount = (int) snapshot.getChildrenCount();
                }
                TextView followingCountTV = findViewById(R.id.followingCount);
                followingCountTV.setText(String.valueOf(followingCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //errors
            }
        });
    }
}