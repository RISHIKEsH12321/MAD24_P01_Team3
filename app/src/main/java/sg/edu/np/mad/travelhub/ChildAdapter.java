package sg.edu.np.mad.travelhub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.BaseViewHolder> {
    private static final int VIEW_TYPE_POST = 0;
    private static final int VIEW_TYPE_POST_CREATION = 1;
    private int viewType;

    private List<ChildItem> childItemList;
    public ChildAdapter(int viewType){
        this.viewType = viewType;
        this.childItemList = new ArrayList<>();
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
            return new PostCreationViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_child_item_edit, parent, false);
            return new PostEditViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        ChildItem childItem = childItemList.get(position);
        holder.bind(childItem);
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


    public static class BaseViewHolder extends RecyclerView.ViewHolder{
        protected TextView tvName, tvDescription;
        protected ImageView childImageView;
        protected ChildItem childItem;
        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvChildMainName);
            tvDescription = itemView.findViewById(R.id.tvChildMainDescription);
            childImageView = itemView.findViewById(R.id.eachChildItemIV);
        }

        public void bind(ChildItem childItem) {
            this.childItem = childItem;
        }
    }

    public static class PostViewHolder extends BaseViewHolder{
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvChildItemName);
        }

        @Override public void bind(ChildItem childItem){
            super.bind(childItem);
            tvName.setText(childItem.getChildName());
        }
    }


    public static class PostCreationViewHolder extends BaseViewHolder{

        private EditText etName, etDescription;
        public PostCreationViewHolder(@NonNull View itemView) {
            super(itemView);
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
        }
    }

    public static class PostEditViewHolder extends BaseViewHolder{

        private EditText etName, etDescription;
        public PostEditViewHolder(@NonNull View itemView) {
            super(itemView);

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
        }
        @Override public void bind(ChildItem childItem){
            super.bind(childItem);
            tvName.setText(childItem.getChildName());


        }
    }
}