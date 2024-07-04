package sg.edu.np.mad.travelhub;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FirebaseRepo {
    private DatabaseReference databaseReference;
    private OnRealtimeDbTaskComplete onRealtimeDbTaskComplete;

    public FirebaseRepo(OnRealtimeDbTaskComplete onRealtimeDbTaskComplete){
        this.onRealtimeDbTaskComplete = onRealtimeDbTaskComplete;
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Posts");
    }

    public void getAllParentData(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, ParentItem> parentItemMap = new HashMap<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String postId = ds.getKey();
                    ParentItem parentItem = new ParentItem();
                    parentItem.setParentName(ds.child("parentName").getValue(String.class));
                    parentItem.setParentImage(ds.child("parentImage").getValue(String.class));
                    parentItem.setParentUser(ds.child("parentUser").getValue(String.class));

                    Map<String, ChildMain> childData = new HashMap<>();

                    parentItem.setChildData(childData);
                    parentItemMap.put(postId, parentItem);
                }
                onRealtimeDbTaskComplete.onSuccess(parentItemMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onRealtimeDbTaskComplete.onFailure(error);
            }
        });
    }

    public void getAllChildMainData(String postId){
        databaseReference.child(postId).child("childData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ChildMain> childMainList = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ChildMain childMain = new ChildMain();
                    String childMainName = ds.child("childMainName").getValue(String.class);
                    //Log.d("CHILDMAINNAME", childMainName);
                    childMain.setChildMainName(childMainName);
                    childMain.setKey(ds.getKey()); // Set the key

                    List<ChildItem> childItemList = new ArrayList<>();

                    DataSnapshot childItemReference = ds.child("childItemList");
                    for (DataSnapshot childItemSnapshot : childItemReference.getChildren()) {
                        ChildItem childItem = childItemSnapshot.getValue(ChildItem.class);
                        //Log.d("CHILDITEMNAME", childItem.getChildName());
                        if (childItem != null) {
                            childItemList.add(childItem);
                        } else {
                            Log.e("FirebaseRepo", "Error: ChildItem is null for key: " + childItemSnapshot.getKey());
                        }
                    }
                    childMain.setChildItemList(childItemList);
                    Log.d("CHILDITEMLIST_SIZE", String.valueOf(childItemList.size()));  // Log size of child items
                    childMainList.add(childMain);
                }


                onRealtimeDbTaskComplete.onSuccessChildMain(childMainList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onRealtimeDbTaskComplete.onFailure(error);
            }
        });
    }

    public void addParentItem(ParentItem parentItem, String postId) {
        //String name = parentItem.getParentName();
        //String image = parentItem.getParentImage();
        Map<String, ChildMain> childData = parentItem.getChildData();

        Log.d("PARENTITEMVALUE", String.valueOf(parentItem));
        DatabaseReference newParentRef = databaseReference.child(postId);
        newParentRef.setValue(parentItem).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("TAG", "ParentItem added successfully");

                    //Insert ChildData
                    newParentRef.child("childData").setValue(childData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "ChildData added successfully");
                            } else {
                                Log.e("TAG", "Error adding ChildData", task.getException());
                            }
                        }
                    });
                } else {
                    Log.e("TAG", "Error adding ParentItem", task.getException());
                }
            }
        });
    }


    public interface OnRealtimeDbTaskComplete{
        void onSuccess(Map<String, ParentItem> parentItemList);
        void onFailure(DatabaseError error);
        void onSuccessChildMain(List<ChildMain> childMainList);
    }
}

