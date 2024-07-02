package sg.edu.np.mad.travelhub;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class ChildMainAdapter extends RecyclerView.Adapter<ChildMainAdapter.BaseViewHolder> {
    private static final int VIEW_TYPE_POST = 0;
    private static final int VIEW_TYPE_POST_CREATION = 1;

    private List<ChildMain> childMainList;
    private int viewType;
    public static OnChildMainInteractionListener listener;


    public ChildMainAdapter(int viewType){

        this.viewType = viewType;
        //to be deleted
        this.childMainList = new ArrayList<>();

    }

    // Define an interface for callback
    public interface OnChildMainInteractionListener {
        void onSaveButtonClick(ChildMain childMain);
    }

    public void setOnChildMainInteractionListener(OnChildMainInteractionListener listener) {
        this.listener = listener;
    }

    public void setChildMainList(List<ChildMain> childMainList){
        //Log.d("setchildmainlist", String.valueOf(childMainList.get(0).getChildMainName()));
        if (childMainList != null) {
            this.childMainList = childMainList;
        } else {
            this.childMainList = new ArrayList<>();
        }
        //to be deleted
        //notifyDataSetChanged();
    }

    public List<ChildMain> getChildMainList() {
        return childMainList;
    }

    @Override
    public int getItemViewType(int position) {
        return viewType; // Return the view type
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_childmain_item , parent , false);
        View view;
        if (viewType == VIEW_TYPE_POST) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_childmain_item, parent, false);
            return new PostViewHolder(view);
        } else if (viewType == VIEW_TYPE_POST_CREATION) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_childmain_item_create, parent, false);
            return new PostCreationViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_childmain_item_edit, parent, false);
            return new PostEditViewholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

        ChildMain childMain = childMainList.get(position);
        holder.bind(childMain);

//        ChildMain childMain = childMainList.get(position);
//        holder.childMainName.setText(childMain.getChildMainName());
////        Glide.with(holder.itemView.getContext()).load(childMain.getchildMainImage())
//
////                .into(holder.childMainIv);
//
//        holder.childMainRecyclerView.setHasFixedSize(true);
//        holder.childMainRecyclerView.setLayoutManager(new GridLayoutManager(holder.itemView.getContext() , 1));
//
//        ChildAdapter childAdapter = new ChildAdapter();
//        List<ChildItem> childItemList = childMain.getChildItemList();
//        childAdapter.setChildItemList(childItemList);
//        holder.childMainRecyclerView.setAdapter(childAdapter);
//        //childAdapter.notifyDataSetChanged();
//
//        holder.childMain = childMain;
//        holder.childAdapter = childAdapter;

//        childAdapter.setChildItemList(parentItem.getChildListItem());
//        holder.childRecyclerView.setAdapqter(childAdapter);
//        childAdapter.notifyDataSetChanged(
    }

    @Override
    public int getItemCount() {
        if (childMainList != null){
            return childMainList.size();
        }else{
            return 0;
        }

    }

    public void addChildMain() {
        ChildMain newChildMain = new ChildMain("New List", new ArrayList<>(), "List"+childMainList.size());
        childMainList.add(newChildMain);
        notifyItemInserted(childMainList.size() - 1);
    }


    public static abstract class BaseViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public RecyclerView childMainRecyclerView;
        public ChildMain childMain;
        //public ChildAdapter childAdapter;

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.eachChildMainName);
            childMainRecyclerView = itemView.findViewById(R.id.childMainRecyclerView);

            //ChildAdapter childAdapter = new ChildAdapter(getItemViewType());
//            ChildAdapter childAdapter = new ChildAdapter(getItemViewType());
//            childMainRecyclerView.setHasFixedSize(true);
//            childMainRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 1));
//            childMainRecyclerView.setAdapter(childAdapter);
        }

        public void bind(ChildMain childMain) {
            // this.childMain = childMain;
//            List<ChildItem> childItemList = childMain.getChildItemList();
//            childAdapter.setChildItemList(childItemList);
//            childAdapter.notifyDataSetChanged();
            this.childMain = childMain;
            //tvName.setText(childMain.getChildMainName());


            //tvName.setText(childMain.getChildMainName());
            //ChildAdapter childAdapter = new ChildAdapter(getItemViewType());
            //childMainRecyclerView.setAdapter(childAdapter);
            //this.childAdapter = childAdapter;
        }
    }

    public static class PostViewHolder extends BaseViewHolder {
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            //The one that made it an error  (cannot put this here cuz not initiated yet)
            //childAdapter.setChildItemList(childMain.getChildItemList());
            //childAdapter.notifyDataSetChanged();
        }

        @Override
        public void bind(ChildMain childMain){
            super.bind(childMain);
            tvName.setText(childMain.getChildMainName());
            ChildAdapter childAdapter = new ChildAdapter(0);
            childAdapter.setChildItemList(childMain.getChildItemList());
            childMainRecyclerView.setHasFixedSize(true);
            childMainRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 1));
            childMainRecyclerView.setAdapter(childAdapter);
        }
    }

    public static class PostEditViewholder extends BaseViewHolder {
        public PostEditViewholder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(ChildMain childMain){
            super.bind(childMain);

            Log.d("BindMethod", "childMainName: " + childMain.getChildMainName());

            tvName.setText(childMain.getChildMainName());
            ChildAdapter childAdapter = new ChildAdapter(0);
            childAdapter.setChildItemList(childMain.getChildItemList());
            childMainRecyclerView.setHasFixedSize(true);
            childMainRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 1));
            childMainRecyclerView.setAdapter(childAdapter);

            EditText etName = itemView.findViewById(R.id.etChildMainName);

            //btnAdd


            //Create edit text
            Button btnSave = itemView.findViewById(R.id.btnSave);
            Button btnEdit = itemView.findViewById(R.id.btnEdit);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("EDIT BUTTON", "edit button pressed");
                    //name edit
                    tvName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tvName.setVisibility(View.INVISIBLE);
                            etName.setVisibility(View.VISIBLE);
                            etName.requestFocus();

