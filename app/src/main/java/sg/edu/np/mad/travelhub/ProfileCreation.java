package sg.edu.np.mad.travelhub;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ProfileCreation extends AppCompatActivity {

    ImageView image;
    TextInputEditText etName, etDescription, etId;
    String name, description;
    Button btnSave, btnCreate;
    FirebaseUser fbuser;
    FirebaseDatabase db;
    DatabaseReference myRef;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    Uri imageUri;
    String downloadUrl, email, password, id;
    //User user;
    String uid;
    Boolean isDuplicate = false;
    private ActivityResultLauncher<Intent> getResult;
    public static final int PICK_IMAGE = 1;
    private final Loading_Dialog loadingDialog = new Loading_Dialog(ProfileCreation.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_creation);
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

        //initialise etId
        etId = findViewById(R.id.PCetId);

        //Get IDs
        Button register = findViewById(R.id.PCbtnCreate);

        //Change Colors
        register.setBackgroundTintList(ColorStateList.valueOf(color2));

        db = FirebaseDatabase.getInstance();
        myRef = db.getReference("Users");
        //get Firebase user
        fbuser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = fbuser.getUid(); //get uid of user

        // Register the activity result launcher
        getResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // Handle the Intent result here
                    imageUri = data.getData();
                    image.setImageURI(imageUri);
                    Log.d("IMAGEURI", String.valueOf(imageUri));
                    // Now that you have the image URI, you can proceed with uploading
                    // To start the loading dialog (keep in mind that this dialog doesn't allow them to get out so you must stop the loading dialog)
                    loadingDialog.startLoadingDialog();
                    uploadToFirebase(uid, imageUri);
                }
            }
        });

        StorageReference imagesRef = storageRef.child("images");
        image = findViewById(R.id.PCivImage);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getResult.launch(intent);
            }
        });


        //Textwatcher to ensure Id entered is unique
        etId.addTextChangedListener(new TextWatcher() {
            private Handler handler = new Handler(Looper.getMainLooper());
            private Runnable workRunnable;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                TextInputLayout idLayout = findViewById(R.id.PCBoxID);
                Context context = ProfileCreation.this;
                Drawable cancelDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_done, context.getTheme());
                idLayout.setEndIconDrawable(cancelDrawable);
                idLayout.setEndIconTintList(ColorStateList.valueOf(Color.GREEN));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Remove any pending posts of workRunnable
                if (workRunnable != null) {
                    handler.removeCallbacks(workRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                final String id = s.toString().trim();
                Log.d("TextWatcher", "Email entered: " + email);
                workRunnable = new Runnable() {
                    @Override
                    public void run() {
                        validateId(id, new ProfileCreation.UserExistsCallback() {
                            @Override
                            public void onCallback(boolean exists) {
                                TextInputLayout idLayout = findViewById(R.id.PCBoxID);
                                Context context = ProfileCreation.this;
                                if (exists) {
                                    Drawable cancelDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_cancel, context.getTheme());
                                    idLayout.setEndIconDrawable(cancelDrawable);
                                    idLayout.setEndIconTintList(ColorStateList.valueOf(Color.RED));
                                    Log.d("TextWatcher", "Email exists: " + email);
                                    isDuplicate = true;
                                } else {
                                    Drawable checkDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_done, context.getTheme());
                                    idLayout.setEndIconDrawable(checkDrawable);
                                    idLayout.setEndIconTintList(ColorStateList.valueOf(Color.GREEN));
                                    Log.d("TextWatcher", "Email does not exist: " + email);
                                    isDuplicate = false;
                                }
                            }
                        });
                    }
                };
                // Post the workRunnable with a delay to prevent rapid database queries
                handler.postDelayed(workRunnable, 500); // Delay of 500ms
            }
        });

        btnCreate = findViewById(R.id.PCbtnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get name and description
                etName = findViewById(R.id.PCetName);
                name = etName.getText().toString();
                etDescription = findViewById(R.id.PCetDescription);
                description = etDescription.getText().toString();
                id = etId.getText().toString();
                email = getIntent().getStringExtra("Email");
                password = getIntent().getStringExtra("Password");

                // Check if user entered name, description, id, and if image URL is not null
                if (name != null && description != null && id != null && downloadUrl != null) {
                    // Create a map to hold the updated fields
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("name", name);
                    updates.put("description", description);
                    updates.put("id", id);
                    updates.put("imageUrl", downloadUrl);

                    if (isDuplicate) {
                        Toast.makeText(ProfileCreation.this, "Duplicate Id entered", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Use updateChildren to update only the specified fields
                    myRef.child(uid).updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // In ProfileCreation activity after profile completion
                                SharedPreferences sharedPreferences = getSharedPreferences(Login.Shared_Preferences, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isProfileComplete", true);
                                editor.apply();

                                etName.setText("");
                                etDescription.setText("");
                                etId.setText("");
                                Toast.makeText(getApplicationContext(), "Profile successfully updated", Toast.LENGTH_SHORT).show();
                                // Go to profile page
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.e("Update", "Failed to update profile", task.getException());
                                Toast.makeText(getApplicationContext(), "Failed to update profile: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // Show a message indicating that information is missing
                    Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public interface UserExistsCallback {
        void onCallback(boolean value);
    }

    private void validateId(String id, final ProfileCreation.UserExistsCallback callback) {
        FirebaseDatabase databaseUser = FirebaseDatabase.getInstance();
        databaseUser.getReference("Users").orderByChild("id").equalTo(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean exists = dataSnapshot.exists();
                        Log.d("validateId", "Id exists in database: " + exists);
                        callback.onCallback(dataSnapshot.exists());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
    }

    private void uploadToFirebase(String uid, Uri imUri) {
        StorageReference fileRef = storageRef.child(uid + "." + getFileExtension(imUri));
        fileRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadUrl = uri.toString();
                                Log.d("IMAGEURL", downloadUrl);
                            }
                        });


                        // To dismiss the loading dialog:
                        loadingDialog.dismissDialog();
                        Toast.makeText(getApplicationContext(), "Image successfully uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle image upload failure
                        Log.e("Upload", "Failed to upload image", e);
                        Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private String getFileExtension(Uri imUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(imUri));
    }
}