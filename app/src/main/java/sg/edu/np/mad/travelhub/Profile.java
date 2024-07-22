package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {
    String uid;
    Button currentActiveBtn;
    private Loading_Dialog loadingDialog;
    FirebaseUser fbuser;
    FirebaseDatabase db;
    DatabaseReference myRef;
    ImageView image;
    TextView id, followerCount, followingCount;
    ImageButton backBtn;
    private List<PlaceDetails> placeDetailsList;
    int color1;
    int color2;
    int color3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Pmain), (v, insets) -> {
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

        //Change color for Bottom NavBar
        BottomNavigationView bottomNavMenu = (BottomNavigationView) findViewById(R.id.bottomNavMenu);
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_selected},
                new int[]{} // default state
        };
        int[] colors = new int[]{
                color1,
                ContextCompat.getColor(this, R.color.unselectedNavBtn)
        };
        ColorStateList colorStateList = new ColorStateList(states, colors);
        bottomNavMenu.setItemIconTintList(colorStateList);
        loadingDialog = new Loading_Dialog(this);
        image = findViewById(R.id.profilePic);
        id = findViewById(R.id.usernameHeader);

        // Bottom Navigation View Logic to link to the different master activities
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavMenu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);
        bottomNavigationView.setOnApplyWindowInsetsListener(null);
        bottomNavigationView.setPadding(0,0,0,0);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_calendar){
                startActivity(new Intent(this, ViewEvents.class));
                overridePendingTransition(0, 0);
                finish();
            } else if (item.getItemId() == R.id.bottom_currency) {
                startActivity(new Intent(this, ConvertCurrency.class));
                overridePendingTransition(0, 0);
                finish();
            } else if (item.getItemId() == R.id.bottom_home) {
                startActivity(new Intent(this, HomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0, 0);
                finish(); // Finish current activity if going back to HomeActivity
                return true;
            }
            return true;
        });

        loadingDialog.startLoadingDialog();
        db = FirebaseDatabase.getInstance();
        myRef = db.getReference("Users");
        //get Firebase user
        fbuser = FirebaseAuth.getInstance().getCurrentUser();
        uid = fbuser.getUid(); //get uid of user
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        //retrieve user name
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User userObject = snapshot.getValue(User.class); // Assuming your User class exists
                    if (userObject != null) {
                        String userid = userObject.getName();
                        id.setText(userid);
                        countFollowersAndFollowing(userObject.getUid());
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

        // Initialize Firebase
        if (fbuser != null) {
            uid = fbuser.getUid();
            myRef = db.getReference("Favourites").child(uid);
        } else {
            Toast.makeText(getApplicationContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }

        //follower following fragments
        followerCount = findViewById(R.id.followerCount);
        followingCount = findViewById(R.id.followingCount);
        followerCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileStats = new Intent(Profile.this, ProfileStats.class);
                profileStats.putExtra("startingFragment", "followers");
                profileStats.putExtra("userUid", uid);
                startActivity(profileStats);
            }
        });
        followingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileStats = new Intent(Profile.this, ProfileStats.class);
                profileStats.putExtra("startingFragment", "following");
                profileStats.putExtra("userUid", uid);
                startActivity(profileStats);
            }
        });

        //settings button to go to settings page
        ImageView settingsBtn = findViewById(R.id.settingsButton);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(getApplicationContext(), Settings.class);
                startActivity(settings);
            }
        });

        //fragments at the bottom
        Button tripsBtn = findViewById(R.id.tripsHeader);
        Button postsBtn = findViewById(R.id.postsHeader);
        Button favoriteBtn = findViewById(R.id.favouritesHeader);
        ArrayList<Button> btnList = new ArrayList<Button>();
        btnList.add(tripsBtn);
        btnList.add(postsBtn);
        btnList.add(favoriteBtn);
        enableFilterBtn(tripsBtn, null);
        currentActiveBtn = tripsBtn;
        replaceFragment(new Trips());

        for (Button btn : btnList) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btn == tripsBtn){
                        replaceFragment(new Trips());
                    } else if(btn == favoriteBtn){
                        replaceFragment(new Favorites());
                    } else{
                        replaceFragment(new Posts());
                    }
                    if(!(currentActiveBtn == btn)){
                        enableFilterBtn(btn, currentActiveBtn);
                        currentActiveBtn = btn;
                    }
                }
            });


        }
        loadUserImage();
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

    private void loadUserImage() {
        myRef = db.getReference("Users");
        if (fbuser != null) {
            myRef.child(uid).child("imageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String imageUrl = snapshot.getValue(String.class);
                        loadImageIntoImageView(imageUrl);
                        loadingDialog.dismissDialog();
                    } else {
                        Toast.makeText(Profile.this, "No image found for user", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismissDialog();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Profile.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissDialog();
                }
            });
        }
    }

    private void loadImageIntoImageView(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .transform(new CircleCrop()) // Apply the CircleCrop transformation
                .skipMemoryCache(true) // Disable memory cache
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable disk cache
                .into(image);

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