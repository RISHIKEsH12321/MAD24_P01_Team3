package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileStats extends AppCompatActivity {

    Button currentActiveBtn;
    ImageButton backButton;

    TextView userIdHeader;

    DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_stats);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //define views
        userIdHeader = findViewById(R.id.userIdHeader);
        backButton = findViewById(R.id.backButton);


        //get string extra
        Intent intent = getIntent();
        String userUid = intent.getStringExtra("userUid");
        String startingFragment = intent.getStringExtra("startingFragment");

        //set logic for back button first to go back to search
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent goBack = new Intent(ProfileStats.this, OtherUserProfile.class);
//                goBack.putExtra("userUid", userUid);
//                startActivity(goBack);
                finish();
            }
        });

        //get user Id to put in header
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userUid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User userObject = snapshot.getValue(User.class);
                    userIdHeader.setText(userObject.getId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //switching between fragments
        Button followersBtn = findViewById(R.id.followerButton);
        Button followingBtn = findViewById(R.id.followingButton);
        //bundle so that fragment can receive the argument
        ArrayList<Button> btnList = new ArrayList<Button>();
        Bundle bundle = new Bundle();
        bundle.putString("userUid", userUid );
        btnList.add(followersBtn);
        btnList.add(followingBtn);
        if (Objects.equals(startingFragment, "followers")) {
            enableFilterBtn(followersBtn, null);
            currentActiveBtn = followersBtn;
            Fragment fragment = new Followers();
            fragment.setArguments(bundle);
            replaceFragment(fragment);
        }
        else {
            enableFilterBtn(followingBtn, null);
            currentActiveBtn = followingBtn;
            Fragment fragment = new Following();
            fragment.setArguments(bundle);
            replaceFragment(fragment);
        }

        for (Button btn : btnList) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btn == followersBtn){
                        Fragment fragment = new Followers();
                        fragment.setArguments(bundle);
                        replaceFragment(fragment);
                    }
                    else{
                        Fragment fragment = new Following();
                        fragment.setArguments(bundle);
                        replaceFragment(fragment);
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
        fragmentTransaction.replace(R.id.statsFrameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void enableFilterBtn(Button activatedBtn, @Nullable Button deactivatedBtn){
        activatedBtn.setTextColor(getResources().getColor(R.color.selectedFilterText));
        activatedBtn.setBackgroundColor(getResources().getColor(R.color.main_orange));

        if (deactivatedBtn != null){
            deactivatedBtn.setTextColor(getResources().getColor(R.color.unselectedFilterText));
            deactivatedBtn.setBackgroundColor(getResources().getColor(R.color.unselectedFilterBackground));
        }
    }
}