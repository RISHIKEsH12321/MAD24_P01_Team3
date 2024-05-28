package sg.edu.np.mad.travelhub;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    Button currentActiveBtn;
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

        Button tripsBtn = findViewById(R.id.tripsHeader);
        Button journalBtn = findViewById(R.id.journalHeader);
        ArrayList<Button> btnList = new ArrayList<Button>();
        btnList.add(tripsBtn);
        btnList.add(journalBtn);
        enableFilterBtn(tripsBtn, null);
        currentActiveBtn = tripsBtn;


        for (Button btn : btnList) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btn == tripsBtn){
                        replaceFragment(new Trips());
                    }
                    else{
                        replaceFragment(new Journals());
                    }
                    if(!(currentActiveBtn == btn)){
                        enableFilterBtn(btn, currentActiveBtn);
                        currentActiveBtn = btn;
                    }
                }
            });
        }
//        tripsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Begin the transaction
//                replaceFragment(new Trips());
//            }
//        });
//
//        journalBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Begin the transaction
//                replaceFragment(new Journals());
//            }
//        });


    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.profileFrameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void enableFilterBtn(Button activatedBtn, @Nullable Button deactivatedBtn){
        activatedBtn.setTextColor(getResources().getColor(R.color.selectedFilterText));
        activatedBtn.setBackgroundColor(getResources().getColor(R.color.selectedFilterBackground));

        if (deactivatedBtn != null){
            deactivatedBtn.setTextColor(getResources().getColor(R.color.unselectedFilterText));
            deactivatedBtn.setBackgroundColor(getResources().getColor(R.color.unselectedFilterBackground));
        }
    }

}