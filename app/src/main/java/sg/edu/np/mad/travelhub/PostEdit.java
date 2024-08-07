package sg.edu.np.mad.travelhub;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PostEdit extends AppCompatActivity implements ChildMainAdapter.OnChildMainInteractionListener, OnImageClickListener.Listener {

    private String postId;
    private FirebaseViewModel firebaseViewModel;
    private RecyclerView childMainRecyclerView;
    private ChildMainAdapter childMainAdapter;
    private FloatingActionButton btnAddChild, btnCreate;

    private AppCompatTextView postName;
    private AppCompatImageView postImage;
    //private RecyclerView postRecyclerView;
    private ParentItem parentItem;
    private ChildMain childMain;


    private EditText etName, etDescription;
    private TextView tvName, tvDescription, tvUser;


    // merge
    private DatabaseReference databaseReference;
    private AppCompatTextView actvName;

    private Button btnComment;
    private AppCompatButton btnBack, menu;

    private ImageView profileImage;
    //image
    private List<ChildMain> mainList;
    private String downloadUrl;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> getResult;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private int childMainPosition;
    private int childItemPosition;
    private final Loading_Dialog loadingDialog = new Loading_Dialog(PostEdit.this);

    int color1;
    int color2;
    int color3;

    private void handleImageClick(int mainPosition, int itemPosition) {
        this.childMainPosition = mainPosition;
        this.childItemPosition = itemPosition;

        // Launch image picker intent
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(intent);
    }
    @Override
    public void onImageClick(int mainPosition, int itemPosition) {
        handleImageClick(mainPosition, itemPosition);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_edit);
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

        // Register the activity result launcher
        getResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // Handle the Intent result here
                    imageUri = data.getData();
                    postImage.setImageURI(imageUri);
                    Log.d("IMAGEURI", String.valueOf(imageUri));
                    // Now that you have the image URI, you can proceed with uploading
                    uploadToFirebase(postId, imageUri);
                }
            }
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        // Notify the adapter of the selected image URI
                        updateChildAdapterWithImage(childMainPosition, childItemPosition, selectedImageUri);
                    }
                }
        );
        //popup menu
        menu = findViewById(R.id.PObtnMenu);
        registerForContextMenu(menu);

        //PostId intent from postlist
        Intent intentFromPost = getIntent();
        postId = intentFromPost.getStringExtra("postId");

        tvUser = findViewById(R.id.POtvUser);
        profileImage = findViewById(R.id.POivUserImage);

        tvName = findViewById(R.id.POtvName);
        etName = findViewById(R.id.POetName);

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvName.setVisibility(View.INVISIBLE);
                etName.setVisibility(View.VISIBLE);
                etName.requestFocus();
            }
        });

        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    tvName.setText(etName.getText());
                    tvName.setVisibility(View.VISIBLE);
                    etName.setVisibility(View.GONE);
                    updateName(String.valueOf(etName.getText()));
                }
            }
        });

        postImage = findViewById(R.id.POacivPostImage);

