package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Posts extends Fragment {
    View view;
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
    private static final String ARG_DATA_KEY = "data_key";


    public static Posts newInstance(String data) {
        Posts fragment = new Posts();
        Bundle args = new Bundle();
        args.putString(ARG_DATA_KEY, data);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_posts, container, false);
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        adapter = new ParentAdapter(getActivity().getApplicationContext());
        recyclerView.setAdapter(adapter);
        loadingDialog.startLoadingDialog();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        String postUid = firebaseUser.getUid();

        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        firebaseViewModel.getParentItemMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Map<String, ParentItem>>() {
            @Override
            public void onChanged(Map<String, ParentItem> parentItemMap) {
//                adapter.setParentItemMap(parentItemMap);
                postMap = parentItemMap;

                if (getActivity() instanceof Profile) {
                    FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (fbUser != null) {
                        String uid = fbUser.getUid();
                        Log.d("UID", "Current user UID: " + uid);

                        Map<String, ParentItem> filteredMap = new HashMap<>();
                        for (Map.Entry<String, ParentItem> entry : postMap.entrySet()) {
                            ParentItem parentItem = entry.getValue();
                            String parentUser = parentItem.getParentUser();
                            Log.d("ParentItem", "Parent name: " + parentItem.getParentName() + ", Parent user: " + parentUser);

                            if (parentUser != null && parentUser.equals(uid)) {
                                filteredMap.put(entry.getKey(), parentItem);
                            }
                        }
                        if (filteredMap != null){
                            adapter.setParentItemMap(filteredMap);
                            adapter.notifyDataSetChanged();
                        }

                    } else {
                        Log.e("UID", "FirebaseUser is null");
                    }
                }
                else if (getActivity() instanceof OtherUserProfile) {
                    Bundle args = getArguments();
                    String uid = null;
                    if (args != null) {
                        uid = args.getString(ARG_DATA_KEY);
                        Log.d("datakey", uid);
                    }

                    Map<String, ParentItem> filteredMap = new HashMap<>();
                    for (Map.Entry<String, ParentItem> entry : postMap.entrySet()) {
                        ParentItem parentItem = entry.getValue();
                        String parentUser = parentItem.getParentUser();
                        Log.d("OtherParentItem", "Parent name: " + parentItem.getParentName() + ", Parent user: " + parentUser);

                        if (parentUser != null && parentUser.equals(uid)) {
                            filteredMap.put(entry.getKey(), parentItem);
                        }
                    }
                    Log.d("FilteredMap", "Filtered map size: " + filteredMap.size());

                    adapter.setParentItemMap(filteredMap);
                    adapter.notifyDataSetChanged();

                }

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

    }

}