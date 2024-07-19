package sg.edu.np.mad.travelhub;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import kotlin.ParameterName;

public class ChildMainAdapter extends RecyclerView.Adapter<ChildMainAdapter.BaseViewHolder> {
    private static final int VIEW_TYPE_POST = 0;
    private static final int VIEW_TYPE_POST_CREATION = 1;
    private static final int VIEW_TYPE_POST_EDIT = 2;

    private List<ChildMain> childMainList;
    private List<ChildItem> childItemList;
    private int viewType;
    private String parentKey;
    private String childMainKey;
    public static OnChildMainInteractionListener listener;

    private OnImageClickListener.Listener onImageClickListener;
    private RecyclerView recyclerView;
    private SparseBooleanArray expandState = new SparseBooleanArray(); // Array to save expand/collapse state


    public ChildMainAdapter(int viewType, OnImageClickListener.Listener onImageClickListener, RecyclerView recyclerView, List<ChildMain> childMainList){

        this.viewType = viewType;
        //to be deleted
        this.childMainList = new ArrayList<>();
        this.onImageClickListener = onImageClickListener;
        this.recyclerView = recyclerView;
        this.childMainList = childMainList;
        //this.expandState = expandState != null ? expandState : new SparseBooleanArray();
    }

    public void resetExpandState() {
//        Log.d("ChildMainAdapter", "resetExpandState called");
//        expandState.clear();
//        for (int i = 0; i < childMainList.size(); i++) {
//            expandState.put(i, false);
//            Log.d("ChildMainAdapter", "Item " + i + " set to not expandable");
//        }
//        logExpandState();
//        notifyDataSetChanged();
    }

