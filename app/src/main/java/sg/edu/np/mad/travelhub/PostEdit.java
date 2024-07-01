package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostEdit extends AppCompatActivity {

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
        childMainAdapter = new ChildMainAdapter(0);
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
}