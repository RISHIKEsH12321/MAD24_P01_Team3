package sg.edu.np.mad.travelhub;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SearchUser extends AppCompatActivity {

    DatabaseReference ref;
    String displayStr;
    RecyclerView recyclerView;
    SearchView searchBar;
    List<User> usersList;
    ValueEventListener eventListener;
    private Loading_Dialog loadingDialog;
    boolean isFollowing;
    UserAdapter adapter;
    FrameLayout rvFrameLayout;
    Button currentActiveBtn;
    int color1;
    int color2;
    int color3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
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

        // Bottom Navigation View Logic to link to the different master activities
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavMenu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_searchUserOrPost);
        bottomNavigationView.setOnApplyWindowInsetsListener(null);
        bottomNavigationView.setPadding(0,0,0,0);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home){
                startActivity(new Intent(this, HomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                finish(); // Finish current activity if going back to HomeActivity
                return true;
            } else if (item.getItemId() == R.id.bottom_calendar){
                startActivity(new Intent(this, ViewEvents.class));
                finish();
            } else if (item.getItemId() == R.id.bottom_currency) {
                startActivity(new Intent(this, ConvertCurrency.class));
                finish();
            } else if (item.getItemId() == R.id.bottom_profile) {
                startActivity(new Intent(this, Profile.class));
                finish();
            }
            return true;
        });

        rvFrameLayout = findViewById(R.id.recyclerviewFrameLayout);

        //switching between fragments
        Button usersBtn = findViewById(R.id.usersBtn);
        Button postsBtn = findViewById(R.id.postsBtn);
        //bundle so that fragment can receive the argument
        ArrayList<Button> btnList = new ArrayList<Button>();
        btnList.add(usersBtn);
        btnList.add(postsBtn);
        enableFilterBtn(usersBtn, null);
        currentActiveBtn = usersBtn;
        Fragment fragment = new usersearch();
        replaceFragment(fragment);


        for (Button btn : btnList) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btn == usersBtn){
                        Fragment fragment = new usersearch();
                        replaceFragment(fragment);
                    }
                    else{
                        Fragment fragment = new postsearch();
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
        fragmentTransaction.replace(R.id.recyclerviewFrameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void enableFilterBtn(Button activatedBtn, @Nullable Button deactivatedBtn){
        activatedBtn.setTextColor(getResources().getColor(R.color.selectedFilterText));
        activatedBtn.setBackgroundColor(getResources().getColor(R.color.main_orange));

        if (deactivatedBtn != null) {
            deactivatedBtn.setTextColor(getResources().getColor(R.color.unselectedFilterText));
            deactivatedBtn.setBackgroundColor(getResources().getColor(R.color.unselectedFilterBackground));
        }
    }
}