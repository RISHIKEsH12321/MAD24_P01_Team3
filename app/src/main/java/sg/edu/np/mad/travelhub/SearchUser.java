package sg.edu.np.mad.travelhub;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

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