    // Method to log the contents of expandState
    private void logExpandState() {
        Log.d("ChildMainAdapter", "Logging expandState contents:");
        for (int i = 0; i < expandState.size(); i++) {
            int key = expandState.keyAt(i);
            boolean value = expandState.valueAt(i);
            Log.d("ChildMainAdapter", "Item " + key + " expandable state: " + value);
        }
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
        Log.d("parentkey111", parentKey);
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
            return new PostViewHolder(view, this, onImageClickListener);
        } else if (viewType == VIEW_TYPE_POST_CREATION) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_childmain_item_create, parent, false);
            return new PostCreationViewHolder(view, this, onImageClickListener);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_childmain_item_edit, parent, false);
            return new PostEditViewholder(view, this, onImageClickListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

        ChildMain childMain = childMainList.get(position);
        //holder.bind(childMain, position);
//        boolean isExpanded = expandState.get(position, false);
        boolean isExpanded = childMain.isExpandable();
        Log.d("ChildMainAdapter", "onBindViewHolder: Item " + position + " expandable state: " + isExpanded);
//        if (holder instanceof PostViewHolder) {
//            ((PostViewHolder) holder).expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
//        }
//        childMain.setExpandable(isExpanded);
        holder.bind(childMain, position);


        if (holder instanceof PostCreationViewHolder) {
            ((PostCreationViewHolder) holder).updateButtonVisibility(childMain);
        }

    }

    @Override
    public int getItemCount() {
        if (childMainList != null){
            return childMainList.size();
        }else{
            return 0;
        }

    }

    public void updateChildItemImage(int mainPosition, int itemPosition, Uri imageUri) {
        ChildMain childMain = childMainList.get(mainPosition);
        ChildItem childItem = childMain.getChildItemList().get(itemPosition);
        childItem.setChildImage(imageUri.toString());

        // Directly update the ViewHolder without calling notifyItemChanged
        if (recyclerView != null) {
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(mainPosition);
                    if (viewHolder instanceof ChildMainAdapter.PostEditViewholder) {
                        ChildMainAdapter.PostEditViewholder editViewHolder = (ChildMainAdapter.PostEditViewholder) viewHolder;
                        editViewHolder.updateChildItemImage(itemPosition, imageUri.toString());
                        editViewHolder.updateButtonVisibility(childMain);
                    }
                }
            });
        }
    }
    public void addChildMain() {
        // Get the key of the last item
        ChildMain lastChildMain;
        String newKey;
        if (childMainList.isEmpty()) {
            newKey = "List0"; // Start with "item0" if the list is empty
        }
        else {
            lastChildMain = childMainList.get(childMainList.size()-1);
            String lastChildMainKey = lastChildMain.getKey();

            // Extract the numeric part from the key
            int lastNumber = Integer.parseInt(lastChildMainKey.replaceAll("[^0-9]", ""));

            // Increment the number
            int newNumber = lastNumber + 1;

            // New Key String
            newKey = "List" + newNumber;
        }

        ChildMain newChildMain = new ChildMain("New List", new ArrayList<>(), newKey);
        childMainList.add(newChildMain);
        notifyItemInserted(childMainList.size() - 1);
    }

    public void deleteMain(int position) {
        if (position >= 0 && position < childMainList.size()) {
            childMainList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, childMainList.size());
        }
    }

    private void deleteChildMain(int position) {
        Log.d("DBREFERENCE", String.valueOf(FirebaseDatabase.getInstance().getReference()));
        if (position >= 0 && position < childMainList.size()) {
            // Get the key of the item to be deleted
            String keyToDelete = childMainList.get(position).getKey();

            Log.d("KEYTODELETE", keyToDelete);
            // Remove the item from the list
            childMainList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, childMainList.size());
            notifyDataSetChanged();

            // Remove the item from the Firebase Database
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Posts")
                    .child(parentKey)
                    .child("childData");
            DatabaseReference childMainRef = databaseRef.child(keyToDelete);

            childMainRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Item successfully deleted from database
                    Log.d("DeleteChildMain", "Item successfully deleted from database");
                } else {
                    // Handle failure
                    Log.e("DeleteChildMain", "Failed to delete item from database", task.getException());
                }
            });
        }
    }

    public static abstract class BaseViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public RecyclerView childMainRecyclerView;
        public ChildMain childMain;
        private ChildMainAdapter adapter;
        protected int viewType;
        protected OnImageClickListener.Listener onImageClickListener;

        public BaseViewHolder(@NonNull View itemView, ChildMainAdapter adapter, int viewType, OnImageClickListener.Listener onImageClickListener) {
            super(itemView);
            this.adapter = adapter;
            this.viewType = viewType;
            this.onImageClickListener = onImageClickListener;
            tvName = itemView.findViewById(R.id.eachChildMainName);
            childMainRecyclerView = itemView.findViewById(R.id.childMainRecyclerView);
        }

        public void bind(ChildMain childMain, int childMainPosition) {
            this.childMain = childMain;
//            boolean isExpandable = childMain.isExpandable();
//            childMainRecyclerView.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
//
//            if (isExpandable) {
//                childMainRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
//                ChildAdapter childAdapter = getChildAdapter(childMainPosition);
//                childAdapter.setChildItemList(childMain.getChildItemList());
//                childMainRecyclerView.setAdapter(childAdapter);
//            }

            handleExpandable(childMainRecyclerView, childMain, getBindingAdapterPosition());
        }

        protected abstract ChildAdapter getChildAdapter(int childMainPosition);

        protected void handleExpandable(RecyclerView childMainRecyclerView, ChildMain childMain, final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isExpandable = !childMain.isExpandable();
                    childMain.setExpandable(isExpandable);
                    adapter.childItemList = childMain.getChildItemList();
                    adapter.notifyItemChanged(position);
                }
            });
        }
    }




    public static class PostViewHolder extends BaseViewHolder {
        private ChildMainAdapter adapter;
        private ChildAdapter childAdapter;
        private OnImageClickListener.Listener onImageClickListener;
        private ConstraintLayout expandableLayout;
        public PostViewHolder(@NonNull View itemView, ChildMainAdapter adapter, OnImageClickListener.Listener onImageClickListener) {
            super(itemView, adapter, VIEW_TYPE_POST, onImageClickListener);
            this.adapter = adapter;
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
        }

        @Override
        public void bind(ChildMain childMain, int childMainPosition){
            super.bind(childMain, childMainPosition);
            tvName.setText(childMain.getChildMainName());
            boolean isExpanded = childMain.isExpandable();
            expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

            if (childMain.isExpandable()) {
                ChildAdapter childAdapter = getChildAdapter(childMainPosition);
                childAdapter.setChildItemList(childMain.getChildItemList());
                childMainRecyclerView.setHasFixedSize(true);
                childMainRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                childMainRecyclerView.setAdapter(childAdapter);
            }

        }
        @Override
        protected ChildAdapter getChildAdapter(int childMainPosition) {
            return new ChildAdapter(VIEW_TYPE_POST, onImageClickListener, childMainPosition);
        }
    }

    public static class PostEditViewholder extends BaseViewHolder {
        private ChildMainAdapter adapter;
        private ChildAdapter childAdapter;
        private String originalChildMainName;
        private List<ChildItem> originalChildItemList;
        private OnImageClickListener.Listener onImageClickListener;
        public Button btnSave;
        public Button btnEdit;
        private Button btnAdd;
        private Button btnRemove;
        private Button btnCancel;
        private boolean isEditing; // Track the edit state
        private ConstraintLayout expandableLayout;

        public PostEditViewholder(@NonNull View itemView, ChildMainAdapter adapter, OnImageClickListener.Listener onImageClickListener) {
            super(itemView, adapter, VIEW_TYPE_POST_EDIT, onImageClickListener);
            this.adapter = adapter;
            this.onImageClickListener = onImageClickListener;

            btnSave = itemView.findViewById(R.id.btnSave);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
        }

        @Override
        protected ChildAdapter getChildAdapter(int childMainPosition) {
            if (isEditing) {
                return new ChildAdapter(VIEW_TYPE_POST_EDIT, onImageClickListener, childMainPosition);
            }
            else {
                return new ChildAdapter(VIEW_TYPE_POST, onImageClickListener, childMainPosition);
            }
        }

        public void bind(ChildMain childMain, int childMainPosition) {
            super.bind(childMain, childMainPosition);
            tvName.setText(childMain.getChildMainName());



            // Store original state
            originalChildMainName = childMain.getChildMainName();
            originalChildItemList = new ArrayList<>(childMain.getChildItemList());

//            expandableLayout.setVisibility(childMain.isExpandable() ? View.VISIBLE : View.GONE);
//            boolean isExpanded = childMain.isExpandable();
//            expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
//            if (childMain.isExpandable()) {
//                Log.d("HELLO123,", "HELLO123");

                childAdapter = getChildAdapter(childMainPosition);
                childAdapter.setChildItemList(childMain.getChildItemList());
                childAdapter.setParentKey(adapter.parentKey);
                childAdapter.setChildMainKey(childMain.getKey());
                childMainRecyclerView.setHasFixedSize(true);
                childMainRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                childMainRecyclerView.setAdapter(childAdapter);
//            }

            EditText etName = itemView.findViewById(R.id.etChildMainName);

            // Add item button logic
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addChildItem();
                }
            });

            // Remove item button logic
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                            .setTitle("Delete entry")
                            .setMessage("Are you sure you want to delete this entry?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    int position = getAdapterPosition();
                                    if (position != RecyclerView.NO_POSITION) {
                                        adapter.deleteChildMain(position);
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });

            // Cancel edit button logic
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isEditing = false;
                    updateButtonVisibility(childMain);
                    revertChanges();
                }
            });

            // Edit button logic
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isEditing = true;
                    updateButtonVisibility(childMain);
                    enableEditing(childMain, etName);

                }
            });

            // Save button logic
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isEditing = false;
                    updateButtonVisibility(childMain);
                    saveChanges(childMain, etName);
                }
            });

            // Set initial button visibility
            updateButtonVisibility(childMain);
        }

        private void enableEditing(ChildMain childMain, EditText etName) {
            etName.setText(tvName.getText().toString());
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
                        tvName.setVisibility(View.VISIBLE);
                        etName.setVisibility(View.GONE);
                    }
                }
            });

            // Set the adapter in edit mode
            childAdapter = new ChildAdapter(2, onImageClickListener, getAdapterPosition());
            childAdapter.setParentKey(adapter.parentKey);
            childAdapter.setChildMainKey(childMain.getKey());
            childAdapter.setChildItemList(childMain.getChildItemList());
            childMainRecyclerView.setAdapter(childAdapter);
        }

        private void saveChanges(ChildMain childMain, EditText etName) {
            tvName.setText(etName.getText());
            tvName.setVisibility(View.VISIBLE);
            etName.setVisibility(View.INVISIBLE);

            childMain.setChildMainName(tvName.getText().toString());
            if (adapter.listener != null) {
                adapter.listener.onSaveButtonClick(childMain);
            }

            // Upload images and save to Firebase
            uploadChildItemImages(childMain, adapter.parentKey, childMain.getKey());

            // Set the adapter in view mode
            childAdapter = new ChildAdapter(0, onImageClickListener, getAdapterPosition());
            childAdapter.setChildItemList(childMain.getChildItemList());
            childMainRecyclerView.setAdapter(childAdapter);
        }

        private void revertChanges() {
            tvName.setText(originalChildMainName);
            childMain.setChildMainName(originalChildMainName);
            childMain.setChildItemList(new ArrayList<>(originalChildItemList));

            // Set the adapter in view mode
            childAdapter = new ChildAdapter(0, onImageClickListener, getAdapterPosition());
            childAdapter.setChildItemList(originalChildItemList);
            childMainRecyclerView.setAdapter(childAdapter);
        }

        private void uploadChildItemImages(ChildMain childMain, String parentKey, String childMainKey) {
            AtomicInteger totalChildItems = new AtomicInteger(0);
            AtomicInteger uploadCounter = new AtomicInteger(0);
            List<ChildItem> childItemList = childMain.getChildItemList();

            totalChildItems.addAndGet(childItemList.size());

            if (totalChildItems.get() == 0) {
                // No child items to upload, directly call saveChildMainToFirebase
                saveChildMainToFirebase(childMain, parentKey, childMainKey);
                return;
            }

            for (int i = 0; i < childItemList.size(); i++) {
                ChildItem childItem = childItemList.get(i);
                if (childItem.getChildImage() != null) {
                    Uri imageUri = Uri.parse(childItem.getChildImage());
                    String childItemKey = String.valueOf(i); // Using position index as the key
                    StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(parentKey).child(childMainKey).child(childItemKey + "." + getFileExtension(imageUri));

                    // Check if an existing image URL needs to be deleted
                    if (childItem.getChildImage().startsWith("https://")) {
                        StorageReference existingImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(childItem.getChildImage());
                        existingImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Existing image deleted, proceed to upload the new image
                                Log.d("Upload", "Existing image deleted");
                                uploadNewImage(fileRef, imageUri, childItem, totalChildItems, uploadCounter, parentKey, childMainKey, childMain);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure in deleting the existing image
                                Log.e("Upload", "Failed to delete existing image", e);
                                // Still proceed to upload the new image
                                uploadNewImage(fileRef, imageUri, childItem, totalChildItems, uploadCounter, parentKey, childMainKey, childMain);
                            }
                        });
                    } else {
                        // No existing image found, proceed to upload the new image
                        uploadNewImage(fileRef, imageUri, childItem, totalChildItems, uploadCounter, parentKey, childMainKey, childMain);
                    }
                } else {
                    // Increment upload counter for items without images
                    int completedUploads = uploadCounter.incrementAndGet();

                    // Check if all uploads are complete
                    if (completedUploads == totalChildItems.get()) {
                        // All child item images have been uploaded, now call saveChildMainToFirebase
                        saveChildMainToFirebase(childMain, parentKey, childMainKey);
                    }
                }
            }
        }

        private void uploadNewImage(StorageReference fileRef, Uri imageUri, ChildItem childItem, AtomicInteger totalChildItems, AtomicInteger uploadCounter, String parentKey, String childMainKey, ChildMain childMain) {
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        childItem.setChildImage(downloadUrl);

                        Log.d("ChildItemImageUpload", "Image uploaded and URL updated: " + downloadUrl);

                        // Increment upload counter
                        int completedUploads = uploadCounter.incrementAndGet();

                        // Check if all uploads are complete
                        if (completedUploads == totalChildItems.get()) {
                            // All child item images have been uploaded, now call saveChildMainToFirebase
                            saveChildMainToFirebase(childMain, parentKey, childMainKey);
                        }
                    }))
                    .addOnFailureListener(e -> {
                        Log.e("ChildItemImageUpload", "Failed to upload image for ChildItem", e);

                        // Increment upload counter even on failure to avoid hanging
                        int completedUploads = uploadCounter.incrementAndGet();

                        // Check if all uploads are complete
                        if (completedUploads == totalChildItems.get()) {
                            // All child item images have been uploaded, now call saveChildMainToFirebase
                            saveChildMainToFirebase(childMain, parentKey, childMainKey);
                        }
                    });
        }

        private void saveChildMainToFirebase(ChildMain childMain, String parentKey, String childMainKey) {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference childMainRef = databaseRef.child("Posts").child(parentKey).child("childData").child(childMainKey);

            childMainRef.setValue(childMain).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("FirebaseSave", "ChildMain saved successfully.");
                    adapter.notifyItemChanged(getAdapterPosition());
                } else {
                    Log.e("FirebaseSave", "Failed to save ChildMain.", task.getException());
                }
            });
        }

        // for uploading images (general)
        private String getFileExtension(Uri imUri) {
            ContentResolver cr = itemView.getContext().getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cr.getType(imUri));
        }
        private void addChildItem() {
            ChildItem newChildItem = new ChildItem("New Item", "New Item description", "New Item image");
            List<ChildItem> childData = childMain.getChildItemList();

            childData.add(newChildItem);
            childMain.setChildItemList(childData);

            childAdapter.setChildItemList(childData);
            childAdapter.notifyItemInserted(childData.size() - 1);
            childMainRecyclerView.scrollToPosition(childData.size() - 1);
        }

        public void updateChildItemImage(int itemPosition, String imageUrl) {
            RecyclerView childRecyclerView = itemView.findViewById(R.id.childMainRecyclerView);
            RecyclerView.ViewHolder childViewHolder = childRecyclerView.findViewHolderForAdapterPosition(itemPosition);
            if (childViewHolder instanceof ChildAdapter.PostEditViewHolder) {
                ChildAdapter.PostEditViewHolder childVH = (ChildAdapter.PostEditViewHolder) childViewHolder;
                // Update the image using Glide or any other image loading library
                Glide.with(childVH.childImageView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_profile)
                        .into(childVH.childImageView);
            }
        }

        //currently not working
        // Method to update button visibility based on edit state
        public void updateButtonVisibility(ChildMain childMain) {
            boolean isExpanded = childMain.isExpandable();
            if (isEditing) {
                btnAdd.setVisibility(View.VISIBLE);
                btnRemove.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.INVISIBLE);
                btnSave.setVisibility(View.VISIBLE);
            } else {
                btnAdd.setVisibility(View.INVISIBLE);
                btnRemove.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);
                btnEdit.setVisibility(isExpanded ? View.VISIBLE : View.INVISIBLE); // Make btnEdit visible only if expanded
                btnSave.setVisibility(View.INVISIBLE);
            }
        }
    }



    public static class PostCreationViewHolder extends BaseViewHolder {
        private TextView tvName;
        private EditText etName;
        private Button childMainButton, btnDelete;
        private ChildAdapter childAdapter;
        private OnImageClickListener.Listener onImageClickListener;
        private ChildMainAdapter adapter;
        private ConstraintLayout expandableLayout;

        public PostCreationViewHolder(@NonNull View itemView, ChildMainAdapter adapter, OnImageClickListener.Listener onImageClickListener) {
            super(itemView, adapter, VIEW_TYPE_POST_CREATION, onImageClickListener);
            this.adapter = adapter;
            this.onImageClickListener = onImageClickListener;
            tvName = itemView.findViewById(R.id.eachChildMainName); // Make sure this ID is correct
            etName = itemView.findViewById(R.id.etChildMainName);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);



            childMainRecyclerView.setHasFixedSize(true);
            childMainRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 1));

            //button to delete
            btnDelete = itemView.findViewById(R.id.childMainbtnDelete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteMain();
                }
            });

            //button to add child item
            childMainButton = itemView.findViewById(R.id.btnAdd);
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
                    if (isRecyclerViewExpanded()) {
                        tvName.setVisibility(View.INVISIBLE);
                        etName.setVisibility(View.VISIBLE);
                        etName.requestFocus();
                    }
                }
            });

            etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        saveAndUpdateName();
                    }
                }
            });
        }

        @Override
        public void bind(ChildMain childMain, int childMainPosition){
            super.bind(childMain, childMainPosition);
            //tvName.setText(childMain.getChildMainName());
            this.childMain = childMain; // Ensure the instance variable is set

            boolean isExpanded = childMain.isExpandable();
            expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

//            if (childMain.isExpandable()) {
//                ChildAdapter childAdapter = getChildAdapter(childMainPosition);
//                childAdapter.setChildItemList(childMain.getChildItemList());
//                childMainRecyclerView.setHasFixedSize(true);
//                childMainRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
//                childMainRecyclerView.setAdapter(childAdapter);
//            }

            tvName.setText(childMain.getChildMainName());
            etName.setText(childMain.getChildMainName());
            childAdapter = new ChildAdapter(1, onImageClickListener, getAdapterPosition());
            childAdapter.setChildItemList(childMain.getChildItemList());
            childMainRecyclerView.setAdapter(childAdapter);
            childAdapter.notifyDataSetChanged();

            updateButtonVisibility(childMain);
        }
        private void saveAndUpdateName() {
            String name = etName.getText().toString().trim();
            childMain.setChildMainName(name);
            tvName.setText(name);
        }
        private boolean isRecyclerViewExpanded() {
            return childMain.isExpandable();
        }

        public void updateButtonVisibility(ChildMain childMain) {
            boolean isExpanded = childMain.isExpandable();
            childMainButton.setVisibility(isExpanded ? View.VISIBLE : View.INVISIBLE);
            btnDelete.setVisibility(isExpanded ? View.VISIBLE : View.INVISIBLE);

            if (isExpanded) {
                etName.setVisibility(View.VISIBLE);
                tvName.setVisibility(View.INVISIBLE);
            } else {
                saveAndUpdateName();
                etName.setVisibility(View.GONE);
                tvName.setVisibility(View.VISIBLE);
            }
        }

        private void addChildItem() {
            ChildItem newChildItem = new ChildItem("New Item", "New Item description", "New Item image");
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
        private void deleteMain() {
            AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext())
                    .setTitle("Delete entry")
                    .setMessage("Are you sure you want to delete this entry?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Get the adapter position of this ViewHolder
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            // Ensure the listener is not null and then call the method
                            if (position != RecyclerView.NO_POSITION) {
                                adapter.deleteMain(position);
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        @Override
        protected ChildAdapter getChildAdapter(int childMainPosition) {
            return new ChildAdapter(VIEW_TYPE_POST_CREATION, onImageClickListener, childMainPosition);
        }
    }


}
