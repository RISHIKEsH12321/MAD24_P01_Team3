package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PostList extends AppCompatActivity {

    DatabaseReference ref;
    private FirebaseViewModel firebaseViewModel;
    RecyclerView parentRView;
    FloatingActionButton btnCreate;
    //List<PostChild> postChildList;
    ValueEventListener eventListener;
    ParentAdapter parentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        UniqueID for posts
        String uniqueID = UUID.randomUUID().toString();
        Log.d("UNIQUEID", uniqueID);

        //Button to go back to home page
//        btnBack = findViewById(R.id.PObtnBack);
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentBack = new Intent(getApplicationContext(), HomeActivity.class);
//                startActivity(intentBack);
//                finish();
//            }
//        });

        //Populate parentrecyclerview
        parentRView = findViewById(R.id.PLrvList);

        parentRView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        parentRView.setLayoutManager(layoutManager);

        parentAdapter = new ParentAdapter();
        parentRView.setAdapter(parentAdapter);


        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        firebaseViewModel.getParentItemMutableLiveData().observe(this, new Observer<Map<String, ParentItem>>() {
            @Override
            public void onChanged(Map<String, ParentItem> parentItemMap) {
                parentAdapter.setParentItemMap(parentItemMap);
                parentAdapter.notifyDataSetChanged();
            }
        });
        firebaseViewModel.getDatabaseErrorMutableLiveData().observe(this, new Observer<DatabaseError>() {
            @Override
            public void onChanged(DatabaseError error) {
                Toast.makeText(PostList.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        firebaseViewModel.getAllParentData();



        btnCreate = findViewById(R.id.PLfabSearch);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCreate = new Intent(getApplicationContext(), PostCreation.class);
                //String postId = UUID.randomUUID().toString();
                //intentCreate.putExtra("postId", postId);
                String postId;
                String firstParentItemKey;
                List<String> listOfPostId = parentAdapter.getPostIds();
                if (listOfPostId.isEmpty()) {
                    postId = "post1";
                }
                else {
                    //get first parent key because newest key is always the first
                    firstParentItemKey = listOfPostId.get(listOfPostId.size()-1);
                    int newKey = Integer.parseInt(firstParentItemKey.replaceAll("[^0-9]", "")) + 1;
                    postId = "post" + newKey;
                    Log.d("NEWKEY", postId);
                }

                for (int i = 0; i < listOfPostId.size(); i++) {
                    Log.d("keyss", listOfPostId.get(i));
                }

                intentCreate.putExtra("postId", postId);
                startActivity(intentCreate);
            }
        });

        ///////////////
        //Instantiating for recycler view (parent)
        //ref straight to the first post
//        ref = FirebaseDatabase.getInstance().getReference("Posts").child("b4654679-ced6-4ea3-a58e-711f946a5a5d");
//        parentRView = findViewById(R.id.PLrvList);
//
//        //GridLayoutManager
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(PostList.this, 1);
//        parentRView.setLayoutManager(gridLayoutManager);
//
//        //list
//        postChildList = new ArrayList<>();
//        ParentAdapter adapter = new ParentAdapter();
//        parentRView.setAdapter(adapter);
//
//        //Populating recycler view (parent)
//        eventListener = ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                postChildList.clear();
//                for (DataSnapshot postChildSnapshot: snapshot.getChildren()){
//                    PostChild postChild = postChildSnapshot.getValue(PostChild.class);
//                    Log.d("FirebaseData", "Snapshot: " + snapshot.toString());
//                    postChildList.add(postChild);
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                //something
//            }
//        });


        // Add child to parent recycler view
//        addChild.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SearchUser.this, ProfileCreation.class); //OtherProfile.class);
//                startActivity(intent);
//            }
//        });
    }
}