//                            String newName = etName.getText().toString();
//                            childMain.setChildMainName(newName);
//                            tvName.setText(newName);

//                            childAdapter.setChildItemList(childMain.getChildItemList());
//                            childAdapter.notifyDataSetChanged();
                            //saveChildMainToFirebase(childMain);
                        }
                    });

                    //recyclerview edit
                    ChildAdapter editChildAdapter = new ChildAdapter(2);
                    editChildAdapter.setChildItemList(childMain.getChildItemList());
                    childMainRecyclerView.setHasFixedSize(true);
                    childMainRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 1));
                    childMainRecyclerView.setAdapter(editChildAdapter);
                    btnEdit.setVisibility(View.INVISIBLE);
                    btnSave.setVisibility(View.VISIBLE);
                }
            });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvName.setVisibility(View.VISIBLE);
                    etName.setVisibility(View.INVISIBLE);

                    btnEdit.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.INVISIBLE);


                    // Update the childMain with the new values
                    String newName = etName.getText().toString();
                    tvName.setText(newName);

                    //updateKey (firebase function)

                    childMain.setChildMainName(etName.getText().toString());

//                    String oldKey = childMain.getChildMainName(); // Assuming the current key is the name
//                    String newKey = etName.getText().toString().trim();
//                    childMain.setChildMainName(newKey);

//                    updateFirebaseNode(oldKey, newKey, childMain);

                    // Trigger the callback to save the data
                    if (listener != null) {
                        listener.onSaveButtonClick(childMain);
                    }
                    //Log.d("Post id", );
                }
            });

            //fetch data from firebase
            //Child main id

            //btnEdit.setOnClickListener();
        }

//        private void saveChildMainToFirebase(ChildMain childMain) {
//            // Assuming you have a Firebase reference to your childMain node
//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("childMain");
//
//            // Use the childMain's ID or another unique identifier to update the correct node
//            databaseReference.child(childMain.getId()).setValue(childMain)
//                    .addOnSuccessListener(aVoid -> {
//                        // Successfully updated in Firebase
//                        Log.d("Firebase", "ChildMain name updated successfully.");
//                    })
//                    .addOnFailureListener(e -> {
//                        // Failed to update in Firebase
//                        Log.d("Firebase", "Failed to update ChildMain name.", e);
//                    });
//        }
private void updateFirebaseNode(String oldKey, String newKey, ChildMain childMain) {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("post2/childData");

    // Retrieve current data
    databaseReference.child(oldKey).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                // Get the data
                ChildMain oldChildMain = dataSnapshot.getValue(ChildMain.class);

                // Set the new key with the same data
                databaseReference.child(newKey).setValue(oldChildMain).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Remove the old key
                        databaseReference.child(oldKey).removeValue().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Log.d("Firebase", "ChildMain name updated successfully.");
                            } else {
                                Log.d("Firebase", "Failed to remove old ChildMain node.");
                            }
                        });
                    } else {
                        Log.d("Firebase", "Failed to set new ChildMain node.");
                    }
                });
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d("Firebase", "Error retrieving data", databaseError.toException());
        }
    });
}
    }

    public static class PostCreationViewHolder extends BaseViewHolder {
        private TextView tvName;
        private EditText etName;
        private Button childMainButton, btnDelete;
        private ChildAdapter childAdapter;

        public PostCreationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvChildMainName); // Make sure this ID is correct
            etName = itemView.findViewById(R.id.etChildMainName);

            //ChildAdapter childAdapter = new ChildAdapter(1);

            childMainRecyclerView.setHasFixedSize(true);
            childMainRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 1));
            //childMainRecyclerView.setAdapter(childAdapter);
            //childAdapter.setChildItemList(childMain.getChildItemList());
            //childAdapter.notifyDataSetChanged();

            //button to delete
            btnDelete = itemView.findViewById(R.id.childMainbtnDelete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar snackbar= Snackbar.make(v.findViewById(android.R.id.content),"POPUP", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });

            //button to add child item
            childMainButton = itemView.findViewById(R.id.childMainButton);
            childMainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addChildItem();
                }
            });

            //Changing of name and description
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvName.setVisibility(View.INVISIBLE);
                    etName.setVisibility(View.VISIBLE);
                    etName.requestFocus();
                }
            });

            etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        tvName.setText(etName.getText());
                        childMain.setChildMainName(String.valueOf(etName.getText()).trim());
                        tvName.setVisibility(View.VISIBLE);
                        etName.setVisibility(View.GONE);
                    }
                }
            });
        }

        @Override
        public void bind(ChildMain childMain){
            super.bind(childMain);
            //tvName.setText(childMain.getChildMainName());

            childAdapter = new ChildAdapter(1);
            childAdapter.setChildItemList(childMain.getChildItemList());
            childMainRecyclerView.setAdapter(childAdapter);
            childAdapter.notifyDataSetChanged();

        }
        private void addChildItem() {
            ChildItem newChildItem = new ChildItem("New Item", "New Item image");
            List<ChildItem> childData = childMain.getChildItemList();

            // Add the new ChildItem to the list
            childData.add(newChildItem);

            // Update the ChildMain instance with the new list
            childMain.setChildItemList(childData);

            // Update the adapter and notify the change
            childAdapter.setChildItemList(childData);
            childAdapter.notifyItemInserted(childData.size() - 1);
            childMainRecyclerView.scrollToPosition(childData.size() - 1);
        }

    }

}
