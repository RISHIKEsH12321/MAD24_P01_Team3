package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Post extends AppCompatActivity {
    private FirebaseViewModel firebaseViewModel;
    private RecyclerView childMainRecyclerView;
    private ChildMainAdapter childMainAdapter;
    private String postId;
    private DatabaseReference databaseReference;
    private FloatingActionButton btnAddChild;
    private Button childMainButton;

    private AppCompatImageView postImage;
    //private RecyclerView postRecyclerView;
    private ParentItem parentItem;
    private List<ChildMain> mainList;

    private AppCompatTextView actvName;
    private TextView tvName, tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //Intent from Post List class
        Intent intentFromPost = getIntent();
        postId = intentFromPost.getStringExtra("postId");

        //Init name, desc and image
        actvName = findViewById(R.id.POactvPostName);
        tvDescription = findViewById(R.id.POtvDescription);
        postImage = findViewById(R.id.POacivPostImage);


//        childMainRecyclerView = findViewById(R.id.childMainRecyclerView);

        //Recyclerview
        childMainRecyclerView = findViewById(R.id.POrvChildMainRecyclerView);
        childMainRecyclerView.setHasFixedSize(true);
        childMainRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Adapter
        childMainAdapter = new ChildMainAdapter(0, new OnImageClickListener.Listener() {
            @Override
            public void onImageClick(int mainPosition, int itemPosition) {
                //to zoom in on image
            }
        }, childMainRecyclerView, mainList);
        childMainRecyclerView.setAdapter(childMainAdapter);

        //childMainButton.setVisibility(View.INVISIBLE);
        //Firebase
        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        firebaseViewModel.getChildMainMutableLiveData().observe(this, new Observer<List<ChildMain>>() {
            @Override
            public void onChanged(List<ChildMain> childMainList) {
                mainList = childMainList;
                childMainAdapter.setChildMainList(childMainList);
               Log.d("CHILDMAINADAPTER_SIZE", String.valueOf(childMainAdapter.getChildMainList().size()));
                childMainAdapter.notifyDataSetChanged();
            }
        });
        firebaseViewModel.getDatabaseErrorMutableLiveData().observe(this, new Observer<DatabaseError>() {
            @Override
            public void onChanged(DatabaseError error) {
                Toast.makeText(Post.this, error.toString(), Toast.LENGTH_SHORT).show();
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
                    actvName.setText(parentItem.getParentName());
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
        //Set button invisible for each ChildMain button
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
