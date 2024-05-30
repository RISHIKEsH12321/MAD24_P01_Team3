package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

public class Recommended_Places_Recyclerview_Adapter extends RecyclerView.Adapter<Recommended_Places_Recyclerview_Adapter.MyViewHolder> {

    Context context;
    ArrayList<Place> placeList;

    public Recommended_Places_Recyclerview_Adapter(Context context, ArrayList<Place> placeList){
        this.context = context;
        this.placeList = placeList;
    }

    @NonNull
    @Override
    public Recommended_Places_Recyclerview_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recommended_places_recyclerview_row, parent, false);

        return new Recommended_Places_Recyclerview_Adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Recommended_Places_Recyclerview_Adapter.MyViewHolder holder, int position) {
        // assigning values to views (rows) created in the recycler_view_row layout file
        // based on the position of the recycler view

        Place place = placeList.get(position);

        // Using Glide to load the image from URL into the ImageView
        Glide.with(context)
                .load(place.getImgUrl()) // Assuming getImgUrl() returns the image URL
                .transform(new CenterCrop(),new RoundedCorners(10))
                .into(holder.placeImg);

        holder.placeName.setText(place.getName());
        holder.location.setText(place.getLocation());
        holder.rating.setText(String.valueOf(place.getRating()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to start the ViewPlaceActivity
                Intent intent = new Intent(context, ViewPlaceActivity.class);
                // Pass the Place object as an extra in the intent
                intent.putExtra("place", place);
                // Start the ViewPlaceActivity
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        // the recyclerview just wants to know the number of items you want displayed
        return placeList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // grabbing the views from our recycler_view_row layout file
        // kinda like the onCreate method

        ImageView placeImg;
        TextView placeName;
        ImageView locationIcon;
        TextView location;
        ImageView star_rating;
        TextView rating;


        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            placeImg = itemView.findViewById(R.id.placeImg);
            placeName = itemView.findViewById(R.id.placeName);
            locationIcon = itemView.findViewById(R.id.locationIcon);
            location = itemView.findViewById(R.id.location);
            star_rating = itemView.findViewById(R.id.star_rating);
            rating = itemView.findViewById(R.id.rating);
        }
    }
}
