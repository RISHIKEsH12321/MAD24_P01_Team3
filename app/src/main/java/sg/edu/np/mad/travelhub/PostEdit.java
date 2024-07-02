package sg.edu.np.mad.travelhub;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.Intent;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class PostEdit extends AppCompatActivity implements ChildMainAdapter.OnChildMainInteractionListener {

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


    // merge
    private DatabaseReference databaseReference;
    private AppCompatTextView actvName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //PostId intent from postlist
        Intent intentFromPost = getIntent();
        postId = intentFromPost.getStringExtra("postId");

        tvName = findViewById(R.id.POtvName);

        childMainRecyclerView = findViewById(R.id.POrvChildMainRecyclerView);
//        childMainRecyclerView = findViewById(R.id.childMainRecyclerView);

        //Recyclerview
        childMainRecyclerView.setHasFixedSize(true);
        childMainRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Adapter
        childMainAdapter = new ChildMainAdapter(2);
        childMainAdapter.setParentKey(postId);
        childMainAdapter.setOnChildMainInteractionListener(this);

        childMainRecyclerView.setAdapter(childMainAdapter);

        //childMainButton.setVisibility(View.INVISIBLE);
        //Firebase
        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        firebaseViewModel.getChildMainMutableLiveData().observe(this, new Observer<List<ChildMain>>() {
            @Override
            public void onChanged(List<ChildMain> childMainList) {
                childMainAdapter.setChildMainList(childMainList);
                Log.d("CHILDMAINADAPTER_SIZE", String.valueOf(childMainAdapter.getChildMainList().get(0).getChildMainName()));
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
                    ParentItem parentItem = snapshot.getValue(ParentItem.class);
                    tvName.setText(parentItem.getParentName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
}

//where is updating of childitem list