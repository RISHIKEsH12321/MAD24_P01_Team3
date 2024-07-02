package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostCreation extends AppCompatActivity {

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

//    DatabaseReference ref;
//    AppCompatButton btnBack;
//    RecyclerView parentRView;
//    FloatingActionButton addChild;
//    List<PostChild> postChildList;
//    ValueEventListener eventListener;


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
        childMainAdapter = new ChildMainAdapter(1);
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
        parentItem.setParentKey(postId);
        //Get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        parentItem.setParentUser(uid);

        // Get the FirebaseRepo instance and add the new ParentItem
        FirebaseRepo firebaseRepo = new FirebaseRepo(new FirebaseRepo.OnRealtimeDbTaskComplete() {
            @Override
            public void onSuccess(Map<String, ParentItem> parentItemList) {
                // Handle success if needed
                Toast.makeText(PostCreation.this, "ParentItem added successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(DatabaseError error) {
                // Handle failure if needed
                Toast.makeText(PostCreation.this, "Error adding ParentItem: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccessChildMain(List<ChildMain> childMainList) {
                Toast.makeText(PostCreation.this, "ChildMain added successfully", Toast.LENGTH_SHORT).show();
            }
        });

        Log.d("ParentItem", "Adding ParentItem with childData size: " + parentItem.getChildData().size());

        firebaseRepo.addParentItem(parentItem, postId);

    }
}