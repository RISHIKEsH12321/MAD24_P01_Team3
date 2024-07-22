package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentSection extends AppCompatActivity {
    EditText editTextComment;
    ImageButton btnAddComment;

    String PostKey;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    ImageView profileImage;
    RecyclerView RvComment;
    CommentAdapter commentAdapter;
    List<Comment> listComment;
    static String COMMENT_KEY = "Comment" ;

    public interface OnUserDataRetrievedListener {
        void onUserDataRetrieved(List<String> userData);
        void onError(Exception e);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comment_section);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //get postkey from intent
        Intent intent = getIntent();
        PostKey = intent.getStringExtra("postId");

        //ini views
        RvComment = findViewById(R.id.rvComment);
        editTextComment = findViewById(R.id.editTextComment);editTextComment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
        editTextComment.setMovementMethod(new ScrollingMovementMethod());
        editTextComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 200) {
                    Toast.makeText(CommentSection.this, "You have reached the 200 character limit", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnAddComment = findViewById(R.id.btnAddComment);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        profileImage = findViewById(R.id.profile_imageview);
        String uid = firebaseUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        userRef.child("imageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String imageUrl = snapshot.getValue(String.class);
                    Glide.with(CommentSection.this)
                            .load(imageUrl)
                            .transform(new CircleCrop()) // Apply the CircleCrop transformation
                            .skipMemoryCache(true) // Disable memory cache
                            .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable disk cache
                            .into(profileImage);
                } else {
                    Toast.makeText(CommentSection.this, "No image found for user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CommentSection.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });


        // add comment button click listener
        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddComment.setVisibility(View.INVISIBLE);
                String comment_content = editTextComment.getText().toString();
                String uid = firebaseUser.getUid();
                iniCurrentUser(uid, new OnUserDataRetrievedListener() {
                    @Override
                    public void onUserDataRetrieved(List<String> userData) {
                        String uname = userData.get(0);
                        String uimg = userData.get(1);

                        DatabaseReference commentReference = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey).push();
                        Comment comment = new Comment(comment_content, uid, uimg, uname);

                        commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showMessage("Comment added");
                                editTextComment.setText("");
                                btnAddComment.setVisibility(View.VISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessage("Fail to add comment : " + e.getMessage());
                                btnAddComment.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        showMessage("Failed to retrieve user data: " + e.getMessage());
                        btnAddComment.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        // Adjust layout when keyboard is shown
        final View rootView = findViewById(R.id.main);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom - 50;
                if (keypadHeight > screenHeight * 0.15) {
                    // Keyboard is opened
                    findViewById(R.id.comment_input_layout).setPadding(0, 0, 0, keypadHeight);
                } else {
                    // Keyboard is closed
                    findViewById(R.id.comment_input_layout).setPadding(0, 0, 0, 0);
                }
            }
        });

        iniRvComment();

    }

    private void iniCurrentUser(String uid, OnUserDataRetrievedListener listener) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User userObject = snapshot.getValue(User.class); // Assuming your User class exists
                    if (userObject != null) {
                        String uimg = userObject.getImageUrl();
                        String uname = userObject.getName();
                        List<String> userData = new ArrayList<>();
                        userData.add(uname);
                        userData.add(uimg);
                        listener.onUserDataRetrieved(userData);
                    } else {
                        Log.w("TAG", "User object not found in database");
                        listener.onError(new Exception("User object not found in database"));
                    }
                } else {
                    Log.d("TAG", "No user data found");
                    listener.onError(new Exception("No user data found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Error retrieving user data", error.toException());
                listener.onError(error.toException());
            }
        });
    }

    private void iniRvComment() {

        RvComment.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listComment = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()) {

                    Comment comment = snap.getValue(Comment.class);
                    listComment.add(comment) ;

                }

                commentAdapter = new CommentAdapter(getApplicationContext(),listComment);
                RvComment.setAdapter(commentAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void showMessage(String message) {

        Toast.makeText(this,message,Toast.LENGTH_LONG).show();

    }


    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy",calendar).toString();
        return date;

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
}