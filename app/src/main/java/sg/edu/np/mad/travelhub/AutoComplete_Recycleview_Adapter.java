package sg.edu.np.mad.travelhub;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AutoComplete_Recycleview_Adapter extends RecyclerView.Adapter<AutoComplete_Recycleview_Adapter.MyViewHolder> {
    private Context context;
    private List<Map<String, String>> autocompletePredictionList;
    private List<String> autoCompletePredicionPlaceIds;
    private LatLng currentCity;
    private final Handler handler = new Handler();
    private final Activity activity;
    private Loading_Dialog loadingDialog;
    private String query;
    private List<PlaceDetails>placeDetailsList;
    private boolean justHistory;
    private SavePlaceHistoryDBHandler placeHistoryDB;

    public AutoComplete_Recycleview_Adapter(Context context, List<Map<String, String>> autocompletePredictionList, List<String> autoCompletePredicionPlaceIds, LatLng currentCity, Activity activity, String query, List<PlaceDetails>historyPlaceDetails){
        this.context = context;
        this.autocompletePredictionList = autocompletePredictionList;
        this.autoCompletePredicionPlaceIds = autoCompletePredicionPlaceIds;
        this.currentCity = currentCity;
        this.activity = activity;
        loadingDialog = new Loading_Dialog(activity);
        this.placeHistoryDB = new SavePlaceHistoryDBHandler(context);
        this.query = query;
        this.justHistory = false;
        this.placeDetailsList = historyPlaceDetails;
    }

    public AutoComplete_Recycleview_Adapter(Context context, List<Map<String, String>> historyPredictionList, List<PlaceDetails> placeDetailsList, Activity activity, String query){
        this.context = context;
        this.autocompletePredictionList = historyPredictionList;
        this.placeDetailsList = placeDetailsList;
        this.activity = activity;
        loadingDialog = new Loading_Dialog(activity);
        this.placeHistoryDB = new SavePlaceHistoryDBHandler(context);
        this.query = query;
        this.justHistory = true;
    }

    @NonNull
    @Override
    public AutoComplete_Recycleview_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.search_autocomplete_recyclerview, parent, false);

        return new AutoComplete_Recycleview_Adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AutoComplete_Recycleview_Adapter.MyViewHolder holder, int position) {
        holder.autoCompleteBackgroundItem.setBackgroundResource(R.drawable.search_autocomplete_bg);

        String primaryText = autocompletePredictionList.get(position).get("primary");
        String secondaryText = autocompletePredictionList.get(position).get("secondary");

        if (query != null){
            String[] queryWords = query.split("\\s+");
            SpannableStringBuilder builder = new SpannableStringBuilder(primaryText);

            for (String word : queryWords) {
                // Find the start index of the word in the primaryText
                int startPos = primaryText.toLowerCase(Locale.getDefault()).indexOf(word.toLowerCase(Locale.getDefault()));

                // If the word is found, apply bold styling
                if (startPos != -1) {
                    int endPos = startPos + word.length();
                    builder.setSpan(new StyleSpan(Typeface.BOLD), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            holder.primaryText.setText(builder);
        } else{
            holder.primaryText.setText(primaryText);
        }

        holder.secondaryText.setText(secondaryText);

        if (this.justHistory){
            holder.history_icon.setVisibility(View.VISIBLE);
            holder.delete_search_history.setVisibility(View.VISIBLE);
            holder.delete_search_history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    placeHistoryDB.deletePlaceByName(primaryText);
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        // Check if autocompletePredictionList is not null
                        if (autocompletePredictionList != null && adapterPosition < autocompletePredictionList.size()) {
                            autocompletePredictionList.remove(adapterPosition);
                        }

                        // Check if autoCompletePredicionPlaceIds is not null and remove if needed
                        if (autoCompletePredicionPlaceIds != null && adapterPosition < autoCompletePredicionPlaceIds.size()) {
                            autoCompletePredicionPlaceIds.remove(adapterPosition);
                        }

                        // Notify adapter about the item removal
                        notifyItemRemoved(adapterPosition);
                    }
                }
            });
            PlaceDetails selectedPlace = placeDetailsList.get(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, CollapsingViewPlaceActivity.class);
                    intent.putExtra("place", selectedPlace);
                    activity.startActivity(intent);
                }
            });
        } else{
            String placeId = autoCompletePredicionPlaceIds.get(position);
            if (placeId == null){
                holder.history_icon.setVisibility(View.VISIBLE);
                holder.delete_search_history.setVisibility(View.VISIBLE);
                holder.delete_search_history.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        placeHistoryDB.deletePlaceByName(primaryText);
                        int adapterPosition = holder.getAdapterPosition();
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            // Check if autocompletePredictionList is not null
                            if (autocompletePredictionList != null && adapterPosition < autocompletePredictionList.size()) {
                                autocompletePredictionList.remove(adapterPosition);
                            }

                            // Check if autoCompletePredicionPlaceIds is not null and remove if needed
                            if (autoCompletePredicionPlaceIds != null && adapterPosition < autoCompletePredicionPlaceIds.size()) {
                                autoCompletePredicionPlaceIds.remove(adapterPosition);
                            }

                            // Notify adapter about the item removal
                            notifyItemRemoved(adapterPosition);
                        }
                    }
                });
                PlaceDetails selectedPlace = placeDetailsList.get(position);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity, CollapsingViewPlaceActivity.class);
                        intent.putExtra("place", selectedPlace);
                        activity.startActivity(intent);
                    }
                });
            } else{
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadingDialog.startLoadingDialog();
                        // Perform network operation in a background thread
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                OkHttpClient client = new OkHttpClient();
                                String apiKey = BuildConfig.googleApikey;
                                String fields = "name,photo,rating,reviews,formatted_address";

                                HttpUrl.Builder urlBuilder = HttpUrl.parse("https://maps.googleapis.com/maps/api/place/details/json").newBuilder();
                                urlBuilder.addQueryParameter("fields", fields);
                                urlBuilder.addQueryParameter("place_id", placeId);
                                urlBuilder.addQueryParameter("key", apiKey);

                                String url = urlBuilder.build().toString();
                                Log.d("url", url);

                                Request request = new Request.Builder()
                                        .url(url)
                                        .build();

                                try {
                                    Response response = client.newCall(request).execute();
                                    if (response.isSuccessful()) {
                                        String responseBody = response.body().string();
                                        JsonElement jsonElement = JsonParser.parseString(responseBody);
                                        JsonObject jsonObject = jsonElement.getAsJsonObject();

                                        if ("OK".equals(jsonObject.get("status").getAsString())) {
                                            JsonObject resultObject = jsonObject.getAsJsonObject("result");
                                            PlaceDetails placeDetails = new PlaceDetails();

                                            String name = resultObject.get("name").getAsString();
                                            placeDetails.setName(name);

                                            double rating = resultObject.has("rating")
                                                    ? resultObject.get("rating").getAsDouble()
                                                    : 0;
                                            placeDetails.setRating(rating);

                                            String address = resultObject.has("formatted_address")
                                                    ? resultObject.get("formatted_address").getAsString()
                                                    : "NA";
                                            placeDetails.setAddress(address);

                                            List<String> photosUrl = new ArrayList<>();
                                            if (resultObject.has("photos")) {
                                                JsonArray photosArray = resultObject.getAsJsonArray("photos");
                                                for (JsonElement photoElement : photosArray) {
                                                    JsonObject photoObject = photoElement.getAsJsonObject();
                                                    String photoReference = photoObject.get("photo_reference").getAsString();
                                                    String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=" + photoReference + "&key=" + apiKey;
                                                    photosUrl.add(photoUrl);
                                                }
                                            }
                                            placeDetails.setPhotos(photosUrl);

                                            List<PlaceReview> reviewList = new ArrayList<>();
                                            if (resultObject.has("reviews")) {
                                                JsonArray reviewsArray = resultObject.getAsJsonArray("reviews");
                                                for (JsonElement reviewElement : reviewsArray) {
                                                    JsonObject reviewObject = reviewElement.getAsJsonObject();

                                                    String authorName = reviewObject.has("author_name") ? reviewObject.get("author_name").getAsString() : "Author Name Not Available";
                                                    String authorUrl = reviewObject.has("author_url") ? reviewObject.get("author_url").getAsString() : null;
                                                    String profilePhotoUrl = reviewObject.has("profile_photo_url") ? reviewObject.get("profile_photo_url").getAsString() : null;
                                                    Double reviewRating = reviewObject.has("rating") ? reviewObject.get("rating").getAsDouble() : 0;
                                                    String relativeTimeDescription = reviewObject.has("relative_time_description") ? reviewObject.get("relative_time_description").getAsString() : "NA";
                                                    String text = reviewObject.has("text") ? reviewObject.get("text").getAsString() : "NA";
                                                    long time = reviewObject.has("time") ? reviewObject.get("time").getAsLong() : 0;
                                                    boolean translated = reviewObject.has("translated") && reviewObject.get("translated").getAsBoolean();

                                                    PlaceReview review = new PlaceReview(authorName, authorUrl, profilePhotoUrl, reviewRating, relativeTimeDescription, text, time, translated);
                                                    reviewList.add(review);
                                                }
                                            }
                                            placeDetails.setReviews(reviewList);

                                            Log.d("Place Details", "Name: " + name);
                                            Log.d("Place Details", "Rating: " + rating);
                                            Log.d("Place Details", "Photo References: " + photosUrl);

                                            // Start new activity on UI thread
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Intent intent = new Intent(activity, CollapsingViewPlaceActivity.class);
                                                    intent.putExtra("place", placeDetails);
                                                    intent.putExtra("cityLatLng", currentCity);
                                                    activity.startActivity(intent);
                                                    loadingDialog.dismissDialog();
                                                }
                                            });
                                        } else {
                                            Log.e("FetchPlaceDetailsTask", "Request failed: " + jsonObject.get("status").getAsString());
                                        }
                                    } else {
                                        Log.e("FetchPlaceDetailsTask", "Request failed: " + response.message());
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return autocompletePredictionList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout autoCompleteBackgroundItem;
        TextView primaryText;
        TextView secondaryText;
        ImageView history_icon;
        ImageButton delete_search_history;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            autoCompleteBackgroundItem = itemView.findViewById(R.id.autoCompleteBackgroundItem);
            primaryText = itemView.findViewById(R.id.primaryText);
            secondaryText = itemView.findViewById(R.id.secondaryText);
            history_icon = itemView.findViewById(R.id.history_icon);
            delete_search_history = itemView.findViewById(R.id.delete_search_history);
        }
    }
}
