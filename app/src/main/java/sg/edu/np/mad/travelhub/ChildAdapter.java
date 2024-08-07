package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.BaseViewHolder> {
    private static final int VIEW_TYPE_POST = 0;
    private static final int VIEW_TYPE_POST_CREATION = 1;
    private static final int VIEW_TYPE_POST_EDIT = 1;
    private int viewType;
    private List<ChildItem> childItemList;
    public String parentKey;
    public String childMainKey;
    private OnImageClickListener.Listener onImageClickListener;
    private int childMainPosition;
    private Context context;
    private SharedPreferences preferences;
    public ChildAdapter(int viewType, OnImageClickListener.Listener onImageClickListener, int childMainPosition, Context context){
        this.viewType = viewType;
        this.onImageClickListener = onImageClickListener;
        this.childItemList = new ArrayList<>();
        this.childMainPosition = childMainPosition;
        this.context = context;
        this.preferences = context.getSharedPreferences("spinner_preferences", Context.MODE_PRIVATE);
    }
    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public String getChildMainKey() {
        return childMainKey;
    }

    public void setChildMainKey(String childMainKey) {
        this.childMainKey = childMainKey;
    }
    public void setChildItemList(List<ChildItem> childItemList){
        if (childItemList != null) {
            this.childItemList = childItemList;
        } else {
            this.childItemList = new ArrayList<>();
        }
        //this.childItemList.removeAll(Collections.singleton(null));
    }
    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_child_item , parent, false);
