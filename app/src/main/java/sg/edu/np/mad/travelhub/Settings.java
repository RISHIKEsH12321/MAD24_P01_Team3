package sg.edu.np.mad.travelhub;

import static android.content.ContentValues.TAG;

import android.app.AlarmManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;

public class Settings extends AppCompatActivity {
    private DatabaseReference databaseReference;

    public static final String ACTION_REQUEST_SCHEDULE_EXACT_ALARM = "android.settings.REQUEST_SCHEDULE_EXACT_ALARM";
    public static final String POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS";
    private static final String ACTION_APP_NOTIFICATION_SETTINGS = "android.settings.APP_NOTIFICATION_SETTINGS";

    public static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 1;
    ImageView backButton;
    Button deleteaccButton, epBtn;

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
        epBtn = findViewById(R.id.epButton);
        TextView themesheader = findViewById(R.id.themesheader);
        TextView mpnheader = findViewById(R.id.mpnHeader);
        TextView baheader = findViewById(R.id.baHeader);
        TextView epheader = findViewById(R.id.epHeader);
        SwitchMaterial mpnbtn = findViewById(R.id.mpnButton);
        SwitchMaterial babtn = findViewById(R.id.baButton);
        backButton = findViewById(R.id.backButton);
        deleteaccButton = findViewById(R.id.btnDeleteAcc);
        Button btnLogout = findViewById(R.id.btnLogout);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currencies, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> themeadapter = ArrayAdapter.createFromResource(this,
                R.array.themes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner themesSpinner = findViewById(R.id.colour_themes);
        themesSpinner.setAdapter(themeadapter);
        SharedPreferences preferences = getSharedPreferences("spinner_preferences", MODE_PRIVATE);
        int selectedPosition = preferences.getInt("selected_spinner_position", 0);
        themesSpinner.setSelection(selectedPosition);

        themesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Use Shared Preference to save the Spinner setting
                SharedPreferences preferences = getSharedPreferences("spinner_preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("selected_spinner_position", position);
                editor.apply();

                // Get input from Spinner
                String selectedTheme = parent.getItemAtPosition(position).toString();

                // Apply the selected theme
                getTheme().applyStyle(R.style.Base_Theme_TravelHub, true);
                int color1;
                int color2;
                int color3;
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
                //Get IDs


                //Change Colors
                epBtn.setBackgroundTintList(ColorStateList.valueOf(color1));
                themesheader.setTextColor(color1);
                mpnheader.setTextColor(color1);
                baheader.setTextColor(color1);
                epheader.setTextColor(color1);
                mpnbtn.setThumbTintList(ColorStateList.valueOf(color1));
                babtn.setThumbTintList(ColorStateList.valueOf(color1));
                ColorStateList colorStateList = ColorStateList.valueOf(color1);
                backButton.setImageTintList(colorStateList);
                btnLogout.setBackgroundColor(color2);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });





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
        boolean baState = sharedPreferences.getBoolean("ba", false);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();



        SwitchMaterial mpnButton =findViewById(R.id.mpnButton);
        SwitchMaterial baButton =findViewById(R.id.baButton);
//        mpnButton.setChecked(sharedPreferences.getBoolean("mpn", false));
        baButton.setChecked(sharedPreferences.getBoolean("ba", false));

        //Check for permissions and set accordingly
        setmpnCheck(mpnButton);

        myEdit.putBoolean("mpn", mpnState);
        myEdit.putBoolean("ba", baState);

        deleteaccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                DatabaseReference userListingRef = databaseReference.child(currentuid);
                userListingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
                            currentuser.delete();
                            //delete the user listing
                            userListingRef.removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Settings.this, "User deleted successfully.", Toast.LENGTH_SHORT).show();
                                    Intent backToLogin = new Intent(Settings.this, Register.class);
                                    startActivity(backToLogin);
                                } else {
                                    Toast.makeText(Settings.this, "Failed to delete user.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(Settings.this, "No listing found for the current user.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Error checking user listing", error.toException());
                        Toast.makeText(Settings.this, "Error checking user listing.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        //go to editProfile pg
        epBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfile = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(editProfile);
            }
        });

        mpnButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putBoolean("mpn", isChecked);
                Log.d(TAG, "isChecked: " + isChecked);
                Log.d(TAG, "sharedPreferences: " + sharedPreferences.getBoolean("mpn", false));

                mpnButton.setChecked(isChecked);
                myEdit.apply();
                handleMpnBtn(isChecked);
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



        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(Login.Shared_Preferences, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isProfileComplete", false);
                editor.putBoolean("remember_me", false);
                editor.apply();
                logoutUser();
            }
        });

    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void setmpnCheck(SwitchMaterial mpnButton) {
//        boolean hasAlarmPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).canScheduleExactAlarms();
//        boolean hasNotificationPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || hasNotificationPermission(this);
//        SharedPreferences.Editor myEdit = sharedPreferences.
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean savedPer = sharedPreferences.getBoolean("mpn", false);

        boolean bothPermissions = hasAlarmPermissions() && hasNotificationPermission(Settings.this);
        if (bothPermissions && savedPer){
            mpnButton.setChecked(bothPermissions);
        }else{
            mpnButton.setChecked(false);
        }
    }

    public boolean hasAlarmPermissions(){
        boolean per = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            per = alarmManager.canScheduleExactAlarms();
        }
        return per;
    }

    private void getAlarmPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent requestPermissionIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                try {
                    startActivity(requestPermissionIntent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(Settings.this, "No app can handle this request", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public boolean hasNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // No need to check for older versions
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFICATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
                // Optionally, guide user to notification settings
                Toast.makeText(this, "Turn on Notification Permission in Mobile Settings", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void handleMpnBtn(boolean isChecked){
        DatabaseHandler db = new DatabaseHandler(Settings.this, null, null, 1);

        //If checked ask for permissions and schedule reminders/notifications
        if (isChecked) {
            getAlarmPermissions();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!hasNotificationPermission(Settings.this)) {
                    requestNotificationPermission();
                }
            }
            try {
                db.scheduleNotification(Settings.this);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            //If not checked, delete all reminders
            db.cancelAllReminders(Settings.this);
        }
    }


}