//        childMainRecyclerView = findViewById(R.id.childMainRecyclerView);

        //Desc et and tv
        etDescription = findViewById(R.id.POetDescription);
        tvDescription = findViewById(R.id.POtvDescription);

        tvDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDescription.setVisibility(View.INVISIBLE);
                etDescription.setVisibility(View.VISIBLE);
                etDescription.requestFocus();
            }
        });

        etDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    tvDescription.setText(etDescription.getText());
                    tvDescription.setVisibility(View.VISIBLE);
                    etDescription.setVisibility(View.GONE);
                    updateDescription(String.valueOf(etDescription.getText()));
                }
            }
        });

        //Recyclerview
        childMainRecyclerView = findViewById(R.id.POrvChildMainRecyclerView);
        childMainRecyclerView.setHasFixedSize(true);
        childMainRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Adapter
        childMainAdapter = new ChildMainAdapter(2, new OnImageClickListener.Listener() {
            @Override
            public void onImageClick(int mainPosition, int itemPosition) {
                handleImageClick(mainPosition, itemPosition);
            }
        }, childMainRecyclerView, mainList);
        childMainAdapter.setParentKey(postId);
        childMainAdapter.setOnChildMainInteractionListener(this);

        childMainRecyclerView.setAdapter(childMainAdapter);

        //childMainButton.setVisibility(View.INVISIBLE);
        //Firebase
        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        firebaseViewModel.getChildMainMutableLiveData().observe(this, new Observer<List<ChildMain>>() {
            @Override
            public void onChanged(List<ChildMain> childMainList) {
                mainList = childMainList;
                childMainAdapter.setChildMainList(childMainList);
                //Log.d("CHILDMAINADAPTER_SIZE", String.valueOf(childMainAdapter.getChildMainList().get(0).getChildMainName()));
                childMainAdapter.notifyDataSetChanged();
            }
        });
        firebaseViewModel.getDatabaseErrorMutableLiveData().observe(this, new Observer<DatabaseError>() {
            @Override
            public void onChanged(DatabaseError error) {
                Toast.makeText(PostEdit.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        firebaseViewModel.getAllChildMainData(postId);

        //Get post name, etc...
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Posts");
        databaseReference.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    parentItem = snapshot.getValue(ParentItem.class);
                    tvName.setText(parentItem.getParentName());
                    tvDescription.setText(parentItem.getParentDescription());
                    if (postImage != null) {
                        Glide.with(getApplicationContext())
                                .load(parentItem.getParentImage())
                                .into(postImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Initialise Button
        btnAddChild = findViewById(R.id.POfabAddChild);
        btnAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childMainAdapter.addChildMain();
                // Scroll to the newly added item
                childMainRecyclerView.scrollToPosition(childMainAdapter.getItemCount() - 1);
            }
        });

        //comment btn
        btnComment = findViewById(R.id.btnComment);
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommentSection.class);
                intent.putExtra("postId", postId);
                startActivity(intent);
            }
        });

        //back btn
        btnBack = findViewById(R.id.PObtnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        DatabaseReference userRef;
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        loadingDialog.startLoadingDialog();  // Show loading dialog
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingDialog.dismissDialog();  // Show loading dialog
                if (snapshot.exists()) {
                    User userObject = snapshot.getValue(User.class); // Assuming your User class exists
                    if (userObject != null) {
                        String userid = userObject.getName();
                        String imageUrl = userObject.getImageUrl();
                        Glide.with(PostEdit.this)
                                .load(imageUrl)
                                .transform(new CircleCrop()) // Apply the CircleCrop transformation
                                .skipMemoryCache(true) // Disable memory cache
                                .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable disk cache
                                .into(profileImage);
                        tvUser.setText(userid);

                        // Update UI elements with retrieved name and description
                    } else {
                        Log.w("TAG", "User object not found in database");
                    }
                } else {
                    Log.d("TAG", "No user data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismissDialog();  // Show loading dialog
                Log.w("TAG", "Error retrieving user data", error.toException());
            }
        });

        ColorStateList colorStateList = ColorStateList.valueOf(color1);
        btnBack.setBackgroundTintList(colorStateList);
        menu.setBackgroundTintList(colorStateList);
        btnAddChild.setBackgroundTintList(colorStateList);
        btnComment.setBackgroundTintList(colorStateList);
    }

    private void updateChildAdapterWithImage(int mainPosition, int itemPosition, Uri imageUri) {
        RecyclerView recyclerView = findViewById(R.id.POrvChildMainRecyclerView);
        ChildMainAdapter adapter = (ChildMainAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.updateChildItemImage(mainPosition, itemPosition, imageUri);
            // Ensure that the save button is visible after the image is selected
            ChildMainAdapter.PostEditViewholder viewHolder = (ChildMainAdapter.PostEditViewholder) recyclerView.findViewHolderForAdapterPosition(mainPosition);
            if (viewHolder != null) {
                Log.d("doesthisrun", "hello");
                viewHolder.updateButtonVisibility(adapter.getChildMainList().get(mainPosition)); // Update button visibility based on the edit state
            }
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_post, menu);;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.uploadImage) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            getResult.launch(intent);
        }
        if (item.getItemId() == R.id.delete) {
            deletePost();
            finish();
        }
        return true;
    }

    public void onSaveButtonClick(ChildMain childMain) {
        saveChildMainData(childMain);
    }

    private void saveChildMainData(ChildMain childMain) {
        //update childmainname


        //update childitem
        DatabaseReference childMainRef = databaseReference.child(postId).child("childData").child(childMain.getKey());
        List<ChildItem> childData = childMain.getChildItemList();
        childMainRef.setValue(childMain).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    childMainRef.child("childItemList").setValue(childData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "ChildData updated successfully");
                            } else {
                                Log.e("TAG", "Error updated ChildData", task.getException());
                            }
                        }
                    });
                } else {
                    Log.e("TAG", "Error updating childdata", task.getException());
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void uploadToFirebase(String uid, Uri imUri) {
        loadingDialog.startLoadingDialog();
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postId);

        // Fetch the existing image URL from the database
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ParentItem parentItem = snapshot.getValue(ParentItem.class);
                    String existingImageUrl = parentItem.getParentImage();

                    // If an existing image URL is found, delete the existing image from Firebase Storage
                    if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                        StorageReference existingImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(existingImageUrl);
                        existingImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                loadingDialog.dismissDialog();
                                // Existing image deleted, proceed to upload the new image
                                Log.d("Upload", "Existing image deleted");
                                performUpload(uid, imUri);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingDialog.dismissDialog();
                                // Handle failure in deleting the existing image
                                Log.e("Upload", "Failed to delete existing image", e);
                            }
                        });
                    } else {
                        // No existing image found, proceed to upload the new image
                        performUpload(uid, imUri);
                        loadingDialog.dismissDialog();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database read error
                Log.e("Upload", "Database error: " + error.getMessage());
            }
        });
    }

    private void performUpload(String uid, Uri imUri) {
        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(postId).child(postId + "." + getFileExtension(imUri));
        fileRef.putFile(imUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadUrl = uri.toString();
                                Log.d("IMAGEURL", downloadUrl);

                                // Update the image URL in the database
                                DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postId);

                                postRef.child("parentImage").setValue(downloadUrl);

                                // Dismiss the loading dialog
                                Toast.makeText(getApplicationContext(), "Image successfully uploaded", Toast.LENGTH_SHORT).show();
                            }
                        });
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

    private void deletePost() {
        //delete image from storage
        String existingImageUrl = parentItem.getParentImage();

        // If an existing image URL is found, delete the existing image from Firebase Storage
        if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
            StorageReference existingImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(existingImageUrl);
            existingImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Existing image deleted, proceed to upload the new image
                    Log.d("Upload", "Existing image deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle failure in deleting the existing image
                    Log.e("Upload", "Failed to delete existing image", e);
                }
            });
        }

        //delete from realtime db
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts").child(parentItem.getParentKey());
        postRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Post deleted successfully
                    Toast.makeText(PostEdit.this, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                    // Optionally, you can finish the activity or navigate to another screen
                    finish();
                } else {
                    // Failed to delete post
                    Toast.makeText(PostEdit.this, "Failed to delete post", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private String getFileExtension(Uri imUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(imUri));
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        // Reset expandable state for all ChildMain items in the adapter
//        if (childMainAdapter != null) {
//            for (ChildMain childMain : childMainAdapter.getChildMainList()) {
//                childMain.setExpandable(false);
//            }
//            childMainAdapter.notifyDataSetChanged();
//        }
//    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        childMainAdapter.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        childMainAdapter.onRestoreInstanceState(savedInstanceState);
//    }
    private void updateName(String postName) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("parentName", postName);

        // Update the node with the new name
        postRef.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Update successful
                    Log.d("UpdateName", "Post name updated successfully");
                } else {
                    // Update failed
                    Log.e("UpdateName", "Failed to update post name", task.getException());
                }
            }
        });
    }

    private void updateDescription(String postDescription) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("parentDescription", postDescription);

        // Update the node with the new name
        postRef.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Update successful
                    Log.d("UpdateName", "Post name updated successfully");
                } else {
                    // Update failed
                    Log.e("UpdateName", "Failed to update post name", task.getException());
                }
            }
        });
    }
    private void updateDescription() {

    }
    @Override
    protected void onPause() {
        super.onPause();

        // Log to confirm onPause is called
        Log.d("Post", "onPause called");

        // Reset expand state when the activity goes to the background
        childMainAdapter.resetExpandState();
    }
    @Override
    protected void onStop() {
        super.onStop();

        // Log to confirm onStop is called
        Log.d("Post", "onStop called");

        // Reset expand state when the activity is stopped
        childMainAdapter.resetExpandState();
    }
}

//where is updating of childitem list