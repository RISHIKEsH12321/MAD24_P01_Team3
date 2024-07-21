package sg.edu.np.mad.travelhub;

import static android.app.ProgressDialog.show;
import static android.widget.Toast.makeText;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PostCreation extends AppCompatActivity implements OnImageClickListener.Listener{

    private String postId;
    private FirebaseViewModel firebaseViewModel;
    private RecyclerView childMainRecyclerView;
    private ChildMainAdapter childMainAdapter;
    private FloatingActionButton btnAddChild, btnCreate;

    private AppCompatTextView postName;
    private AppCompatImageView postImage;
    //private RecyclerView postRecyclerView;
    private ParentItem parentItem;
    private ParentItem childMain;


    private EditText etName, etDescription;
    private TextView tvName, tvDescription, tvUser;

    //image
    private String downloadUrl;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> getResult;
    private final Loading_Dialog loadingDialog = new Loading_Dialog(PostCreation.this);
    private List<ChildMain> mainList;
    private int childMainPosition;
    private int childItemPosition;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
//    DatabaseReference ref;
//    AppCompatButton btnBack;
//    RecyclerView parentRView;
//    FloatingActionButton addChild;
//    List<PostChild> postChildList;
//    ValueEventListener eventListener;



    private void handleImageClick(int mainPosition, int itemPosition) {
        this.childMainPosition = mainPosition;
        this.childItemPosition = itemPosition;

        // Launch image picker intent
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(intent);
    }

    private void updateChildAdapterWithImage(int mainPosition, int itemPosition, Uri imageUri) {
        // Notify the adapter to update the specific child item with the selected image
        RecyclerView recyclerView = findViewById(R.id.POrvChildMainRecyclerView);
        ChildMainAdapter adapter = (ChildMainAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.updateChildItemImage(mainPosition, itemPosition, imageUri);
        }
    }

    @Override
    public void onImageClick(int mainPosition, int itemPosition) {
        handleImageClick(mainPosition, itemPosition);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_creation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
        AppCompatButton menu = findViewById(R.id.PObtnMenu);
        registerForContextMenu(menu);

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
                    // To start the loading dialog (keep in mind that this dialog doesn't allow them to get out so you must stop the loading dialog)
                    loadingDialog.startLoadingDialog();
                    uploadToFirebase(postId, imageUri);
                }
            }
        });

        //PostId intent from postlist
        postId = getIntent().getStringExtra("postId");

        //Init postname and postimage
        //postName = findViewById(R.id.POactvPostName);
        postImage = findViewById(R.id.POacivPostImage);
        childMainRecyclerView = findViewById(R.id.POrvChildMainRecyclerView);

        parentItem = new ParentItem();

        tvUser = findViewById(R.id.POtvUser);
        //tvUser

        //init userRef on top

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        DatabaseReference userRef;
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User userObject = snapshot.getValue(User.class); // Assuming your User class exists
                    if (userObject != null) {
                        String userid = userObject.getName();
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
                Log.w("TAG", "Error retrieving user data", error.toException());
            }
        });

        //Name et and tv
        etName = findViewById(R.id.POetName);
        tvName = findViewById(R.id.POtvName);

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
                }
            }
        });



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
                }
            }
        });

        //ChildMainView
        //childMain
        //childMainRecyclerView = findViewById(R.id.POrvChildMainRecyclerView);
