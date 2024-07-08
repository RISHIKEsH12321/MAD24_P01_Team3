package sg.edu.np.mad.travelhub;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class Following extends Fragment {
    String uid;
    private Loading_Dialog loadingDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_following, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.followingList);

        //define a loading dialog
        loadingDialog = new Loading_Dialog(getActivity());

        Bundle arguments = getArguments();
        if (arguments != null) {
            uid = arguments.getString("userUid");
            // Use the receivedUserUid for data fetching or other operations
        }

        //GridLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        //list
        List uidList = new ArrayList<>();
        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference().child("Follow").child(uid)
                .child("following");
        //add UIDs to list
        followingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                        uidList.add(childSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //get user objects in userlist
        List usersList = new ArrayList<>();
        followingRef = FirebaseDatabase.getInstance().getReference().child("Users");
        loadingDialog.startLoadingDialog();
        followingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                        User userObject = snapshot.getValue(User.class);
                        if (uidList.contains(userObject.getUid())) {
                            usersList.add(userObject);
                        }
                    }
                    loadingDialog.dismissDialog();
                    UserAdapter adapter = new UserAdapter(getContext(), (ArrayList<User>) usersList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }
}