//        return new ChildViewHolder(view);
        View view;
        if (viewType == VIEW_TYPE_POST) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_child_item, parent, false);
            return new PostViewHolder(view);
        } else if (viewType == VIEW_TYPE_POST_CREATION){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_child_item_create, parent, false);
            return new PostCreationViewHolder(view, onImageClickListener, this);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_child_item_edit, parent, false);
            return new PostEditViewHolder(view, parentKey, childMainKey, this, onImageClickListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        ChildItem childItem = childItemList.get(position);
        holder.bind(childItem, childItemList, childMainPosition);
        //holder.childName.setText(childItem.getChildName());
        //Glide.with(holder.itemView.getContext()).load(childItem.getChildImage())
        //.into(holder.childImageView);

        // Get saved theme preference
        int selectedSpinnerPosition = preferences.getInt("selected_spinner_position", 0);
        String selectedTheme = context.getResources().getStringArray(R.array.themes)[selectedSpinnerPosition];

        int color1, color2, color3;
        // Set colors based on the selected theme
        switch (selectedTheme) {
            case "Default":
                color1 = context.getResources().getColor(R.color.main_orange);
                color2 = context.getResources().getColor(R.color.main_orange);
                color3 = context.getResources().getColor(R.color.main_orange_bg);
                break;
            case "Watermelon":
                color1 = context.getResources().getColor(R.color.wm_green);
                color2 = context.getResources().getColor(R.color.wm_red);
                color3 = context.getResources().getColor(R.color.wm_red_bg);
                break;
            case "Neon":
                color1 = context.getResources().getColor(R.color.nn_pink);
                color2 = context.getResources().getColor(R.color.nn_cyan);
                color3 = context.getResources().getColor(R.color.nn_cyan_bg);
                break;
            case "Protanopia":
                color1 = context.getResources().getColor(R.color.pro_purple);
                color2 = context.getResources().getColor(R.color.pro_green);
                color3 = context.getResources().getColor(R.color.pro_green_bg);
                break;
            case "Deuteranopia":
                color1 = context.getResources().getColor(R.color.deu_yellow);
                color2 = context.getResources().getColor(R.color.deu_blue);
                color3 = context.getResources().getColor(R.color.deu_blue_bg);
                break;
            case "Tritanopia":
                color1 = context.getResources().getColor(R.color.tri_orange);
                color2 = context.getResources().getColor(R.color.tri_green);
                color3 = context.getResources().getColor(R.color.tri_green_bg);
                break;
            default:
                color1 = context.getResources().getColor(R.color.main_orange);
                color2 = context.getResources().getColor(R.color.main_orange);
                color3 = context.getResources().getColor(R.color.main_orange_bg);
                break;
        }

        // Set background tint for buttons
        ColorStateList colorStateList = ColorStateList.valueOf(color1);

        if (holder instanceof PostCreationViewHolder) {
            ((PostCreationViewHolder) holder).btnDelete.setBackgroundTintList(colorStateList);
        }
        else if (holder instanceof PostEditViewHolder) {
            ((PostEditViewHolder) holder).btnDelete.setBackgroundTintList(colorStateList);
        }
    }


    @Override
    public int getItemCount() {
        if (childItemList != null){
            return childItemList.size();
        }else{
            return  0;
        }
    }

    public void deleteFromFirebase(int position) {
        Log.d("POSITION", String.valueOf(parentKey));
        Log.d("POSITION", String.valueOf(childMainKey));

        Log.d("POSITION", String.valueOf(position));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Posts")
                .child(parentKey)
                .child("ChildData")
                .child(childMainKey)
                .child("childItemList")
                .child(String.valueOf(position));

        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Remove the item from the list and notify the adapter
                childItemList.remove(position);
                notifyItemRemoved(position);
                Log.d("DeleteItem", "Item deleted successfully from position " + position);
            } else {
                Log.e("DeleteItem", "Failed to delete item: " + task.getException());
            }
        });

        // Get the image URL of the item to be deleted
        String imageUrl = childItemList.get(position).getChildImage();

        if (!imageUrl.contains("content://") && !imageUrl.isEmpty()) {
            Log.d("logimageurl", imageUrl);
            // Reference to the Firebase Storage item to be deleted
            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

            // Delete the image from Firebase Storage
            imageRef.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("DeleteImage", "Image deleted successfully from Storage");
                    // If image deletion is successful, delete the item from the Realtime Database
                    //deleteChildItemFromDatabase(databaseReference, position);
                } else {
                    Log.e("DeleteImage", "Failed to delete image: " + task.getException());
                }
            });
        } else {
            // If there is no image URL, just delete the item from the Realtime Database
            //deleteChildItemFromDatabase(databaseReference, position);
        }
    }
    public void updateImage(int position, Uri imageUri) {
        childItemList.get(position).setChildImage(String.valueOf(imageUri));
        notifyItemChanged(position);
    }


    public static class BaseViewHolder extends RecyclerView.ViewHolder{
        protected TextView tvName, tvDescription;
        protected ImageView childImageView;
        protected ChildItem childItem;
        protected List<ChildItem> childItemList;
        protected int childMainPosition;

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.eachChildMainName);
            tvDescription = itemView.findViewById(R.id.tvChildItemDescription);
            childImageView = itemView.findViewById(R.id.eachChildItemIV);
        }

        public void bind(ChildItem childItem, List<ChildItem> childItemList, int childMainPosition) {

            this.childItem = childItem;
            this.childItemList = childItemList;
            this.childMainPosition = childMainPosition;
        }
    }

    public static class PostViewHolder extends BaseViewHolder{

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvChildItemName);
        }

        @Override public void bind(ChildItem childItem, List<ChildItem> childItemList, int childMainPosition){
            super.bind(childItem, childItemList, childMainPosition);
            tvName.setText(childItem.getChildName());
            tvDescription.setText(childItem.getChildDescription());
            Glide.with(childImageView.getContext())
                    .load(childItem.getChildImage())
                    .placeholder(R.drawable.ic_image) // Placeholder image
                    .into(childImageView);        }
    }


    public static class PostCreationViewHolder extends BaseViewHolder {

        private EditText etName, etDescription;
        public ImageView childImageView;
        private List<ChildItem> childItemList;
        private OnImageClickListener.Listener onImageClickListener;
        private int childMainPosition;
        private AppCompatButton btnDelete;
        ChildAdapter childAdapter;

        public PostCreationViewHolder(@NonNull View itemView, OnImageClickListener.Listener onImageClickListener, ChildAdapter childAdapter) {
            super(itemView);
            this.onImageClickListener = onImageClickListener;
            this.childAdapter = childAdapter;
            childImageView = itemView.findViewById(R.id.eachChildItemIV);
            tvName = itemView.findViewById(R.id.eachChildMainName);
            etName = itemView.findViewById(R.id.etChildMainName);
            etDescription = itemView.findViewById(R.id.etChildItemDescription);

            // Changing of name and description
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
                        childItem.setChildName(String.valueOf(etName.getText()));
                        tvName.setVisibility(View.VISIBLE);
                        etName.setVisibility(View.GONE);
                    }
                }
            });

            tvDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvDescription.setVisibility(View.INVISIBLE);
                    etDescription.setVisibility(View.VISIBLE);
                    etDescription.requestFocus();
                }
            });

            etDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        tvDescription.setText(etDescription.getText());
                        childItem.setChildDescription(String.valueOf(etDescription.getText()));
                        tvDescription.setVisibility(View.VISIBLE);
                        etDescription.setVisibility(View.GONE);
                    }
                }
            });

            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteChildItem(getBindingAdapterPosition());
                }
            });
        }

        @Override
        public void bind(ChildItem childItem, List<ChildItem> childItemList, int childMainPosition) {
            super.bind(childItem, childItemList, childMainPosition);
            this.childItemList = childItemList;  // Ensure childItemList is assigned
            this.childMainPosition = childMainPosition;

            int position = getAdapterPosition();

            tvName.setText(childItem.getChildName());
            tvDescription.setText(childItem.getChildDescription());
            childImageView.setOnClickListener(v -> {
                if (onImageClickListener != null) {
                    Log.d("Imageclicked", "image");
                    onImageClickListener.onImageClick(childMainPosition, position);
                }
            });

            // Check if childItem has an image URL, otherwise set default image
            if (childItem.getChildImage() != null && !childItem.getChildImage().isEmpty()) {
                childImageView.setImageURI(Uri.parse(childItem.getChildImage()));
                Log.d("imageurichild", String.valueOf(Uri.parse(childItem.getChildImage())));
            } else {
                childImageView.setImageResource(R.drawable.ic_image); // Set default image
            }
        }
        // Method to delete a ChildItem
        public void deleteChildItem(int childPosition) {
            if (childPosition >= 0 && childPosition < childItemList.size()) {
                childItemList.remove(childPosition);
                childAdapter.notifyItemRemoved(childPosition);
                childAdapter.notifyItemRangeChanged(childPosition, childItemList.size());
            }
        }
        // Ensure childAdapter is set when the ViewHolder is created or bound
        public void setChildAdapter(ChildAdapter childAdapter) {
            this.childAdapter = childAdapter;
        }
    }


    public static class PostEditViewHolder extends BaseViewHolder {
        private ChildAdapter adapter;
        private EditText etName, etDescription;
        private String parentKey;
        private String childMainKey;
        public ImageView childImageView;
        private TextView tvName, tvDescription;
        private OnImageClickListener.Listener onImageClickListener;
        private AppCompatButton btnDelete;
        private int childMainPosition;

        public PostEditViewHolder(@NonNull View itemView, String parentKey, String childMainKey, ChildAdapter adapter, OnImageClickListener.Listener onImageClickListener) {
            super(itemView);
            this.parentKey = parentKey;
            this.childMainKey = childMainKey;
            this.adapter = adapter;
            this.onImageClickListener = onImageClickListener;
            this.childMainPosition = childMainPosition;
            childImageView = itemView.findViewById(R.id.eachChildItemIV);
            tvName = itemView.findViewById(R.id.eachChildMainName);
            etName = itemView.findViewById(R.id.etChildMainName);
            etDescription = itemView.findViewById(R.id.etChildItemDescription);
            tvDescription = itemView.findViewById(R.id.tvChildItemDescription);

            // Image click listener
//            childImageView.setOnClickListener(v -> {
//                if (onImageClickListener != null) {
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION) {
//                        onImageClickListener.onImageClick(childMainPosition, position);
//                    }
//                }
//            });

            // Changing of name and description
            tvName.setOnClickListener(v -> {
                tvName.setVisibility(View.INVISIBLE);
                etName.setVisibility(View.VISIBLE);
                etName.requestFocus();
            });

            etName.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    tvName.setText(etName.getText());
                    // Update the child item name
                    childItem.setChildName(String.valueOf(etName.getText()));
                    tvName.setVisibility(View.VISIBLE);
                    etName.setVisibility(View.GONE);
                }
            });

            tvDescription.setOnClickListener(v -> {
                tvDescription.setVisibility(View.INVISIBLE);
                etDescription.setVisibility(View.VISIBLE);
                etDescription.requestFocus();
            });

            etDescription.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    tvDescription.setText(etDescription.getText());
                    childItem.setChildDescription(String.valueOf(etDescription.getText()));
                    tvDescription.setVisibility(View.VISIBLE);
                    etDescription.setVisibility(View.GONE);
                }
            });

            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    adapter.deleteFromFirebase(position);
                }
            });
        }

        @Override
        public void bind(ChildItem childItem, List<ChildItem> childItemList, int childMainPosition) {
            super.bind(childItem, childItemList, childMainPosition);
            this.childItemList = childItemList;
            this.childMainPosition = childMainPosition;

            tvName.setText(childItem.getChildName());
            tvDescription.setText(childItem.getChildDescription());
            int position = getAdapterPosition();

            childImageView.setOnClickListener(v -> {
                if (onImageClickListener != null) {
                    Log.d("Imageclicked", "image");
                    onImageClickListener.onImageClick(childMainPosition, position);
                }
            });

            // Check if childItem has an image URL, otherwise set default image
            if (childItem.getChildImage() != null && !childItem.getChildImage().isEmpty()) {
                childImageView.setImageURI(Uri.parse(childItem.getChildImage()));
                Log.d("imageurichild", String.valueOf(Uri.parse(childItem.getChildImage())));
            } else {
                childImageView.setImageResource(R.drawable.ic_image); // Set default image
            }
            // Use Glide to load the image
            Glide.with(childImageView.getContext())
                    .load(childItem.getChildImage())
                    .placeholder(R.drawable.ic_image) // Placeholder image
                    .into(childImageView);
            Log.d("childImageView", childItem.getChildImage());
        }

        public void setImage(String imageUrl) {
            Glide.with(childImageView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_image)
                    .into(childImageView);
        }
    }



}