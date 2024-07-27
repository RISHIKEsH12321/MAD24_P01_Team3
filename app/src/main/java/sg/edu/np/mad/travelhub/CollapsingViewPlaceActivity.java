package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CollapsingViewPlaceActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Loading_Dialog loadingDialog = new Loading_Dialog(CollapsingViewPlaceActivity.this);
    private PDFragmentAdapter adapter = new PDFragmentAdapter(this);
    private BottomSheetBehavior<View> behavior;
    private PlaceDetails place;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = db.getReference("Favourites");
    private FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = fbuser != null ? fbuser.getUid() : null;
    private TextView placeName;
    private ImageView locationIcon;
    private RatingBar ratingBar;
    private TabLayout PlaceDetailsTabs;
    private AppCompatButton addToPlanBtn;
    int color1;
    int color2;
    int color3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collapsing_view_place);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences preferences = getSharedPreferences("spinner_preferences", MODE_PRIVATE);
        int selectedSpinnerPosition = preferences.getInt("selected_spinner_position", 0);
        String selectedTheme = getResources().getStringArray(R.array.themes)[selectedSpinnerPosition];

        switch (selectedTheme) {
            case "Default":
                color1 = getResources().getColor(R.color.main_orange);
                color2 = getResources().getColor(R.color.main_orange);
                color3 = getResources().getColor(R.color.main_orange_bg);
                break;
            case "Watermelon":
                color1 = getResources().getColor(R.color.wm_green);
                color2 = getResources().getColor(R.color.wm_red);
                color3 = getResources().getColor(R.color.wm_red_bg);
                break;
            case "Neon":
                color1 = getResources().getColor(R.color.nn_pink);
                color2 = getResources().getColor(R.color.nn_cyan);
                color3 = getResources().getColor(R.color.nn_cyan_bg);
                break;
            case "Protanopia":
                color1 = getResources().getColor(R.color.pro_purple);
                color2 = getResources().getColor(R.color.pro_green);
                color3 = getResources().getColor(R.color.pro_green_bg);
                break;
            case "Deuteranopia":
                color1 = getResources().getColor(R.color.deu_yellow);
                color2 = getResources().getColor(R.color.deu_blue);
                color3 = getResources().getColor(R.color.deu_blue_bg);
                break;
            case "Tritanopia":
                color1 = getResources().getColor(R.color.tri_orange);
                color2 = getResources().getColor(R.color.tri_green);
                color3 = getResources().getColor(R.color.tri_green_bg);
                break;
            default:
                color1 = getResources().getColor(R.color.main_orange);
                color2 = getResources().getColor(R.color.main_orange);
                color3 = getResources().getColor(R.color.main_orange_bg);
                break;
        }

        placeName = findViewById(R.id.placeName);
        locationIcon = findViewById(R.id.locationIcon);
        ratingBar = findViewById(R.id.ratingBar);
        PlaceDetailsTabs = findViewById(R.id.PlaceDetailsTabs);
        addToPlanBtn = findViewById(R.id.addToPlanBtn);

        placeName.setTextColor(color1);
        locationIcon.setColorFilter(color2);
        // Set the RatingBar progress tint color
        ratingBar.setProgressTintList(ColorStateList.valueOf(color2)); // Sets the color for the progress (filled portion)
        PlaceDetailsTabs.setSelectedTabIndicatorColor(color2);
        PlaceDetailsTabs.setTabTextColors(getResources().getColor(R.color.view_place_font_color), color1);
        addToPlanBtn.setBackgroundTintList(ColorStateList.valueOf(color2));

        // Get the intent that started this activity
        Intent intent = getIntent();

        // Check if intent has extras and retrieve the Place object
        if (intent != null && intent.hasExtra("place")) {
            place = (PlaceDetails) intent.getParcelableExtra("place");
            if (place.getKinds() != null){
                Log.d("place", place.getKinds());
            }

            // Setting the front end with place details
            placeName = findViewById(R.id.placeName);
            placeName.setText(place.getName());

            TextView placeLocation = findViewById(R.id.placeLocation);
            placeLocation.setText(place.getAddress());

            TextView ratingText = findViewById(R.id.ratingText);
            if (place.getRating() == 0){
                ratingText.setText("NA");
            } else{
                ratingText.setText(String.valueOf(place.getRating()));
            }

            ratingBar = findViewById(R.id.ratingBar);
            float rating = (float) place.getRating(); // Example: Rating of 4.5 stars
            ratingBar.setRating(rating);

            // Loading placePhotoImg
            ImageView placePhotoImg = findViewById(R.id.placePhotoImg);
            if (place.getPhotos().isEmpty()){
                Glide.with(this)
                        .load(R.drawable.imagenotfound) // Assuming getImgUrl() returns the image URL
                        .into(placePhotoImg);
            } else{
                Glide.with(this)
                        .load(place.getPhotos().get(0)) // Assuming getImgUrl() returns the image URL
                        .into(placePhotoImg);
            }

            ImageButton favouriteBtn = findViewById(R.id.favouriteBtn);

            // Set initial drawable and tag for favorite button
            favouriteBtn.setImageResource(R.drawable.unfavourite_place_collapse_btn);
            favouriteBtn.setTag(R.drawable.unfavourite_place_collapse_btn);

            // Set initial drawable and tag for favorite button
            if (uid != null) {
                Log.d("UID", uid);
                Log.d("PlaceId", place.getPlaceId());
                DatabaseReference placeRef = myRef.child(uid).child(place.getPlaceId());

                placeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            favouriteBtn.setImageResource(R.drawable.favourite_more_place_btn);
                            favouriteBtn.setTag(R.drawable.favourite_more_place_btn);
                        } else {
                            favouriteBtn.setImageResource(R.drawable.unfavourite_place_collapse_btn);
                            favouriteBtn.setTag(R.drawable.unfavourite_place_collapse_btn);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase", "Error checking favorite status", databaseError.toException());
                    }
                });
            }

            // Set click listener for favorite button
            favouriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (uid == null) {
                        Log.d("FavouriteBtn", "User not signed in.");
                        return;
                    }

                    int favouriteDrawableResource = R.drawable.favourite_more_place_btn;
                    int unfavouriteDrawableResource = R.drawable.unfavourite_place_collapse_btn;

                    Integer currentTag = (Integer) favouriteBtn.getTag();
                    Log.d("FavouriteBtn", "Current background drawable tag: " + currentTag);

                    // Toggle between favorite and unfavorite
                    if (currentTag != null && currentTag == favouriteDrawableResource) {
                        favouriteBtn.setImageResource(unfavouriteDrawableResource);
                        favouriteBtn.setTag(unfavouriteDrawableResource);
                        myRef.child(uid).child(place.getPlaceId()).removeValue();
                    } else {
                        favouriteBtn.setImageResource(favouriteDrawableResource);
                        favouriteBtn.setTag(favouriteDrawableResource);
                        myRef.child(uid).child(place.getPlaceId()).setValue(place);
                    }
                }
            });
        } else {
            // Handle case where intent does not have expected extra
            Log.e("Intent Error", "Intent does not contain 'place' extra or 'place' extra is null");
        }

        FrameLayout bottomSheet = findViewById(R.id.bottomSheet);

        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setDraggable(true);
        bottomSheet.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                bottomSheet.removeOnLayoutChangeListener(this);
                behavior.setPeekHeight(1320, true);
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });


        // Create and fetch Description and load fragments
        loadingDialog.startLoadingDialog();
        if (intent.hasExtra("cityLatLng")){
            LatLng cityLatLng = intent.getParcelableExtra("cityLatLng");
            getPlaceXid(place, cityLatLng);
        } else if (place.getDescription() == null){
            getPlaceDescription(place.getPlaceXid());
        } else{
            initializeFragmentUI();
        }


        // Event Handler for viewPlaceBackBtn
        ImageButton viewPlaceBackBtn = findViewById(R.id.viewPlaceBackBtn);
        viewPlaceBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Event Handler for addtoPlanBtn
        AppCompatButton addToPlanBtn = findViewById(R.id.addToPlanBtn);
        addToPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the EventManagementActivity
                Intent intent = new Intent(CollapsingViewPlaceActivity.this, EventManagement.class);
                // Pass the Place object as an extra in the intent
                intent.putExtra("purpose", "Create");
                intent.putExtra("place", place);
                // Start the ViewPlaceActivity
                startActivity(intent);
                Log.d("AddToBtn", "CLICKED");
            }
        });
    }

    private void getPlaceXid(PlaceDetails placeAutoComplete, LatLng cityLatLng){
        executor.execute(() -> {
            OkHttpClient client = new OkHttpClient();
            String apiKey = BuildConfig.otmApikey;
            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.opentripmap.com/0.1/en/places/autosuggest").newBuilder();
            urlBuilder.addQueryParameter("name", placeAutoComplete.getName());
            urlBuilder.addQueryParameter("radius", "50000");
            urlBuilder.addQueryParameter("lon", String.valueOf(cityLatLng.longitude));
            urlBuilder.addQueryParameter("lat", String.valueOf(cityLatLng.latitude));
            urlBuilder.addQueryParameter("format", "json");
            urlBuilder.addQueryParameter("limit", String.valueOf(1));
            urlBuilder.addQueryParameter("apikey", apiKey);

            String url = urlBuilder.build().toString();
            Log.d("Url", url);

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            // Execute the request and handle the response
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("Response", responseBody);
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        if (jsonArray.length() > 0) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String placeXid = jsonObject.getString("xid");
                            Log.d("PlaceXid", placeXid);

                            runOnUiThread(() -> {
                                getPlaceDescription(placeXid);
                            });
                        } else {
                            String description = "No Description available for this place.";
                            place.setDescription(description);
                            runOnUiThread(() -> {
                                initializeFragmentUI();
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Response", "Unsuccessful: " + response.code());
                    // Handle error response
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Handle network or IO errors
            }
        });
    }

    private void getPlaceDescription(String placeXid){
        if (placeXid != null){
            executor.execute(() -> {
                OkHttpClient client = new OkHttpClient();
                String apiKey = BuildConfig.otmApikey;
                HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.opentripmap.com/0.1/en/places/xid/" + placeXid).newBuilder();
                urlBuilder.addQueryParameter("apikey", apiKey);

                String url = urlBuilder.build().toString();
                Log.d("Url", url);

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                // Execute the request and handle the response
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.d("Response", responseBody);

                        // Parse the JSON response to extract the description
                        try {

                            JSONObject jsonObject = new JSONObject(responseBody);
                            String description;
                            // Check if the key "wikipedia_extracts" exists before trying to access it
                            if (jsonObject.has("wikipedia_extracts")) {
                                JSONObject wikipedia_extracts = jsonObject.getJSONObject("wikipedia_extracts");
                                // Safely retrieve the description with a default value
                                description = wikipedia_extracts.optString("text", "No Description available for this place.");
                                // Use the description as needed
                            } else {
                                // Handle the case where "wikipedia_extracts" is not present
                                description = "No Description available for this place.";
                                // Use the description as needed
                            }
                            // Log or process the description as needed
                            Log.d("Description", description);
                            place.setDescription(description);

                            if (place.getKinds() == null){
                                String kinds = jsonObject.getString("kinds");
                                place.setKinds(kinds);
                                Log.d("Set Kinds", kinds);
                            }

                            runOnUiThread(() -> {
                                initializeFragmentUI();
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("Response", "Unsuccessful: " + response.code());
                        // Handle error response
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle network or IO errors
                }
            });
        } else{
            String description = "No Description available for this place.";
            place.setDescription(description);
            initializeFragmentUI();
        }
    }

    private void initializeFragmentUI(){
        TabLayout tabLayout = findViewById(R.id.PlaceDetailsTabs);
        ViewPager2 viewPager = findViewById(R.id.fragmentTab);
        viewPager.setUserInputEnabled(false);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // To actually get back the current behavior state of the bottomsheet
//                                        int currentState = behavior.getState();
//                                        bottomSheet.post(() ->{
//                                            if (currentState == BottomSheetBehavior.STATE_COLLAPSED){
//                                                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                                            } else{
//                                                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                                            }
//                                        });
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // auto expand the bottomsheet for better viewing
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Create AboutFragment and pass description
        AboutFragment aboutFragment = new AboutFragment();
        Bundle aboutBundle = new Bundle();
        aboutBundle.putString("description", place.getDescription()); // Assuming getPlaceDescription() returns the description string
        aboutFragment.setArguments(aboutBundle);
        adapter.addFragment(aboutFragment, "About");

        // Create ReviewsFragment and pass the reviews
        ReviewsFragment reviewsFragment = new ReviewsFragment();
        Bundle reviewBundle = new Bundle();

        // Check if place.getReviews() is null or empty
        ArrayList<PlaceReview> placeReviewList = (place.getReviews() != null && !place.getReviews().isEmpty())
                ? new ArrayList<>(place.getReviews())
                : new ArrayList<>();

        reviewBundle.putParcelableArrayList("placeReviewsList", placeReviewList);
        reviewsFragment.setArguments(reviewBundle);
        Log.d("Reviews", "Number of Reviews: " + placeReviewList.size());
        adapter.addFragment(reviewsFragment, "Reviews");

        // Create PhotosFragment and pass photo URLs
        PhotosFragment photosFragment = new PhotosFragment();
        Bundle photosBundle = new Bundle();

        // Check if place.getPhotos() is null or empty
        ArrayList<String> photoUrls = (place.getPhotos() != null && !place.getPhotos().isEmpty())
                ? new ArrayList<>(place.getPhotos().subList(1, Math.min(place.getPhotos().size(), 6)))
                : new ArrayList<>();

        photosBundle.putStringArrayList("placePhotos", photoUrls);
        photosFragment.setArguments(photosBundle);
        adapter.addFragment(photosFragment, "Photos");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(adapter.getPageTitle(position));
        }).attach();
        loadingDialog.dismissDialog();

        // Store the place into the history of the user into sqlite
        if (!(place.isHistory())){
            SavePlaceHistoryDBHandler dbHelper = new SavePlaceHistoryDBHandler(this);
            dbHelper.insertPlaceDetails(place);
        }
    };
}
