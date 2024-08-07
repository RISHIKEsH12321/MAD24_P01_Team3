package sg.edu.np.mad.travelhub;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link postsearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class postsearch extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    DatabaseReference ref;
    String displayStr;
    RecyclerView recyclerView;
    SearchView searchBar;
    Map<String, ParentItem> postMap;
    ValueEventListener eventListener;
    private Loading_Dialog loadingDialog;
    boolean isFollowing;
    ParentAdapter adapter;
    FirebaseUser firebaseUser;
    FirebaseViewModel firebaseViewModel;
    FloatingActionButton fabCreate;
    int color1;
    int color2;
    int color3;
    public postsearch() {

        // Required empty public constructor

    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences = getContext().getSharedPreferences("spinner_preferences", MODE_PRIVATE);
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


        searchBar = view.findViewById(R.id.SUsvSearch);
        recyclerView = view.findViewById(R.id.SUrvList);

        loadingDialog = new Loading_Dialog(getActivity());

        //get views
        ref = FirebaseDatabase.getInstance().getReference("Posts");

        //Grid Layout Manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        //Alert dialog when user presses the recyclerview

        //list
        postMap = new HashMap<>();
        adapter = new ParentAdapter(getContext());
        recyclerView.setAdapter(adapter);
        loadingDialog.startLoadingDialog();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        String postUid = firebaseUser.getUid();

        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        firebaseViewModel.getParentItemMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Map<String, ParentItem>>() {
            @Override
            public void onChanged(Map<String, ParentItem> parentItemMap) {
                adapter.setParentItemMap(parentItemMap);
                postMap = parentItemMap;
                adapter.notifyDataSetChanged();
            }
        });
        firebaseViewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), new Observer<DatabaseError>() {
            @Override
            public void onChanged(DatabaseError error) {
//                Toast.makeText(PostList.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        firebaseViewModel.getAllParentData();
        loadingDialog.dismissDialog();

//        eventListener = ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                postMap.clear();
//                for (DataSnapshot postSnapshot: snapshot.getChildren()){
//                    ParentItem post = postSnapshot.getValue(ParentItem.class);
//                    if (post != null && firebaseUser != null) {
//                       postMap.put(post.getParentKey(), post);
//                    }
//                }
//                adapter.notifyDataSetChanged();
//                loadingDialog.dismissDialog();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        //searchBar logic
        searchBar.clearFocus(); //incase focus is default on the search bar
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText, adapter);
                return true;
            }
        });

        fabCreate = view.findViewById(R.id.fabCreate);
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PostCreation.class);
                startActivity(intent);
            }
        });

        ColorStateList colorStateList = ColorStateList.valueOf(color1);
        fabCreate.setBackgroundTintList(colorStateList);
    }

    // TODO: Rename and change types and number of parameters
    public static postsearch newInstance(String param1, String param2) {
        postsearch fragment = new postsearch();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    //to filter the list of results in search bar
    private void filterList(String text, ParentAdapter adapter) {
        Map<String, ParentItem> filteredMap = new HashMap<>();
        for (Map.Entry<String, ParentItem> entry : postMap.entrySet()) { // Iterate over the entries of postMap
            ParentItem parentItem = entry.getValue();
            Log.d("parentitem", parentItem.getParentName());
            if (parentItem.getParentName().toLowerCase().contains(text.toLowerCase())) {
                filteredMap.put(entry.getKey(), parentItem);
            }
        }
        adapter.setParentItemMap(filteredMap); // Pass the filtered map to the adapter
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_postsearch, container, false);
    }
}