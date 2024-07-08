package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder>{
    Context context;
    List<User> users;
    private FirebaseUser firebaseUser;

    public UserAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }


    public void updateList(List<User> filteredList) {
        this.users = filteredList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // Assign values to views according to position of view
        User user = users.get(position);
        //if the user object is the currently logged in user, exit this function

        holder.id.setText(user.getId());
        holder.name.setText(user.getName());
        Glide.with(context)
                .load(user.getImageUrl())
                .into(holder.profImage);

        holder.userDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent userProfile = new Intent(context, OtherUserProfile.class);
                userProfile.putExtra("userUid", user.getUid());
                context.startActivity(userProfile);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Number of recycler views
        return users.size();
    }

//    public int onClick() {
//
//    }
}
