package sg.edu.np.mad.travelhub;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.media.Rating;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Place_Reviews_Recyclerview_Adapter extends RecyclerView.Adapter<Place_Reviews_Recyclerview_Adapter.MyViewHolder> {
    Context context;
    ArrayList<PlaceReview> placeReviews;

    public Place_Reviews_Recyclerview_Adapter(Context context, ArrayList<PlaceReview> placeReviews){
        this.context = context;
        this.placeReviews = placeReviews;
        setupTheme();
    }
    int color1;
    int color2;
    private void setupTheme(){
        SharedPreferences preferences = context.getSharedPreferences("spinner_preferences", Context.MODE_PRIVATE);
        int selectedSpinnerPosition = preferences.getInt("selected_spinner_position", 0);
        String selectedTheme = context.getResources().getStringArray(R.array.themes)[selectedSpinnerPosition];

        switch (selectedTheme) {
            case "Default":
                color1 = context.getResources().getColor(R.color.main_orange);
                color2 = context.getResources().getColor(R.color.main_orange);
                break;
            case "Watermelon":
                color1 = context.getResources().getColor(R.color.wm_green);
                color2 = context.getResources().getColor(R.color.wm_red);
                break;
            case "Neon":
                color1 = context.getResources().getColor(R.color.nn_pink);
                color2 = context.getResources().getColor(R.color.nn_cyan);
                break;
            case "Protanopia":
                color1 = context.getResources().getColor(R.color.pro_purple);
                color2 = context.getResources().getColor(R.color.pro_green);
                break;
            case "Deuteranopia":
                color1 = context.getResources().getColor(R.color.deu_yellow);
                color2 = context.getResources().getColor(R.color.deu_blue);
                break;
            case "Tritanopia":
                color1 = context.getResources().getColor(R.color.tri_orange);
                color2 = context.getResources().getColor(R.color.tri_green);
                break;
            default:
                color1 = context.getResources().getColor(R.color.main_orange);
                color2 = context.getResources().getColor(R.color.main_orange);
                break;
        }
    }

    @NonNull
    @Override
    public Place_Reviews_Recyclerview_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.place_reviews_recyclerview, parent, false);

        return new Place_Reviews_Recyclerview_Adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Place_Reviews_Recyclerview_Adapter.MyViewHolder holder, int position) {
        if (placeReviews.isEmpty()){
            // Display No Reviews text if possible
        } else{
            PlaceReview placeReview = placeReviews.get(position);
            // Extract fields
            String author_name = placeReview.getAuthorName();
            String profile_photo_url = placeReview.getProfilePhotoUrl();
            Double rating = placeReview.getRating(); // Default value if not present
            String relative_time_description = placeReview.getRelativeTimeDescription();
            String content = placeReview.getText();

            holder.authorName.setText(author_name);
            if (!(profile_photo_url==null)){
                Glide.with(context)
                        .load(profile_photo_url)
                        .transform(new CenterCrop(),new RoundedCorners(10))
                        .into(holder.profileImg);
            }
            float floatRating = rating.floatValue();
            holder.reviewRating.setRating(floatRating);
            holder.reviewRating.setProgressTintList(ColorStateList.valueOf(color2));
            holder.relativeTimeDescription.setText(relative_time_description);
            holder.reviewContent.setText(content);
        }
    }

    @Override
    public int getItemCount() {
        return placeReviews.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView relativeTimeDescription;
        RatingBar reviewRating;
        ImageView profileImg;
        TextView authorName;
        TextView reviewContent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeTimeDescription = itemView.findViewById(R.id.relativeTimeDescription);
            reviewRating = itemView.findViewById(R.id.reviewRating);
            profileImg = itemView.findViewById(R.id.profileImg);
            authorName = itemView.findViewById(R.id.authorName);
            reviewContent = itemView.findViewById(R.id.reviewContent);
        }
    }


}
