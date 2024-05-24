package sg.edu.np.mad.travelhub;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;

public class ProfileCreation extends AppCompatActivity {

    ImageView image;
    TextInputEditText etName, etDescription;
    String name, description;
    Button btnSave, btnCreate;
    FirebaseUser fbuser;
    FirebaseDatabase db;
    DatabaseReference myRef;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    Uri imageUri;
    String downloadUrl;

    //sus
    //User user;
    String uid;
    private ActivityResultLauncher<Intent> getResult;
    public static final int PICK_IMAGE = 1;

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

        btnCreate = findViewById(R.id.PCbtnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get name and description
                etName = findViewById(R.id.PCetName);
                name = etName.getText().toString();
                etDescription = findViewById(R.id.PCetDescription);
                description = etDescription.getText().toString();

                //Check if user entered name and description
                if (!name.isEmpty() && !description.isEmpty()) {
                    //Create user class to store data
                    User user = new User(downloadUrl, name, description);
                    //build child
                    myRef.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                etName.setText("");
                                etDescription.setText("");
                                Toast.makeText(getApplicationContext(), "Successfully created", Toast.LENGTH_SHORT).show();
                                //Go to profile page
                                Intent intent = new Intent(getApplicationContext(), Profile.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.e("Save", "Failed to save user", task.getException());
                                Toast.makeText(getApplicationContext(), "Failed to create profile: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
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