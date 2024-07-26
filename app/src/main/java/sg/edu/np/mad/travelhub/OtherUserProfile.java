package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
    int color1;
    int color2;
    int color3;
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

        SharedPreferences preferences = getSharedPreferences("spinner_preferences", MODE_PRIVATE);
        int selectedSpinnerPosition = preferences.getInt("selected_spinner_position", 0);
        String selectedTheme = getResources().getStringArray(R.array.themes)[selectedSpinnerPosition];

        switch (selectedTheme) {
            case "Default":
                color1 = getResources().getColor(R.color.main_orange);
                color2 = getResources().getColor(R.color.main_orange);
                color3 = getResources().getColor(R.color.main_orange_bg);
                break;
            case "Watermelon":
                color1 = getResources().getColor(R.color.wm_green);
                color2 = getResources().getColor(R.color.wm_red);
                color3 = getResources().getColor(R.color.wm_red_bg);
                break;
            case "Neon":
                color1 = getResources().getColor(R.color.nn_pink);
                color2 = getResources().getColor(R.color.nn_cyan);
                color3 = getResources().getColor(R.color.nn_cyan_bg);
                break;
            case "Protanopia":
                color1 = getResources().getColor(R.color.pro_purple);
                color2 = getResources().getColor(R.color.pro_green);
                color3 = getResources().getColor(R.color.pro_green_bg);
                break;
            case "Deuteranopia":
                color1 = getResources().getColor(R.color.deu_yellow);
                color2 = getResources().getColor(R.color.deu_blue);
                color3 = getResources().getColor(R.color.deu_blue_bg);
                break;
            case "Tritanopia":
                color1 = getResources().getColor(R.color.tri_orange);
                color2 = getResources().getColor(R.color.tri_green);
                color3 = getResources().getColor(R.color.tri_green_bg);
                break;
            default:
                color1 = getResources().getColor(R.color.main_orange);
                color2 = getResources().getColor(R.color.main_orange);
                color3 = getResources().getColor(R.color.main_orange_bg);
                break;
        }

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
                finish();
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
                    User userObject = snapshot.getValue(User.class);
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


        //fragments at the bottom
        Button favoritesBtn = findViewById(R.id.favoritesHeader);
        Button postsBtn = findViewById(R.id.postsHeader);
        ArrayList<Button> btnList = new ArrayList<Button>();
        btnList.add(favoritesBtn);
        btnList.add(postsBtn);
        enableFilterBtn(favoritesBtn, null);
        currentActiveBtn = favoritesBtn;
        replaceFragment(new Favorites(userUid));

        for (Button btn : btnList) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btn == favoritesBtn){
                        replaceFragment(new Favorites(userUid));
                    } else{
                        replaceFragment(Posts.newInstance(userUid));
                    }
                    if(!(currentActiveBtn == btn)){
                        enableFilterBtn(btn, currentActiveBtn);
                        currentActiveBtn = btn;
                    }
                }
            });


        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.profileFrameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void enableFilterBtn(Button activatedBtn, @Nullable Button deactivatedBtn){
        activatedBtn.setTextColor(getResources().getColor(R.color.selectedFilterText));
        activatedBtn.setBackgroundColor(color1);

        if (deactivatedBtn != null){
            deactivatedBtn.setTextColor(getResources().getColor(R.color.unselectedFilterText));
            deactivatedBtn.setBackgroundColor(getResources().getColor(R.color.unselectedFilterBackground));
        }
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