package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class favoritePlaceRVAdapter extends RecyclerView.Adapter<favoritePlaceRVAdapter.CardViewHolder> {

    private Context context;
    private List<PlaceDetails> dataList; // replace YourDataModel with your actual data model
    private FirebaseDatabase db = FirebaseDatabase.getInstance();;
    private DatabaseReference myRef = db.getReference("Favourites");;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = fbuser.getUid();

    public favoritePlaceRVAdapter(Context context, List<PlaceDetails> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public favoritePlaceRVAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_item_recyclerview, parent, false);
        return new favoritePlaceRVAdapter.CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        PlaceDetails place = dataList.get(position);
        // Bind data to views

        if (!(place.getPhotos().isEmpty())){
            String placePhoto = place.getPhotos().get(0);
            // Using Glide to load the image from URL into the ImageView
            Glide.with(context)
                    .load(placePhoto) // Assuming getImgUrl() returns the image URL
                    .transform(new CenterCrop(),new RoundedCorners(10))
                    .into(holder.catPlace);
        } else{
            Glide.with(context)
                    .load(R.drawable.imagenotfound)  // Replace with your "ImageNotFound" drawable
                    .transform(new CenterCrop(),new RoundedCorners(10))
                    .into(holder.catPlace);
        }

        holder.recPlaceName.setText(place.getName());
        if (place.getRating() == 0){
            holder.recPlaceRatingtv.setText("NA");
        } else{
            holder.recPlaceRatingtv.setText(String.valueOf(place.getRating()));
        }

        if (uid != null) {
            DatabaseReference placeRef = myRef.child(uid).child(place.getPlaceId());

            placeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        holder.favouritePlaceBtn.setBackgroundResource(R.drawable.favourite_top_place_icon);
                        holder.favouritePlaceBtn.setTag(R.drawable.favourite_top_place_icon);
                    } else {
                        holder.favouritePlaceBtn.setBackgroundResource(R.drawable.unfavourite_top_place_icon);
                        holder.favouritePlaceBtn.setTag(R.drawable.unfavourite_top_place_icon);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "Error checking favorite status", databaseError.toException());
                }
            });
        } else {
            holder.favouritePlaceBtn.setBackgroundResource(R.drawable.unfavourite_top_place_icon);
            holder.favouritePlaceBtn.setTag(R.drawable.unfavourite_top_place_icon);
        }

        holder.favouritePlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uid == null) {
                    Log.d("FavouriteBtn", "User not signed in.");
                    return;
                }

                int favouriteDrawableResource = R.drawable.favourite_top_place_icon;
                int unfavouriteDrawableResource = R.drawable.unfavourite_top_place_icon;

                Integer currentTag = (Integer) holder.favouritePlaceBtn.getTag();
                Log.d("FavouriteBtn", "Current background drawable tag: " + currentTag);

                if (currentTag != null && currentTag == favouriteDrawableResource) {
                    holder.favouritePlaceBtn.setBackgroundResource(unfavouriteDrawableResource);
                    holder.favouritePlaceBtn.setTag(unfavouriteDrawableResource);
                    myRef.child(uid).child(place.getPlaceId()).removeValue();
                } else {
                    holder.favouritePlaceBtn.setBackgroundResource(favouriteDrawableResource);
                    holder.favouritePlaceBtn.setTag(favouriteDrawableResource);
                    myRef.child(uid).child(place.getPlaceId()).setValue(place);
                }
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to start the ViewPlaceActivity
                Intent intent = new Intent(context, CollapsingViewPlaceActivity.class);
                // Pass the PlaceDetail object as an extra in the intent
                intent.putExtra("place", (Parcelable) place);
                // Start the ViewPlaceActivity
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView catPlace;
        TextView recPlaceName;
        TextView recPlaceRatingtv;
        ImageView recPlaceRatingImg;
        ImageButton favouritePlaceBtn;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            catPlace = itemView.findViewById(R.id.catPlace);
            recPlaceName = itemView.findViewById(R.id.recPlaceName);
            recPlaceRatingtv = itemView.findViewById(R.id.recPlaceRatingtv);
            recPlaceRatingImg = itemView.findViewById(R.id.recPlaceRatingImg);
            favouritePlaceBtn = itemView.findViewById(R.id.favouritePlaceBtn);
        }
    }
}

