package sg.edu.np.mad.travelhub;

//import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
//import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class Login extends AppCompatActivity {
    TextInputEditText etEmail, etPassword;
    Button btnLogin;
    FirebaseAuth mAuth;
    TextView tvRegister;

    //Test Input
//    banana123@gmail.com
//    banana123

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
//            Intent intent = new Intent(getApplicationContext(), SearchUser.class);
//            startActivity(intent);
//            finish();
            Log.d("currentUser", "currentUser: "+ currentUser.toString());
            Log.d("currentUser", "currentUser emil: "+ currentUser.getEmail());
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
                loginUser();
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
//        showBiometricPrompt(email, password);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Login successful.",
                                    Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            Intent intent = new Intent(getApplicationContext(), ViewEvents.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showBiometricPrompt(String email, String password) {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(Login.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Authentication successful", Toast.LENGTH_SHORT).show();
//                signInWithEmail(email, password);
                Intent intent = new Intent(getApplicationContext(), ViewEvents.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setDescription("Please authenticate with your biometrics to continue")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Login successful.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ViewEvents.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private User checkCurrentUserDetails(){
        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
        String email = sharedPreferences.getString("currentUserEmail", null);
        String password = sharedPreferences.getString("currentUserPassword", null);

        // Check if email or password is null or empty
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }

        User user = new User();
        user.email = email;
        user.password = password;
        // If both checks pass, return User Details
        return user;
    }

//    private boolean isValidEmail(String email) {
//        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//        return email.matches(emailPattern);
//    }

}
