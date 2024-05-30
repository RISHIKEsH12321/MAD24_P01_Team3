package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Smain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backtoProfile = new Intent(getApplicationContext(), Profile.class);
                startActivity(backtoProfile);
            }
        });

        // Storing data into SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("settings",MODE_PRIVATE);

// Creating an Editor object to edit(write to the file)
        boolean mpnState = sharedPreferences.getBoolean("mpn", false);
        boolean asState = sharedPreferences.getBoolean("as", false);
        boolean baState = sharedPreferences.getBoolean("ba", false);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        Button epButton =findViewById(R.id.epButton);

        SwitchMaterial mpnButton =findViewById(R.id.mpnButton);
        SwitchMaterial baButton =findViewById(R.id.baButton);
        SwitchMaterial asButton =findViewById(R.id.asButton);
        mpnButton.setChecked(sharedPreferences.getBoolean("mpn", false));
        asButton.setChecked(sharedPreferences.getBoolean("as", false));
        baButton.setChecked(sharedPreferences.getBoolean("ba", false));

        myEdit.putBoolean("mpn", mpnState);
        myEdit.putBoolean("as", asState);
        myEdit.putBoolean("ba", baState);
        epButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfile = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(editProfile);
            }
        });

        mpnButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myEdit.putBoolean("mpn", isChecked);
                myEdit.apply();
            }
        });

        asButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myEdit.putBoolean("as", isChecked);
                myEdit.apply();
            }
        });

        baButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myEdit.putBoolean("ba", isChecked);
                myEdit.apply();
            }
        });

        myEdit.commit();



    }
}