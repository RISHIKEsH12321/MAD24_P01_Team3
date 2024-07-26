package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{
    private Context mContext;
    private List<Comment> mData;

    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.each_comment,parent,false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Glide.with(mContext)
                .load(mData.get(position).getUimg())
                .transform(new CircleCrop()) // Apply the CircleCrop transformation
                .into(holder.img_user);
        holder.tv_name.setText(mData.get(position).getUname());
        holder.tv_content.setText(mData.get(position).getContent());
//        Log.d("TimestampCheck123", "Timestamp type: " + (mData.get(position).getTimeStamp() == null ? "null" : mData.get(position).getTimeStamp().getClass().getName()));
//        Log.d("TimestampCheck123", "Timestamp value: " + mData.get(position).getTimeStamp());


//        Log.d("timestampcheck4", mData.get(position).getTimeStamp());
        holder.tv_date.setText(timestampToString((Long)mData.get(position).getTimeStamp()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        ImageView img_user;
        TextView tv_name, tv_content, tv_date;

        public CommentViewHolder(View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.comment_user_img);
            tv_name = itemView.findViewById(R.id.comment_username);
            tv_content = itemView.findViewById(R.id.comment_content);
            tv_date = itemView.findViewById(R.id.comment_date);
        }
    }

    private String timestampToString(long time) {

//        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
//        calendar.setTimeInMillis(time);
//        String date = DateFormat.format("MMM dd, yyyy",calendar).toString();
//        return date;

        // Get the current time in milliseconds
        long currentTime = System.currentTimeMillis();

        // Calculate the time difference in milliseconds
        long diff = currentTime - time;

        // Calculate the time difference in minutes, hours, days, and weeks
        long diffInMinutes = diff / (1000 * 60);
        long diffInHours = diffInMinutes / 60;
        long diffInDays = diffInHours / 24;
        long diffInWeeks = diffInDays / 7;

        if (diffInMinutes < 60) {
            // Less than 1 hour, display minutes ago
            return diffInMinutes + (diffInMinutes == 1 ? " minute ago" : " minutes ago");
        } else if (diffInHours < 24) {
            // Less than 24 hours, display hours ago
            return diffInHours + (diffInHours == 1 ? " hour ago" : " hours ago");
        } else if (diffInDays < 7) {
            // More than 24 hours but less than 1 week, display days ago
            return diffInDays + (diffInDays == 1 ? " day ago" : " days ago");
        } else {
            // More than 1 week, display the date in MMM dd, yyyy format
            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            calendar.setTimeInMillis(time);
            return DateFormat.format("MMM dd, yyyy", calendar).toString();
        }
    }
}


