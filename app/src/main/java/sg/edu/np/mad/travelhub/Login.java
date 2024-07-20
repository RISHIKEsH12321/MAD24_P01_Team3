package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;


public class Login extends AppCompatActivity{
    TextInputEditText etEmail, etPassword;
    Button btnLogin;
    FirebaseAuth mAuth;
    TextView tvRegister;
    CheckBox rememberMeCheckBox;
    public static final String Shared_Preferences = "SharedPreferences";
    private final Loading_Dialog loadingDialog = new Loading_Dialog(Login.this);


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean rememberMe = sharedPreferences.getBoolean("remember_me", false);
        boolean allowBiometric = sharedPreferences.getBoolean("ba", false);

        if (currentUser != null) {
            Log.d("LOGIN",
                    "currentUser: " + currentUser.getUid() +
                            ",\nrememberMe: " + rememberMe +
                            ",\nallowBiometric: " + allowBiometric
            );

            checkForExistingData(currentUser, new ProfileCheckCallback() {
                @Override
                public void onProfileCheckComplete(boolean isProfileComplete) {
                    Log.d("LOGIN", "onProfileCheckComplete: " + isProfileComplete);
                    if (isProfileComplete) {
                        if (allowBiometric) {
                            authenticateWithBiometrics();
                        } else {
                            proceedToHome();
                        }
                    } else {
                        proceedToCreateProfile();
                    }
                }
            });
        } else {
            Log.d("LOGIN", "No user currently signed in");
            // Handle the case where rememberMe is false or user is not logged in
            // For example, you might want to redirect to a login screen
            // loginUser();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences preferences = getSharedPreferences("spinner_preferences", MODE_PRIVATE);
        int selectedSpinnerPosition = preferences.getInt("selected_spinner_position", 0);
        String selectedTheme = getResources().getStringArray(R.array.themes)[selectedSpinnerPosition];

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
        Button login = findViewById(R.id.LbtnRegister);
        TextView title = findViewById(R.id.toptitle);

        //Change Colors
        login.setBackgroundTintList(ColorStateList.valueOf(color2));
        title.setTextColor(color1);

        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.LetEmail);
        etPassword = findViewById(R.id.LetPassword);
        tvRegister = findViewById(R.id.LtvRegisterHere);
        btnLogin = findViewById(R.id.LbtnRegister);
        //Remember me
//        rememberMe();

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                rememberMe();
                String email, password;
                email = String.valueOf(etEmail.getText());
                password = String.valueOf(etPassword.getText());

                loginUser();
//                authenticateWithBiometrics(email,password);
            }
        });
    }

    private void loginUser() {
        String email, password;
        email = String.valueOf(etEmail.getText());
        password = String.valueOf(etPassword.getText());

        if (TextUtils.isEmpty(email)){
            Toast.makeText(Login.this, "Enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(Login.this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the checkbox is checked
        rememberMeCheckBox = findViewById(R.id.rmbMeCheckBox); // Assuming your checkbox ID
        boolean isChecked = rememberMeCheckBox.isChecked();

        // Update shared preferences based on checkbox state
        SharedPreferences sharedPreferences = getSharedPreferences(Shared_Preferences, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("remember_me", isChecked);
        editor.apply();
        Log.d("LOGIN", "remember_me setting: " + sharedPreferences.getBoolean("remember_me", false));

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Login successful.",
                                    Toast.LENGTH_SHORT).show();
                            proceedToHome();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Authentication failed. Try Again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//    private void rememberMe() {
//        SharedPreferences sharedPreferences = getSharedPreferences(Shared_Preferences, MODE_PRIVATE);
////        boolean isRemembered = sharedPreferences.getBoolean("remember_me", false);
//        boolean isProfileComplete = sharedPreferences.getBoolean("isProfileComplete", false);
//
//        // Check if the checkbox is checked
//        CheckBox rememberMeCheckBox = findViewById(R.id.rmbMeCheckBox); // Assuming your checkbox ID
//        boolean isChecked = rememberMeCheckBox.isChecked();
//
//        // Update shared preferences based on checkbox state
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("remember_me", isChecked);
//        editor.apply();
//
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            if (isProfileComplete) {
//                Toast.makeText(getApplicationContext(), "Login successful.", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getApplicationContext(), ConvertCurrency.class);
//                startActivity(intent);
//                finish();
//            } else {
//                Toast.makeText(getApplicationContext(), "Complete your profile.", Toast.LENGTH_SHORT).show();
//
//            }
//        }
//    }

    private void showBiometricPrompt(BiometricCallback callback) {
        loadingDialog.dismissDialog();
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(Login.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                callback.onBiometricAuthenticationSuccessful();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                callback.onBiometricAuthenticationFailed();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setDescription("Please authenticate with your biometrics to continue")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void authenticateWithBiometrics() {
        showBiometricPrompt(new BiometricCallback() {
            @Override
            public void onBiometricAuthenticationSuccessful() {
                proceedToHome();
            }

            @Override
            public void onBiometricAuthenticationFailed() {
                Toast.makeText(getApplicationContext(), "Biometric authentication failed", Toast.LENGTH_SHORT).show();
//                loginUser();
            }
        });
    }
    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Login successful.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ViewEvents.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void proceedToHome() {
        Intent intent = new Intent(getApplicationContext(), ViewEvents.class);
        startActivity(intent);
        finish();
    }

    private void proceedToCreateProfile(){
        Intent intent = new Intent(getApplicationContext(), ProfileCreation.class);
        finish();
        startActivity(intent);

    }

    private void checkForExistingData(FirebaseUser user, ProfileCheckCallback callback) {
        // If user has signed up but yet to create profile, bring to ProfileCreation page
        loadingDialog.startLoadingDialog();
        FirebaseDatabase databaseRef = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = databaseRef.getReference().child("Users");
        Query query = usersRef.orderByChild("uid").equalTo(user.getUid()); // Assuming 'uid' is the child you want to order by

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isProfileComplete = snapshot.exists();
                callback.onProfileCheckComplete(isProfileComplete);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
                callback.onProfileCheckComplete(false); // Assuming false if error occurs
            }
        });
    }

    // Define a callback interface
    interface ProfileCheckCallback {
        void onProfileCheckComplete(boolean isProfileComplete);
    }


}
