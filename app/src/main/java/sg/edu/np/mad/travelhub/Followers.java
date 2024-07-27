package sg.edu.np.mad.travelhub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Followers extends Fragment {
    String uid;
    boolean isUidListPopulated;
    private Loading_Dialog loadingDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_followers, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.followersList);

        //define a loading dialog
        loadingDialog = new Loading_Dialog(getActivity());

        //get uid
        Bundle arguments = getArguments();
        if (arguments != null) {
            uid = arguments.getString("userUid");
            //use the receivedUserUid for data fetching or other operations
        }
        //GridLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        //list
        List<String> uidList = new ArrayList<>();
        DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference().child("Follow").child(uid)
                .child("followers");
        //add UIDs to list
        isUidListPopulated = false; //variable to ensure uid list is populated before userslist is populated
        followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                        uidList.add(childSnapshot.getKey());
                    }
                    Log.i("meemee", uidList.toString());
                    isUidListPopulated = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //get user objects in userlist
        List usersList = new ArrayList<>();
        followersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        loadingDialog.startLoadingDialog();
        followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && isUidListPopulated) {
                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                        User userObject = childSnapshot.getValue(User.class);
                        if (uidList.contains(userObject.getUid())) {
                            usersList.add(userObject);
                        }
                    }

                    UserAdapter adapter = new UserAdapter(getContext(), (ArrayList<User>) usersList);
                    recyclerView.setAdapter(adapter);

                }
                loadingDialog.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}