//        childMainRecyclerView = findViewById(R.id.childMainRecyclerView);
//
//        //Recyclerview
        childMainRecyclerView.setHasFixedSize(true);
        childMainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        //Adapter
        mainList = new ArrayList<>();
        childMainAdapter = new ChildMainAdapter(1, new OnImageClickListener.Listener() {
            @Override
            public void onImageClick(int mainPosition, int itemPosition) {
                handleImageClick(mainPosition, itemPosition);
            }
        }, childMainRecyclerView, mainList);

        childMainRecyclerView.setAdapter(childMainAdapter);
        childMainAdapter.setChildMainList(new ArrayList<>());

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

        btnCreate = findViewById(R.id.POfabCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Map<String, Map<String, ChildItem>> aggregatedChildData = new HashMap<>();
//                for (int i = 0; i < childMainRecyclerView.getChildCount(); i++) {
//                    RecyclerView.ViewHolder viewHolder = childMainRecyclerView.findViewHolderForAdapterPosition(i);
//                    if (viewHolder instanceof ChildMainAdapter.PostCreationViewHolder) {
//                        ChildMainAdapter.PostCreationViewHolder childMainViewHolder = (ChildMainAdapter.PostCreationViewHolder) viewHolder;
//                        ChildMain childMain = childMainViewHolder.childMain;
//                        String childMainKey = "ChildMain" + i;
//                        aggregatedChildData.put(childMainKey, childMain.getChildData());
//                    }
//                }
                Map<String, ChildMain> aggregatedChildData = new HashMap<>();
                for (int i = 0; i < childMainRecyclerView.getChildCount(); i++){
                    RecyclerView.ViewHolder viewHolder = childMainRecyclerView.findViewHolderForAdapterPosition(i);
                    if (viewHolder instanceof ChildMainAdapter.PostCreationViewHolder) {
                        ChildMainAdapter.PostCreationViewHolder childMainViewholder = (ChildMainAdapter.PostCreationViewHolder) viewHolder;
                        ChildMain childMain = childMainViewholder.childMain;
                        aggregatedChildData.put("List"+i, childMain);
                        Log.d("ChildMainName", childMain.getChildMainName());
                    }
                }
                parentItem.setChildData(aggregatedChildData);
                addParentToFirebase(parentItem, postId);
            }
        });

        //Map<String, Map<String, ChildItem>> childMainMap;
        //childMainMap =
        // Bottom Navigation View Logic to link to the different master activities
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavMenu);
//        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
//
//        bottomNavigationView.setOnItemSelectedListener(item -> {
//            if (item.getItemId() == R.id.bottom_calendar){
//                startActivity(new Intent(this, ViewEvents.class));
//                overridePendingTransition(0, 0);
//                finish();
//            } else if (item.getItemId() == R.id.bottom_currency) {
//                startActivity(new Intent(this, ConvertCurrency.class));
//                overridePendingTransition(0, 0);
//                finish();
//            } else if (item.getItemId() == R.id.bottom_profile) {
//                // To be changed
//                startActivity(new Intent(this, PostCreation.class));
//                overridePendingTransition(0, 0);
//                finish();
//            }
//            return true;
//        });
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
            Intent intent = new Intent(this, PostList.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    //upload to database
    private void addParentToFirebase(ParentItem parentItem, String postId) {
        // Create ChildItem objects
        //ChildItem childItem1 = new ChildItem("ChildItem1 Name", "ChildItem1 ImageUrl");
        //ChildItem childItem2 = new ChildItem("ChildItem2 Name", "ChildItem2 ImageUrl");

        // Create nested maps for childData
        //Map<String, ChildItem> listOfItems = new HashMap<>();
        //listOfItems.put("ChildItem1", childItem1);
        //listOfItems.put("ChildItem2", childItem2);
        Log.d("ParentItem", "ChildData size: " + parentItem.getChildData().size());


        //Map<String, Map<String, ChildItem>> childData = parentItem.getChildData();
        //childData.put(childData.keySet(), childData);


        // Create a ParentItem object
        //ParentItem parentItem = new ParentItem("Parent Name", "Parent Image", childData);
        parentItem.setParentName(String.valueOf(etName.getText()));
        parentItem.setParentDescription(String.valueOf(etDescription.getText()));
        if (downloadUrl != null) {
            parentItem.setParentImage(downloadUrl);
        }
        else {
            parentItem.setParentImage("");
        }
//        parentItem.setParentKey(postId);
//        parentItem.setTimeStamp(ServerValue.TIMESTAMP);
        //Get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        parentItem.setParentUser(uid);

        // Get the FirebaseRepo instance and add the new ParentItem
//        FirebaseRepo firebaseRepo = new FirebaseRepo(new FirebaseRepo.OnRealtimeDbTaskComplete() {
//            @Override
//            public void onSuccess(Map<String, ParentItem> parentItemList) {
//                // Handle success if needed
//                makeText(PostCreation.this, "ParentItem added successfully", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(DatabaseError error) {
//                // Handle failure if needed
//                makeText(PostCreation.this, "Error adding ParentItem: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onSuccessChildMain(List<ChildMain> childMainList) {
//                makeText(PostCreation.this, "ChildMain added successfully", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        Log.d("ParentItem", "Adding ParentItem with childData size: " + parentItem.getChildData().size());
//
//        firebaseRepo.addParentItem(parentItem, postId);

        // Upload child item images and add parent item to Firebase
        uploadChildItemImages(parentItem, postId);
    }

    private void sendtoFirebase(ParentItem parentItem, String postId) {
        FirebaseRepo firebaseRepo = new FirebaseRepo(new FirebaseRepo.OnRealtimeDbTaskComplete() {
            @Override
            public void onSuccess(Map<String, ParentItem> parentItemList) {
                // Handle success if needed
                makeText(PostCreation.this, "ParentItem added successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(DatabaseError error) {
                // Handle failure if needed
                makeText(PostCreation.this, "Error adding ParentItem: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccessChildMain(List<ChildMain> childMainList) {
                makeText(PostCreation.this, "ChildMain added successfully", Toast.LENGTH_SHORT).show();
            }
        });

        Log.d("ParentItem", "Adding ParentItem with childData size: " + parentItem.getChildData().size());

        firebaseRepo.addParentItem(parentItem, postId);
    }
    private void uploadChildItemImages(ParentItem parentItem, String postId) {
        AtomicInteger totalChildItems = new AtomicInteger(0);
        AtomicInteger uploadCounter = new AtomicInteger(0);

        for (Map.Entry<String, ChildMain> childMainEntry : parentItem.getChildData().entrySet()) {
            ChildMain childMain = childMainEntry.getValue();
            totalChildItems.addAndGet(childMain.getChildItemList().size());
        }

        if (totalChildItems.get() == 0) {
            // No child items to upload, directly call sendtoFirebase
            sendtoFirebase(parentItem, postId);
            return;
        }

        for (Map.Entry<String, ChildMain> childMainEntry : parentItem.getChildData().entrySet()) {
            String childMainKey = childMainEntry.getKey();
            ChildMain childMain = childMainEntry.getValue();
            List<ChildItem> childItemList = childMain.getChildItemList();

            for (int i = 0; i < childItemList.size(); i++) {
                ChildItem childItem = childItemList.get(i);
                if (childItem.getChildImage() != null) {
                    Uri imageUri = Uri.parse(childItem.getChildImage());
                    String childItemKey = String.valueOf(i); // Using position index as the key
                    StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(postId).child(childMainKey).child(childItemKey + "." + getFileExtension(imageUri));

                    fileRef.putFile(imageUri)
                            .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                childItem.setChildImage(downloadUrl);

                                Log.d("ChildItemImageUpload", "Image uploaded and URL updated: " + downloadUrl);

                                // Increment upload counter
                                int completedUploads = uploadCounter.incrementAndGet();

                                // Check if all uploads are complete
                                if (completedUploads == totalChildItems.get()) {
                                    // All child item images have been uploaded, now call sendtoFirebase
                                    sendtoFirebase(parentItem, postId);
                                }
                            }))
                            .addOnFailureListener(e -> {
                                Log.e("ChildItemImageUpload", "Failed to upload image for ChildItem: " + childItemKey, e);

                                // Increment upload counter even on failure to avoid hanging
                                int completedUploads = uploadCounter.incrementAndGet();

                                // Check if all uploads are complete
                                if (completedUploads == totalChildItems.get()) {
                                    // All child item images have been uploaded, now call sendtoFirebase
                                    sendtoFirebase(parentItem, postId);
                                }
                            });
                } else {
                    // Increment upload counter for items without images
                    int completedUploads = uploadCounter.incrementAndGet();

                    // Check if all uploads are complete
                    if (completedUploads == totalChildItems.get()) {
                        // All child item images have been uploaded, now call sendtoFirebase
                        sendtoFirebase(parentItem, postId);
                    }
                }
            }
        }
    }
    //upload parent image (getresult)
    private void uploadToFirebase(String uid, Uri imUri) {
        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(postId).child(postId + "." + getFileExtension(imUri));
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

    //for uploading images (general)
    private String getFileExtension(Uri imUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(imUri));
    }

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
}