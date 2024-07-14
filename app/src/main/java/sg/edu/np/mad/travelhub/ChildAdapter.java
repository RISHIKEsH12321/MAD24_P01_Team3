package sg.edu.np.mad.travelhub;

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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.BaseViewHolder> {
    private static final int VIEW_TYPE_POST = 0;
    private static final int VIEW_TYPE_POST_CREATION = 1;
    private int viewType;
    private List<ChildItem> childItemList;
    public String parentKey;
    public String childMainKey;
    private PostCreation.OnImageClickListener onImageClickListener;
    private int childMainPosition;

    public ChildAdapter(int viewType, PostCreation.OnImageClickListener onImageClickListener, int childMainPosition){
        this.viewType = viewType;
        this.onImageClickListener = onImageClickListener;
        this.childItemList = new ArrayList<>();
        this.childMainPosition = childMainPosition;
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
            return new PostCreationViewHolder(view, onImageClickListener);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_child_item_edit, parent, false);
            return new PostEditViewHolder(view, parentKey, childMainKey, this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        ChildItem childItem = childItemList.get(position);
        holder.bind(childItem, childItemList, childMainPosition);
        //holder.childName.setText(childItem.getChildName());
        //Glide.with(holder.itemView.getContext()).load(childItem.getChildImage())
        //.into(holder.childImageView);
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
            tvName = itemView.findViewById(R.id.tvChildMainName);
            tvDescription = itemView.findViewById(R.id.tvChildMainDescription);
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
        }
    }


    public static class PostCreationViewHolder extends BaseViewHolder {

        private EditText etName, etDescription;
        private ImageView ivImage;
        private List<ChildItem> childItemList;
        private PostCreation.OnImageClickListener onImageClickListener;
        private int childMainPosition;

        public PostCreationViewHolder(@NonNull View itemView, PostCreation.OnImageClickListener onImageClickListener) {
            super(itemView);
            this.onImageClickListener = onImageClickListener;

            ivImage = itemView.findViewById(R.id.eachChildItemIV);
            tvName = itemView.findViewById(R.id.tvChildMainName);
            etName = itemView.findViewById(R.id.etChildMainName);
            etDescription = itemView.findViewById(R.id.etChildMainDescription);

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
                        tvDescription.setVisibility(View.VISIBLE);
                        etDescription.setVisibility(View.GONE);
                    }
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
            ivImage.setOnClickListener(v -> {
                if (onImageClickListener != null) {
                    Log.d("Imageclicked", "image");
                    onImageClickListener.onImageClick(childMainPosition, position);
                }
            });

            // Check if childItem has an image URL, otherwise set default image
            if (childItem.getChildImage() != null && !childItem.getChildImage().isEmpty()) {
                ivImage.setImageURI(Uri.parse(childItem.getChildImage()));
                Log.d("imageurichild", String.valueOf(Uri.parse(childItem.getChildImage())));
            } else {
                ivImage.setImageResource(R.drawable.ic_profile); // Set default image
            }
        }
    }


    public static class PostEditViewHolder extends BaseViewHolder{
        private ChildAdapter adapter;
        private EditText etName, etDescription;
        private String parentKey;
        private String childMainKey;
        public PostEditViewHolder(@NonNull View itemView, String parentKey, String childMainKey, ChildAdapter adapter) {
            super(itemView);
            this.parentKey = parentKey;
            this.childMainKey = childMainKey;
            this.adapter = adapter;
            tvName = itemView.findViewById(R.id.tvChildMainName);
            etName = itemView.findViewById(R.id.etChildMainName);
            etDescription = itemView.findViewById(R.id.etChildMainDescription);




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
                        tvDescription.setVisibility(View.VISIBLE);
                        etDescription.setVisibility(View.GONE);
                    }
                }
            });

            Button btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        adapter.deleteFromFirebase(position);
                    }
                }
            });
        }
        @Override
        public void bind(ChildItem childItem, List<ChildItem> childItemList, int childMainPosition){
            super.bind(childItem, childItemList, childMainPosition);

            tvName.setText(childItem.getChildName());


        }
    }

}