package sg.edu.np.mad.travelhub;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
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

    private List<ChildMain> childMainList;
    private int viewType;
    private String parentKey;
    public static OnChildMainInteractionListener listener;

    private OnImageClickListener.Listener onImageClickListener;
    private RecyclerView recyclerView;


    public ChildMainAdapter(int viewType, OnImageClickListener.Listener onImageClickListener, RecyclerView recyclerView){

        this.viewType = viewType;
        //to be deleted
        this.childMainList = new ArrayList<>();
        this.onImageClickListener = onImageClickListener;
        this.recyclerView = recyclerView;
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
            return new PostViewHolder(view);
        } else if (viewType == VIEW_TYPE_POST_CREATION) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_childmain_item_create, parent, false);
            return new PostCreationViewHolder(view, onImageClickListener, this);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_childmain_item_edit, parent, false);
            return new PostEditViewholder(view, this, onImageClickListener);
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
                        editViewHolder.updateButtonVisibility();
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
//        if (position >= 0 && position < childMainList.size()) {
//            childMainList.remove(position);
//            notifyItemRemoved(position);
//            notifyItemRangeChanged(position, childMainList.size());
//        }
        Log.d("DBREFERENCE", String.valueOf(FirebaseDatabase.getInstance().getReference()));
        if (position >= 0 && position < childMainList.size()) {
            // Get the key of the item to be deleted
            String keyToDelete = childMainList.get(position).getKey();

            Log.d("KEYTODELETE", keyToDelete);
            // Remove the item from the list
            childMainList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, childMainList.size());

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
            ChildAdapter childAdapter = new ChildAdapter(0, null, getAdapterPosition());
            childAdapter.setChildItemList(childMain.getChildItemList());
            childMainRecyclerView.setHasFixedSize(true);
            childMainRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 1));
            childMainRecyclerView.setAdapter(childAdapter);
        }
    }

    public static class PostEditViewholder extends BaseViewHolder {
        private ChildMainAdapter adapter;
        private String originalChildMainName;
        private List<ChildItem> originalChildItemList;
        private ChildAdapter childAdapter;
        private String parentKey;
        private OnImageClickListener.Listener onImageClickListener;
        public Button btnSave;
        public Button btnEdit;
        private Button btnAdd;
        private Button btnRemove;
        private Button btnCancel;
        private boolean isEditing; // Track the edit state

        public PostEditViewholder(@NonNull View itemView, ChildMainAdapter adapter, OnImageClickListener.Listener onImageClickListener) {
            super(itemView);
            this.adapter = adapter;
            this.onImageClickListener = onImageClickListener;

            btnSave = itemView.findViewById(R.id.btnSave);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }

        public void bind(ChildMain childMain) {
            super.bind(childMain);

            // Store original state
            originalChildMainName = childMain.getChildMainName();
            originalChildItemList = new ArrayList<>();
            for (ChildItem item : childMain.getChildItemList()) {
                originalChildItemList.add(new ChildItem(item.getChildName(), item.getChildImage())); // Assuming ChildItem has a copy constructor
            }
            Log.d("BindMethod", "childMainName: " + childMain.getChildMainName());

            tvName.setText(childMain.getChildMainName());
            childAdapter = new ChildAdapter(0, onImageClickListener, getAdapterPosition());

            childAdapter.setChildItemList(childMain.getChildItemList());

            childMainRecyclerView.setHasFixedSize(true);
            childMainRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 1));
            childMainRecyclerView.setAdapter(childAdapter);

            EditText etName = itemView.findViewById(R.id.etChildMainName);

            // btn to add item
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addChildItem();
                }
            });

            //btn to remove the list
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

            //btn to cancel edit
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isEditing = false;
                    btnCancel.setVisibility(View.INVISIBLE);
                    btnAdd.setVisibility(View.INVISIBLE);
                    btnSave.setVisibility(View.INVISIBLE);
                    btnEdit.setVisibility(View.VISIBLE);
                    btnRemove.setVisibility(View.INVISIBLE);

                    // Revert changes
                    tvName.setText(originalChildMainName);
                    childMain.setChildMainName(originalChildMainName);
                    childMain.setChildItemList(new ArrayList<>());
                    for (ChildItem item : originalChildItemList) {
                        childMain.getChildItemList().add(new ChildItem(item.getChildName(), item.getChildImage())); // Assuming ChildItem has a copy constructor
                    }
                    //reset recyclerview
                    childAdapter = new ChildAdapter(0, onImageClickListener, getAdapterPosition());
                    childAdapter.setChildItemList(originalChildItemList);
                    childMainRecyclerView.setHasFixedSize(true);
                    childMainRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 1));
                    childMainRecyclerView.setAdapter(childAdapter);
                }
            });

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("EDIT BUTTON", "edit button pressed");
                    isEditing = true;
                    //set name already entered to edit text to prevent it from disappearing
                    etName.setText(tvName.getText().toString());

                    btnAdd.setVisibility(View.VISIBLE);
                    btnRemove.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                    btnEdit.setVisibility(View.INVISIBLE);
                    btnSave.setVisibility(View.VISIBLE);

                    //name edit
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

                    //recyclerview edit
                    childAdapter = new ChildAdapter(2, onImageClickListener, getAdapterPosition());
                    childAdapter.setParentKey(adapter.parentKey);
                    childAdapter.setChildMainKey(childMain.getKey());
                    childAdapter.setChildItemList(childMain.getChildItemList());
                    childMainRecyclerView.setHasFixedSize(true);
                    childMainRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 1));
                    childMainRecyclerView.setAdapter(childAdapter);
                }
            });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isEditing = false;
                    btnAdd.setVisibility(View.INVISIBLE);
                    btnRemove.setVisibility(View.INVISIBLE);
                    btnCancel.setVisibility(View.INVISIBLE);
                    btnEdit.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.INVISIBLE);

                    tvName.setText(etName.getText());
                    tvName.setVisibility(View.VISIBLE);
                    etName.setVisibility(View.INVISIBLE);

                    childMain.setChildMainName(tvName.getText().toString());

                    // Trigger the callback to save the data
                    if (listener != null) {
                        listener.onSaveButtonClick(childMain);
                    }

                    // Upload images and save to Firebase
                    uploadChildItemImages(childMain, adapter.parentKey, childMain.getKey());


                    //recyclerview edit
                    childAdapter = new ChildAdapter(0, onImageClickListener, getAdapterPosition());
                    childAdapter.setChildItemList(childMain.getChildItemList());
                    childMainRecyclerView.setHasFixedSize(true);
                    childMainRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 1));
                    childMainRecyclerView.setAdapter(childAdapter);
                }
            });
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
            ChildItem newChildItem = new ChildItem("New Item", "New Item image");
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
        public void updateButtonVisibility() {
            Log.d("doesthisrun2", String.valueOf(isEditing));
            if (isEditing) {
                Log.d("doesthisrun3", String.valueOf(View.INVISIBLE));
                btnEdit.setVisibility(View.INVISIBLE);

                btnSave.setVisibility(View.VISIBLE);
                Log.d("doesthisrun3", String.valueOf(btnSave.getVisibility()));

                btnAdd.setVisibility(View.VISIBLE);
                btnRemove.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
            } else {
                btnSave.setVisibility(View.INVISIBLE);
                btnEdit.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.INVISIBLE);
                btnRemove.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);
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

        public PostCreationViewHolder(@NonNull View itemView, OnImageClickListener.Listener onImageClickListener, ChildMainAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            this.onImageClickListener = onImageClickListener;
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
                    deleteMain();
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

            childAdapter = new ChildAdapter(1, onImageClickListener, getAdapterPosition());
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
            //Has to use int = 2 when adding to existing items in recyclerview

            //if new post, can use int = 1
//            int maxKeyIndex = childData.size() + 1;
//            String newKey = "ChildItem" + (maxKeyIndex);
//
//            Log.d("ChildKey1", "NEWLINE");
//            for (String key : childMain.getChildItemList().keySet()) {
//                Log.d("ChildKey", "Key: " + key);
//            }
//
//            childData.put(newKey, newChildItem);
//            childMain.setChildData(childData);
//
//            List<ChildItem> updatedChildItemList = childMain.getChildItemList();
//            childAdapter.setChildItemList(updatedChildItemList);
//            childAdapter.notifyItemInserted(updatedChildItemList.size() - 1);
//            childMainRecyclerView.scrollToPosition(updatedChildItemList.size() - 1);
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
    }


}
