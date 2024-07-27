package sg.edu.np.mad.travelhub;

import static android.content.ContentValues.TAG;

import android.app.AlarmManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.CircularBounds;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HomeActivity extends AppCompatActivity {
    ArrayList<Place> placeList = new ArrayList<>();
    ArrayList<Place> recommendedPlaceList = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Handler handler = new Handler();
    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 2;
    private ArrayAdapter<String> citiesArrayAdapter;
    private List<String> cityList = new ArrayList<>();
    private Map<String, City> cityDictionary = new HashMap<>();
    private LinkedHashMap<String, PlaceDetails> placesName = new LinkedHashMap<>();
    private List<String> allPlaceId = new ArrayList<>();
    private List<PlaceDetails> placeDetailsList = new ArrayList<>();
    private List<PlaceDetails> topPlaceList = new ArrayList<>();
    private List<PlaceDetails> morePlaceList = new ArrayList<>();
    private int placeSize = 0;
    private List<Map.Entry<String, PlaceDetails>> placesToAdd;
    private boolean updatingRecyclerView = false;
    private LinearLayoutManager topPlacesRVManager;
    private LinearLayoutManager morePlacesRVManager;
    private Top_Places_Recyclerview_Adapter topPlaceAdapter;
    private More_Places_Recyclerview_Adapter morePlaceAdapter;
    ProgressBar morePlacesProgressBar;
    ImageView morePlacesRVProgressBarBG;
    private boolean isScrollingMorePlacesRV;
    private boolean fetchingMorePlaces = false;
    private int limit = 100;
    private int noOfTopPlaces = 4;
    private int noOfMorePlaces = 3;
    private int addNumberOfPlaces = 2;
    private int maxNoOfPaginationLoad = 2;
    private int paginationCount = 0;
    private static final double FAVORITE_PLACE_SCORE = 25.0; // High fixed score for favorite places
    private static final double VIEW_COUNT_WEIGHT = 1.0; // Weight for view count, increase the weight,
    private static final double RECENT_VIEW_WEIGHT = 2.0; // Weight for recent view score
    private static final double BASE_RECENT_VIEW_SCORE = 10.0; // Base score for recency
    private static final Set<String> EXCLUDED_KINDS = new HashSet<>(Arrays.asList("adults", "interesting_places", "tourist_facilities", "foods"));
    private final Loading_Dialog loadingDialog = new Loading_Dialog(HomeActivity.this);
    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;
    private ProgressBar progressBar;
    private List<String> autoCompletePredictionPlaceIds = new ArrayList<>();
    private List<Map<String, String>> autocompletePredictionList = new ArrayList<>();
    private boolean updateAutoCompleteRV = false;
    private boolean isSearchViewInitialized = false;
    private LatLng currentCity;
    private SavePlaceHistoryDBHandler placeHistoryDB;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = db.getReference("Favourites");
    private FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = fbuser != null ? fbuser.getUid() : null;
    private boolean initialisingSearchView = true;
    Button currentActiveBtn;
    int color1;
    int color2;
    int color3;
    ImageButton notificationBell;
    private boolean isNotificationEnabled;

    @Override
    protected void onResume() {
        super.onResume();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SearchView searchView = findViewById(R.id.searchView);
        ConstraintLayout main = findViewById(R.id.main);
        searchView.setQuery("", false);
        main.requestFocus();

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

        // Change colour for Drawables
        TextView dropdown_arrow = findViewById(R.id.dropdown);
        Drawable startDrawable = ContextCompat.getDrawable(this, R.drawable.home_activity_location_marker);
        startDrawable.setTint(color1);
        Drawable endDrawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_down);
        endDrawable.setTint(color1);
        dropdown_arrow.setCompoundDrawablesWithIntrinsicBounds(startDrawable, null, endDrawable, null);

        //Change color for Bottom NavBar
        BottomNavigationView bottomNavMenu = (BottomNavigationView) findViewById(R.id.bottomNavMenu);
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_selected},
                new int[]{} // default state
        };
        int[] colors = new int[]{
                color1,
                ContextCompat.getColor(this, R.color.unselectedNavBtn)
        };
        ColorStateList colorStateList = new ColorStateList(states, colors);
        bottomNavMenu.setItemIconTintList(colorStateList);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavMenu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        sessionToken = AutocompleteSessionToken.newInstance();

        placeHistoryDB = new SavePlaceHistoryDBHandler(this);
        RecyclerView placeAutoCompleteRV = findViewById(R.id.placeAutoCompleteRV);
        List<PlaceDetails> placeHistory = placeHistoryDB.getAllPlaceDetails();
        List<Map<String, String>> historyPredictionList = placeHistoryDB.getAllPrimaryAndSecondaryTexts();
        AutoComplete_Recycleview_Adapter placeAutoCompleteAdapter = new AutoComplete_Recycleview_Adapter(HomeActivity.this, historyPredictionList, placeHistory, HomeActivity.this, null);
        placeAutoCompleteRV.swapAdapter(placeAutoCompleteAdapter, true);

        RecyclerView topPlacesRV = findViewById(R.id.topPlacesRV);
        RecyclerView morePlacesRV = findViewById(R.id.morePlacesRV);

        Top_Places_Recyclerview_Adapter topPlaceAdapter = new Top_Places_Recyclerview_Adapter(this, topPlaceList);
        topPlacesRV.swapAdapter(topPlaceAdapter, true);

        More_Places_Recyclerview_Adapter morePlaceAdapter = new More_Places_Recyclerview_Adapter(this, morePlaceList);
        morePlacesRV.swapAdapter(morePlaceAdapter, true);

        // Initialize shared preferences
        SharedPreferences  notificationPref = getSharedPreferences("settings", MODE_PRIVATE);

        // Initialize state flag from shared preferences
        isNotificationEnabled = notificationPref.getBoolean("mpn", false);

        setNotificationBell();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove location updates when activity is destroyed to prevent memory leaks
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        placeHistoryDB = new SavePlaceHistoryDBHandler(this);

        ImageButton chatButton = findViewById(R.id.chat_btn);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, Chatbot.class);
                startActivity(intent);
            }
        });
//        PlaceDetails place = new PlaceDetails();
//        place.setName("Sample Place");
//        place.setAddress("Sample Address");
//        placeHistoryDB.insertPlaceDetails(place);
//        placeHistoryDB.deletePlaceByName("Lau Pa Sat");
//        placeHistoryDB.deletePlaceByName("Lau Pa Sat -Satay Corner");

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

        // Change colour for Drawables
        ImageButton chat_btn = findViewById(R.id.chat_btn);
        Drawable chat_bg = ContextCompat.getDrawable(this, R.drawable.chat_btn_bg);
        chat_bg.setTint(color1);
        chat_btn.setBackgroundDrawable(chat_bg);

        TextView dropdown_arrow = findViewById(R.id.dropdown);
        Drawable startDrawable = ContextCompat.getDrawable(this, R.drawable.home_activity_location_marker);
        startDrawable.setTint(color1);
        Drawable endDrawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_down);
        endDrawable.setTint(color1);
        dropdown_arrow.setCompoundDrawablesWithIntrinsicBounds(startDrawable, null, endDrawable, null);

        //Change color for Bottom NavBar
        BottomNavigationView bottomNavMenu = (BottomNavigationView) findViewById(R.id.bottomNavMenu);
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_selected},
                new int[]{} // default state
        };
        int[] colors = new int[]{
                color1,
                ContextCompat.getColor(this, R.color.unselectedNavBtn)
        };
        ColorStateList colorStateList = new ColorStateList(states, colors);
        bottomNavMenu.setItemIconTintList(colorStateList);

        // Bottom Navigation View Logic to link to the different master activities
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavMenu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnApplyWindowInsetsListener(null);
        bottomNavigationView.setPadding(0, 0, 0, 0);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_searchUserOrPost) {
                startActivity(new Intent(this, SearchUser.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                return true;
            } else if (item.getItemId() == R.id.bottom_calendar) {
                startActivity(new Intent(this, ViewEvents.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                return true;
            } else if (item.getItemId() == R.id.bottom_currency) {
                startActivity(new Intent(this, ConvertCurrency.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                return true;
            } else if (item.getItemId() == R.id.bottom_profile) {
                startActivity(new Intent(this, Profile.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                return true;
            } else if (item.getItemId() == R.id.bottom_home) {
                // Optional: Handle home selection differently or ignore
                return true;
            }
            return false;
        });



//        ImageButton notificationBell = findViewById(R.id.notification_btn);
//        notificationBell.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(HomeActivity.this, ConvertCurrency.class));
//            }
//        });
        setNotificationBell();
        // Initialize locationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request permissions if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            // Permissions are granted, request location updates
            requestLocationUpdates();
        }

        // Reading Cities(used).json to get the cities and the latlon
        try {
            // Get the AssetManager
            AssetManager assetManager = this.getAssets();

            // Open the JSON file
            InputStream inputStream = assetManager.open("cities(used).json");

            // Read the JSON file into a String
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String json = stringBuilder.toString();

            // Parse the JSON
            JSONObject jsonObject = new JSONObject(json);
            JSONArray citiesArray = jsonObject.getJSONArray("cities");

            // Extract "name" and "country_code" for each city
            for (int i = 0; i < citiesArray.length(); i++) {
                JSONObject cityObject = citiesArray.getJSONObject(i);
                int id = cityObject.getInt("id");
                String name = cityObject.getString("name");
                int stateId = cityObject.getInt("state_id");
                String stateCode = cityObject.getString("state_code");
                String stateName = cityObject.getString("state_name");
                int countryId = cityObject.getInt("country_id");
                String countryCode = cityObject.getString("country_code");
                String countryName = cityObject.getString("country_name");
                String latitude = cityObject.getString("latitude");
                String longitude = cityObject.getString("longitude");
                String wikiDataId = cityObject.getString("wikiDataId");

                // Create a City object
                City city = new City(id, name, stateId, stateCode, stateName, countryId, countryCode, countryName, latitude, longitude, wikiDataId);

                // Store the result in a Map and add it to the list
                String cityText = name + ", " + countryCode;
                cityList.add(cityText);
                cityDictionary.put(cityText, city);
            }

            // Close the reader and stream
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Cities", "Failed");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Initializing place UI and place recommendations
        loadingDialog.startLoadingDialog();
        placesName.clear();
        allPlaceId.clear();
        placeDetailsList.clear();
        topPlaceList.clear();
        morePlaceList.clear();
        paginationCount=0;
        placeSize = 0;
        City firstCity = cityDictionary.get(cityList.get(0));
        displayFetchedPlaces(Double.parseDouble(firstCity.getLatitude()), Double.parseDouble(firstCity.getLongitude()), null);

        // Setting default option of the city selection
        TextView dropdown = findViewById(R.id.dropdown);
        String defaultOption = cityList.get(0);
        dropdown.setText(defaultOption);

        // Getting all the filter buttons and storing them in a list
        ArrayList<Button> btnList = new ArrayList<>();
        Button allBtn = findViewById(R.id.allBtn);
        Button hotelBtn = findViewById(R.id.hotelsBtn);
        Button foodBtn = findViewById(R.id.foodBtn);
        Button amusementsBtn = findViewById(R.id.amusementsBtn);
        Button mallsBtn = findViewById(R.id.mallsBtn);
        Button natureBtn = findViewById(R.id.natureBtn);
        btnList.add(allBtn);
        btnList.add(hotelBtn);
        btnList.add(foodBtn);
        btnList.add(amusementsBtn);
        btnList.add(mallsBtn);
        btnList.add(natureBtn);

        currentActiveBtn = allBtn;

        // Enable filter buttons sets the active/selected filter button to the active orange color
        enableFilterBtn(currentActiveBtn, null);

        // Logic for changing cities
        dropdown.setOnClickListener(v -> {

            // Initializing Dialog
            Dialog dialog = new Dialog(HomeActivity.this);
            // Set Custom Dialog with the XML file in layout folders
            dialog.setContentView(R.layout.dialog_searchable_spinner);
            // Set Custom Width & Height
            Objects.requireNonNull(dialog.getWindow()).setLayout(800, 1200);
            // Set Transparent Background
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // Show Dialog
            dialog.show();

            // Initialize and assign variable
            EditText editText = dialog.findViewById(R.id.edit_text);
            ListView listView = dialog.findViewById(R.id.list_view);

            // Initialize array adapter
            citiesArrayAdapter = new ArrayAdapter<>(HomeActivity.this, android.R.layout.simple_list_item_1, cityList);

            // Set Adapter
            listView.setAdapter(citiesArrayAdapter);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Filter Array List
                    citiesArrayAdapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            // Logic for changing the place recommendation when the city selected changes
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // When item selected from list
                    // Set selected item on text view
                    String city = citiesArrayAdapter.getItem(position);

                    if (!city.equals(dropdown.getText().toString())) {
                        dropdown.setText(city);

                        Log.d("City Selected", city);
                        Log.d("City Selected", cityDictionary.get(city).toString());

                        City cityInfo = cityDictionary.get(city);

                        placesName.clear();
                        allPlaceId.clear();
                        placeDetailsList.clear();
                        topPlaceList.clear();
                        morePlaceList.clear();
                        paginationCount=0;
                        placeSize = 0;
                        loadingDialog.startLoadingDialog();
                        displayFetchedPlaces(Double.parseDouble(cityInfo.getLatitude()), Double.parseDouble(cityInfo.getLongitude()), null);
                        if (!(currentActiveBtn == allBtn)){
                            enableFilterBtn(allBtn, currentActiveBtn);
                            currentActiveBtn = allBtn;
                        }
                        dialog.dismiss();
                    }
                }
            });
        });

        // Logic for setting the color of each filter button when activated/selected
        for (Button btn : btnList) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!(currentActiveBtn == btn)) {
                        enableFilterBtn(btn, currentActiveBtn);
                        currentActiveBtn = btn;
                        String btnText = btn.getText().toString();
                        Log.d("BtnText", btnText);
                        String kinds;
                        switch (btnText) {
                            case "Hotels":
                                kinds = "alpine_hut,campsites,guest_houses,other_hotels,resorts,villas_and_chalet";
//                                kinds ="accomodations";
                                break;
                            case "Food":
                                kinds = "foods";
                                break;
                            case "Amusements":
                                kinds = "amusements,amusement_parks,ferris_wheels,miniature_parks,other_amusement_rides,roller_coasters,water_parks";
                                break;
                            case "Malls":
                                kinds = "malls";
                                break;
                            case "Nature":
                                kinds = "natural";
                                break;
                            default:
                                kinds = null;
                                break;
                        }
                        String currentCity = dropdown.getText().toString();
                        City cityInfo = cityDictionary.get(currentCity);
                        placesName.clear();
                        allPlaceId.clear();
                        placeDetailsList.clear();
                        topPlaceList.clear();
                        morePlaceList.clear();
                        paginationCount=0;
                        placeSize = 0;
                        loadingDialog.startLoadingDialog();
                        displayFetchedPlaces(Double.parseDouble(cityInfo.getLatitude()), Double.parseDouble(cityInfo.getLongitude()), kinds);
                    }
                }
            });
        }

        ScrollView scrollView = findViewById(R.id.contentScrollView);
        RecyclerView morePlacesRV = findViewById(R.id.morePlacesRV);
        morePlacesProgressBar = findViewById(R.id.morePlacesRVProgressBar);
        morePlacesRVProgressBarBG = findViewById(R.id.morePlacesRVProgressBarBG);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            // Get the scrollView and its content height and scroll position
            int scrollY = scrollView.getScrollY();
            int scrollViewHeight = scrollView.getHeight();
            int contentHeight = scrollView.getChildAt(0).getHeight();
            int extraPadding = scrollView.getPaddingBottom();

            // Calculate if the ScrollView is fully scrolled
            if (scrollY + scrollViewHeight >= contentHeight + extraPadding && !morePlaceList.isEmpty() && morePlaceList.size() < 7) {
                // Reached the bottom of the RecyclerView
                if (!isScrollingMorePlacesRV && !fetchingMorePlaces) {
                    isScrollingMorePlacesRV = true;
                    fetchingMorePlaces = true;
                    Log.d("ScrollView", "End of RecyclerView reached");

                    // Simulate a delay
                    morePlacesProgressBar.setVisibility(View.VISIBLE);
                    morePlacesRVProgressBarBG.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(() -> {
                        Log.d("Fetching Data", "True");

                        // Fetch Data
                        int totalElements = placesToAdd.size();
                        int startIndex = morePlacesRVManager.getItemCount() + noOfTopPlaces;

                        Log.d("totalElements", String.valueOf(totalElements));
                        Log.d("startIndex", String.valueOf(startIndex));

                        if (startIndex < totalElements) {
                            if (paginationCount < maxNoOfPaginationLoad){
                                // Log the contents of placesName
                                Log.d("PlacesName", "Contents of placesName:");
                                for (Map.Entry<String, PlaceDetails> entry : placesToAdd) {
                                    Log.d("PlacesName", "Key: " + entry.getKey() + ", Value: " + entry.getValue().getName());
                                }

                                for (int i = startIndex; i < startIndex + addNumberOfPlaces && i < placesToAdd.size() && (morePlaceList.size() + 1 < 7); i++) {
                                    Map.Entry<String, PlaceDetails> entry = placesToAdd.get(i);
                                    Log.d("Place", "Key: " + entry.getKey() + ", Value: " + entry.getValue());
                                    getPlaceIds(entry.getKey(), entry.getValue().getName(), entry.getValue().getKinds(), true);
                                }
                                paginationCount += 1;
                            } else{
                                // All places have been displayed
                                Toast.makeText(getApplicationContext(), "All places have been displayed.", Toast.LENGTH_SHORT).show();
                                fetchingMorePlaces = false;
                            }
                        } else {
                            // All places have been displayed
                            Toast.makeText(getApplicationContext(), "All places have been displayed.", Toast.LENGTH_SHORT).show();
                            fetchingMorePlaces = false;
                        }
                        morePlacesProgressBar.setVisibility(View.GONE);
                        morePlacesRVProgressBarBG.setVisibility(View.GONE);
                    }, 2000); // Delay in milliseconds (2 seconds here)
                }
            } else {
                isScrollingMorePlacesRV = false;
            }
        });

        // Search Bar Place Logic Here (Meant for stage 2 Reference, Please Ignore
        SearchView searchView = findViewById(R.id.searchView);
        progressBar = findViewById(R.id.searchProgressBar);
        searchView.clearFocus();
        sessionToken = AutocompleteSessionToken.newInstance();

        // Initialize Places Client
        Places.initializeWithNewPlacesApiEnabled(getApplicationContext(), BuildConfig.googleApikey);
        placesClient = Places.createClient(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                return true; // Return true to indicate that the query has been handled
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handler.removeCallbacksAndMessages(null);
                // Start a new place prediction request in 300 ms
                City selectedCity = cityDictionary.get(dropdown.getText());
                currentCity = new LatLng(Double.parseDouble(selectedCity.getLatitude()), Double.parseDouble(selectedCity.getLongitude()));
                autocompletePredictionList.clear();
                autoCompletePredictionPlaceIds.clear();

                if (newText.isEmpty()) {
                    if (isSearchViewInitialized){
                        Log.d("NewEmpty Predictions", "true");
                        RecyclerView placeAutoCompleteRV = findViewById(R.id.placeAutoCompleteRV);
                        List<PlaceDetails> placeHistory = placeHistoryDB.getAllPlaceDetails();
                        List<Map<String, String>> historyPredictionList = placeHistoryDB.getAllPrimaryAndSecondaryTexts();

                        placeAutoCompleteRV.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                        AutoComplete_Recycleview_Adapter placeAutoCompleteAdapter = new AutoComplete_Recycleview_Adapter(HomeActivity.this, historyPredictionList, placeHistory, HomeActivity.this, null);
                        if (updateAutoCompleteRV){
                            placeAutoCompleteRV.swapAdapter(placeAutoCompleteAdapter, true);
                        } else{
                            placeAutoCompleteRV.setAdapter(placeAutoCompleteAdapter);
                        }
//                    Log.d("EmptyQuery", "True | Activated");
                        progressBar.setVisibility(View.GONE);
                        return true;
                    } else{
                        return true;
                    }
                }
                Log.d("Lat", String.valueOf(currentCity.latitude));
                Log.d("Lon", String.valueOf(currentCity.longitude));
                if (isSearchViewInitialized) {
                    handler.post(() -> progressBar.setVisibility(View.VISIBLE));
                } else{
                    isSearchViewInitialized = true;
                }
                handler.postDelayed(() -> getPlacePredictions(newText, currentCity), 800);
                return true;
            }
        });

        // Logic for handling when the searchview is focused or not
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                City selectedCity = cityDictionary.get(dropdown.getText());
                currentCity = new LatLng(Double.parseDouble(selectedCity.getLatitude()), Double.parseDouble(selectedCity.getLongitude()));
                RecyclerView placeAutoCompleteRV = findViewById(R.id.placeAutoCompleteRV);
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    placeAutoCompleteRV.setVisibility(View.GONE);
                } else{
                    placeAutoCompleteRV.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void enableFilterBtn(Button activatedBtn, @Nullable Button deactivatedBtn) {
        activatedBtn.setTextColor(color2);
        activatedBtn.setBackgroundColor(color3);

        if (deactivatedBtn != null) {
            deactivatedBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.unselectedFilterText, null));
            deactivatedBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.unselectedFilterBackground, null));
        }
    }

    // Logic for getting place predictions based on the search query in the searchview using findAutoCompletePredictions (Google Places API)
    private void getPlacePredictions(String query, LatLng center) {
        List<PlaceDetails> placeHistory = placeHistoryDB.getPlacesByQuery(query);
        List<Map<String, String>> historyPredictionList = placeHistoryDB.getPrimaryAndSecondaryTextsByQuery(query);

        autocompletePredictionList.addAll(historyPredictionList);
        for (int i = 0; i < autocompletePredictionList.size(); i++) {
            autoCompletePredictionPlaceIds.add(null);
        }

        // Set location bias (if needed, based on your requirements)
        int radiusInMeters = 50000; // 50 kilometers
        CircularBounds circle = CircularBounds.newInstance(center, radiusInMeters);

        final FindAutocompletePredictionsRequest autocompletePlacesRequest = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setLocationBias(circle)
                .setTypesFilter(Arrays.asList(PlaceTypes.ESTABLISHMENT))
                .setSessionToken(sessionToken)
                .build();

        Log.d("Session Token", sessionToken.toString());

        // Perform autocomplete request
        placesClient.findAutocompletePredictions(autocompletePlacesRequest)
                .addOnSuccessListener(
                        (response) -> {
                            Log.d("Success", "Succeeded in getting autocomplete predictions");
                            List<AutocompletePrediction> predictions = response.getAutocompletePredictions();

                            // Populate data with new predictions
                            for (AutocompletePrediction prediction : predictions) {
                                Map<String, String> item = new HashMap<>();
                                item.put("primary", prediction.getPrimaryText(null).toString());
                                item.put("secondary", prediction.getSecondaryText(null).toString());
                                Log.d("PrimaryText", prediction.getPrimaryText(null).toString());
                                Log.d("SecondaryText", prediction.getSecondaryText(null).toString());
                                if (!(autocompletePredictionList.contains(item))){
                                    autocompletePredictionList.add(item);
                                    autoCompletePredictionPlaceIds.add(prediction.getPlaceId());
                                }
                            }

                            // Notify the adapter that the data set has changed
                            RecyclerView placeAutoCompleteRV = findViewById(R.id.placeAutoCompleteRV);
                            placeAutoCompleteRV.setLayoutManager(new LinearLayoutManager(this)); // Set the LayoutManager here

                            if (updateAutoCompleteRV) {
                                AutoComplete_Recycleview_Adapter placeAutoCompleteAdapter = new AutoComplete_Recycleview_Adapter(this, autocompletePredictionList, autoCompletePredictionPlaceIds, currentCity, HomeActivity.this, query, placeHistory);
                                placeAutoCompleteRV.swapAdapter(placeAutoCompleteAdapter, true);
                                updateAutoCompleteRV = true; // Correct the flag setting

                            } else {
                                AutoComplete_Recycleview_Adapter placeAutoCompleteAdapter = new AutoComplete_Recycleview_Adapter(this, autocompletePredictionList, autoCompletePredictionPlaceIds, currentCity, HomeActivity.this, query, placeHistory);
                                placeAutoCompleteRV.setAdapter(placeAutoCompleteAdapter);
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                ).addOnFailureListener(
                        exception -> {
                            Log.e(TAG, "Error getting autocomplete predictions: " + exception.getMessage());
                            autocompletePredictionList.clear();
                            autoCompletePredictionPlaceIds.clear();

                            RecyclerView placeAutoCompleteRV = findViewById(R.id.placeAutoCompleteRV);
                            placeAutoCompleteRV.setLayoutManager(new LinearLayoutManager(this));

                            AutoComplete_Recycleview_Adapter placeAutoCompleteAdapter = new AutoComplete_Recycleview_Adapter(this, autocompletePredictionList, autoCompletePredictionPlaceIds, currentCity, HomeActivity.this, query, placeHistory);
                            placeAutoCompleteRV.setAdapter(placeAutoCompleteAdapter);
                            progressBar.setVisibility(View.GONE);
                        });
    }

    // Logic for getting the location recommendations based on the selected city
    private void displayFetchedPlaces(double lat, double lon, String kinds) {
//        final String defaultKinds = (kinds == null) ? "resorts,amusements,natural,food_courts" : kinds;
        if (kinds == null){
            recommendationKindGetPlaceRadius(lat, lon);
        } else{
            getPlaceRadius(lat, lon, kinds);
        }
    }

    private void getPlaceRadius(double lat, double lon, String kinds) {
        executor.execute(() -> {
            OkHttpClient client = new OkHttpClient();
            String apiKey = BuildConfig.otmApikey;
            double radius = 50000;
            Log.d("placeRadiusKinds", kinds);

            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.opentripmap.com/0.1/en/places/radius").newBuilder();
            urlBuilder.addQueryParameter("radius", Double.toString(radius));
            urlBuilder.addQueryParameter("lon", Double.toString(lon));
            urlBuilder.addQueryParameter("lat", Double.toString(lat));
            urlBuilder.addQueryParameter("kinds", kinds);
            urlBuilder.addQueryParameter("rate", "3");
            urlBuilder.addQueryParameter("limit", Integer.toString(limit));
            urlBuilder.addQueryParameter("apikey", apiKey);

            String url = urlBuilder.build().toString();
            Log.d("Url", url);

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JsonElement jsonElement = JsonParser.parseString(responseBody);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    JsonArray featuresArray = jsonObject.getAsJsonArray("features");

                    // Organize places by kind
                    Map<String, List<PlaceDetails>> placesByKind = new HashMap<>();
                    List<String> kindsList = Arrays.asList(kinds.split(","));
                    for (String kind : kindsList) {
                        placesByKind.put(kind.trim(), new ArrayList<>());
                    }

                    for (JsonElement featureElement : featuresArray) {
                        JsonObject featureObject = featureElement.getAsJsonObject();
                        JsonObject propertiesObject = featureObject.getAsJsonObject("properties");
                        String xid = propertiesObject.get("xid").getAsString();
                        String name = propertiesObject.get("name").getAsString().toLowerCase();
                        String placeKinds = propertiesObject.get("kinds").getAsString().toLowerCase();

                        PlaceDetails place = new PlaceDetails();
                        place.setPlaceXid(xid);
                        place.setName(name);
                        place.setKinds(placeKinds);

                        for (String kind : kindsList) {
                            if (placeKinds.contains(kind.trim())) {
                                placesByKind.get(kind.trim()).add(place);
                                break;
                            }
                        }
                    }

                    // Shuffle and interleave places from different kinds
                    List<PlaceDetails> finalPlaces = new ArrayList<>();
                    List<PlaceDetails> allPlaces = new ArrayList<>();
                    Random random = new Random();

                    for (String kind : kindsList) {
                        List<PlaceDetails> kindPlaces = placesByKind.get(kind.trim());
                        if (kindPlaces != null) {
                            Collections.shuffle(kindPlaces, random); // Shuffle places of each kind
                            allPlaces.addAll(kindPlaces);
                        }
                    }

                    int index = 0;
                    while (finalPlaces.size() < allPlaces.size()) {
                        for (String kind : kindsList) {
                            List<PlaceDetails> kindPlaces = placesByKind.get(kind.trim());
                            if (kindPlaces != null && index < kindPlaces.size()) {
                                PlaceDetails place = kindPlaces.get(index);
                                finalPlaces.add(place);
                                if (!placesName.containsValue(place)){
                                    placesName.put(place.getPlaceXid(), place);
                                }
                                if (finalPlaces.size() >= allPlaces.size()) {
                                    break;
                                }
                            }
                        }
                        index++;
                    }

                    runOnUiThread(() -> {
                        if (!finalPlaces.isEmpty()) {
                            Log.d("List Size", Integer.toString(finalPlaces.size()));

                            // Iterate over the final list and process the places
                            for (int i = 0; i < noOfTopPlaces + noOfMorePlaces && i < finalPlaces.size(); i++) {
                                PlaceDetails place = finalPlaces.get(i);
                                getPlaceIds(place.getPlaceXid(), place.getName(), place.getKinds(), false);
                            }
                        } else {
                            // Handle case where no places are found
                            loadingDialog.dismissDialog();
                            TextView noPlacesFoundTopPlace = findViewById(R.id.noPlacesFoundTopPlace);
                            noPlacesFoundTopPlace.setVisibility(View.VISIBLE);
                            TextView noPlacesFoundMorePlace = findViewById(R.id.noPlacesFoundMorePlace);
                            noPlacesFoundMorePlace.setVisibility(View.VISIBLE);

                            Toast.makeText(this, "No Places found in this area", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e("FetchPlacesTask", "Request failed: " + response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Getting the placeIds for the recommended cities from getPlaceRadius so that I can call for the getPlaceDetails API to get more details about the place
    private void getPlaceIds(String placeXid, String placeName, String placeKinds, boolean loadingMorePlaces) {
        executor.execute(() -> {
            OkHttpClient client = new OkHttpClient();
            String apiKey = BuildConfig.googleApikey;

            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://maps.googleapis.com/maps/api/place/findplacefromtext/json").newBuilder();
            urlBuilder.addQueryParameter("input", placeName);
            urlBuilder.addQueryParameter("inputtype", "textquery");
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
                    JsonArray candidatesArray = jsonObject.getAsJsonArray("candidates");

                    if (candidatesArray.size() > 0) {
                        JsonObject firstCandidate = candidatesArray.get(0).getAsJsonObject();
                        String placeId = firstCandidate.get("place_id").getAsString();

                        // Example usage of placeId (logging or updating UI)
                        runOnUiThread(() -> {
                            if (!allPlaceId.contains(placeId)){
                                Log.d("Place Name", placeName);
                                Log.d("Place ID", placeId);
                                placeSize += 1;
                                allPlaceId.add(placeId);

                                getPlaceDetails(placeId, placeXid, placeKinds, loadingMorePlaces);
                            } else{
                                if (loadingDialog.isDialogShowing()){
                                    loadingDialog.dismissDialog();
                                }
                                placesName.remove(placeXid);
                            }
                        });
                    } else {
                        runOnUiThread(() -> {
                            Log.d("Place ID", "No place ID found");
                            placesName.remove(placeXid);
                            if(placesName.isEmpty()){
                                if (loadingDialog.isDialogShowing()){
                                    loadingDialog.dismissDialog();
                                }
                            }

                            // Handle case where no place IDs are found, if needed
                            morePlacesProgressBar.setVisibility(View.GONE);
                            morePlacesRVProgressBarBG.setVisibility(View.GONE);
                        });
                    }

                } else {
                    Log.e("FetchPlaceIdTask", "Request failed: " + response.message());
                    placesName.remove(placeName);
                    morePlacesProgressBar.setVisibility(View.GONE);
                    morePlacesRVProgressBarBG.setVisibility(View.GONE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Getting the placedetails (name, photo, rating, reviews and address of the place) with the placeId
    private void getPlaceDetails(String placeId, String placeXid, String placeKinds, boolean loadingMorePlaces){
        executor.execute(()->{
            OkHttpClient client = new OkHttpClient();
            String apiKey = BuildConfig.googleApikey;
            // String fields = "name,photo,rating,editorial_summary,reviews,reviews,formatted_address"
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

                    // Check if the response status is OK
                    if ("OK".equals(jsonObject.get("status").getAsString())) {
                        JsonObject resultObject = jsonObject.getAsJsonObject("result");
                        PlaceDetails placeDetails = new PlaceDetails();
                        placeDetails.setPlaceXid(placeXid);
                        placeDetails.setPlaceId(placeId);
                        placeDetails.setKinds(placeKinds);

                        // Extracting name
                        String name = resultObject.get("name").getAsString();
                        placeDetails.setName(name);

                        // Extracting rating PLEASE DO SOMETHING ABOUT THIS
                        double rating = resultObject.has("rating")
                                ? resultObject.get("rating").getAsDouble()
                                : 0;
                        placeDetails.setRating(rating);

                        // Extracting formatted address
                        String address = resultObject.has("formatted_address")
                                ? resultObject.get("formatted_address").getAsString()
                                : "NA";
                        placeDetails.setAddress(address);

                        // Extracting photos
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

                        // Extracting Reviews
                        List<PlaceReview> reviewList = new ArrayList<>();
                        if (resultObject.has("reviews")) {
                            JsonArray reviewsArray = resultObject.getAsJsonArray("reviews");
                            for (JsonElement reviewElement : reviewsArray){
                                JsonObject reviewObject = reviewElement.getAsJsonObject();

                                String authorName = reviewObject.has("author_name") ? reviewObject.get("author_name").getAsString() : "Author Name Not Available";
                                String authorUrl = reviewObject.has("author_url") ? reviewObject.get("author_url").getAsString() : null;
                                String profilePhotoUrl = reviewObject.has("profile_photo_url") ? reviewObject.get("profile_photo_url").getAsString() : null;
                                Double reviewRating = reviewObject.has("rating") ? reviewObject.get("rating").getAsDouble() : 0;
                                String relativeTimeDescription = reviewObject.has("relative_time_description") ? reviewObject.get("relative_time_description").getAsString() : "NA";
                                String text = reviewObject.has("text") ? reviewObject.get("text").getAsString() : "NA";
                                long time = reviewObject.has("time") ? reviewObject.get("time").getAsLong() : null;
                                boolean translated = reviewObject.has("translated") && reviewObject.get("translated").getAsBoolean();

                                PlaceReview review = new PlaceReview(authorName, authorUrl, profilePhotoUrl, reviewRating, relativeTimeDescription, text, time, translated);
                                reviewList.add(review);
                            }
                        }
                        placeDetails.setReviews(reviewList);

                        // Example: Log the extracted details
                        Log.d("Place Details", "Name: " + name);
                        Log.d("Place Details", "Rating: " + rating);
                        Log.d("Place Details", "Photo References: " + photosUrl);

                        // Update the UI on the main thread
                        runOnUiThread(() -> {
                            placesToAdd = new ArrayList<>(placesName.entrySet());
                            placeDetailsList.add(placeDetails);
                            // Sort placeDetailsList based on ratings (descending order)
                            Collections.sort(placeDetailsList, new Comparator<PlaceDetails>() {
                                @Override
                                public int compare(PlaceDetails place1, PlaceDetails place2) {
                                    // Sort in descending order (highest rating first)
                                    return Double.compare(place2.getRating(), place1.getRating());
                                }
                            });

//                            Log.d("placeNameSize", "Size: " + placesName.size());
//                            Log.d("placeDetailsSize", "Size: " + placeDetailsList.size());

                            RecyclerView topPlacesRV = findViewById(R.id.topPlacesRV);
                            RecyclerView morePlacesRV = findViewById(R.id.morePlacesRV);
                            morePlacesRV.setItemViewCacheSize(0);

                            if (loadingMorePlaces){
                                morePlaceList.add(placeDetails);
                                Log.d("Last Element", morePlaceList.get(morePlaceList.size()-1).getName());

                                if (morePlaceAdapter != null) {
                                    morePlaceAdapter.notifyDataSetChanged();
                                    fetchingMorePlaces = false;
                                    morePlacesProgressBar.setVisibility(View.GONE);
                                    morePlacesRVProgressBarBG.setVisibility(View.GONE);
                                } else{
                                    morePlaceAdapter = new More_Places_Recyclerview_Adapter(this, morePlaceList);
                                    morePlacesRV.swapAdapter(morePlaceAdapter, true);
                                }
                            } else{
                                if (placeDetailsList.size() == placeSize){
                                    // recyclerview logic here
                                    if (placeSize < noOfTopPlaces){
                                        for (int i = 0; i < placeSize; i++) {
                                            topPlaceList.add(placeDetailsList.get(i));
                                        }
                                    }
                                    else{
                                        for (int i = 0; i < noOfTopPlaces; i++) {
                                            topPlaceList.add(placeDetailsList.get(i));
                                        }

                                        for (int i = noOfTopPlaces; i < placeDetailsList.size(); i++){
                                            morePlaceList.add(placeDetailsList.get(i));
                                        }
                                    }

//                                    for (PlaceDetails place : topPlaceList){
//                                        Log.d("TopPlaceList: ", place.getName());
//                                    }
//
//                                    for (PlaceDetails place : morePlaceList){
//                                        Log.d("MorePlaceList: ", place.getName());
//                                    }

                                    if (!updatingRecyclerView){
                                        ViewGroup.LayoutParams topLayoutParams = topPlacesRV.getLayoutParams();
                                        topLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                        topPlaceAdapter = new Top_Places_Recyclerview_Adapter(this, topPlaceList);
                                        topPlacesRV.setAdapter(topPlaceAdapter);
                                        topPlacesRVManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                                        topPlacesRV.setLayoutManager(topPlacesRVManager);

                                        ViewGroup.LayoutParams moreLayoutParams = morePlacesRV.getLayoutParams();
                                        moreLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                        morePlaceAdapter = new More_Places_Recyclerview_Adapter(this, morePlaceList);
                                        morePlacesRV.setAdapter(morePlaceAdapter);
                                        morePlacesRVManager = new LinearLayoutManager(this);
                                        morePlacesRV.setLayoutManager(morePlacesRVManager);
                                        updatingRecyclerView = true;
                                    } else{
                                        Log.d("Update","Updating RecyclerView");
                                        // Update the adapter with the new list
                                        topPlaceAdapter = new Top_Places_Recyclerview_Adapter(this, topPlaceList);
                                        topPlacesRV.swapAdapter(topPlaceAdapter, true);

                                        morePlaceAdapter = new More_Places_Recyclerview_Adapter(this, morePlaceList);
                                        morePlacesRV.swapAdapter(morePlaceAdapter, true);
                                    }

                                    TextView noPlacesFoundTopPlace = findViewById(R.id.noPlacesFoundTopPlace);
                                    noPlacesFoundTopPlace.setVisibility(View.VISIBLE);
                                    TextView noPlacesFoundMorePlace = findViewById(R.id.noPlacesFoundMorePlace);
                                    noPlacesFoundMorePlace.setVisibility(View.VISIBLE);

                                    loadingDialog.dismissDialog();
                                }
                            }
                            if (topPlaceList.isEmpty()){
                                TextView noPlacesFoundTopPlace = findViewById(R.id.noPlacesFoundTopPlace);
                                noPlacesFoundTopPlace.setVisibility(View.VISIBLE);
                            } else {
                                TextView noPlacesFoundTopPlace = findViewById(R.id.noPlacesFoundTopPlace);
                                noPlacesFoundTopPlace.setVisibility(View.GONE);
                            }
                            if (morePlaceList.isEmpty()){
                                TextView noPlacesFoundMorePlace = findViewById(R.id.noPlacesFoundMorePlace);
                                noPlacesFoundMorePlace.setVisibility(View.VISIBLE);
                            } else{
                                TextView noPlacesFoundMorePlace = findViewById(R.id.noPlacesFoundMorePlace);
                                noPlacesFoundMorePlace.setVisibility(View.GONE);
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
        });
    }

    // Method to request location updates using LocationManager
    private void requestLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 5F, locationListener);
        } catch (SecurityException e) {
            // Handle SecurityException
            Log.e(TAG, "SecurityException: " + e.getMessage());
        }
    }

    // LocationListener to handle location updates
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                // Use location data
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.d("Latitude", String.valueOf(latitude));
                Log.d("Longitude", String.valueOf(longitude));
                String cityText = "Current Location";
                // Assuming cityList and cityDictionary are initialized elsewhere
                cityList.add(0, cityText);
                City city = new City();
                city.setLatitude(String.valueOf(latitude));
                city.setLongitude(String.valueOf(longitude));
                cityDictionary.put(cityText, city);
                // Notify adapter of the change, ensure adapter is not null
                if (citiesArrayAdapter != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            citiesArrayAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    Log.e("Adapter Error", "citiesArrayAdapter is null");
                    // Handle the case where adapter is unexpectedly null
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, request location updates
                    requestLocationUpdates();
                } else {
                    // Permission denied, handle accordingly (e.g., show a message)
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case REQUEST_CODE_NOTIFICATION_PERMISSION:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission denied
                    // Optionally, guide user to notification settings
                    Toast.makeText(this, "Turn on Notification Permission in Mobile Settings", Toast.LENGTH_SHORT).show();
                }
            }
            // Handle other permissions if needed
        }
    }

    private void recommendationKindGetPlaceRadius(double lat, double lon) {
        List<PlaceDetails> allPlacesList = new ArrayList<>();
        List<PlaceDetails> firebasePlaceDetails = new ArrayList<>();

        // Initialize Firebase
        db = FirebaseDatabase.getInstance();
        fbuser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbuser != null) {
            uid = fbuser.getUid();
            myRef = db.getReference("Favourites").child(uid);
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return; // Exit the method if the user is not authenticated
        }

        // Use ExecutorService to manage asynchronous task
        executor.execute(() -> {
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PlaceDetails place = snapshot.getValue(PlaceDetails.class);
                        if (place != null && !containsPlace(allPlacesList, place)) {
                            firebasePlaceDetails.add(place);
                        }
                    }
                    allPlacesList.addAll(firebasePlaceDetails);

                    // Once data is fetched, perform further processing
                    runOnUiThread(() -> {
                        placeHistoryDB = new SavePlaceHistoryDBHandler(HomeActivity.this);
                        List<PlaceDetails> placeHistory = placeHistoryDB.getAllPlaceDetailsWithCountsAndDates();
                        for (PlaceDetails place : placeHistory){
                            if (place != null && !containsPlace(allPlacesList, place)) {
                                allPlacesList.add(place);
                            }
                        }
                        getPlaceRadius(lat, lon, determineRecommendedKinds(allPlacesList, firebasePlaceDetails, placeHistory));
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    runOnUiThread(() -> {
                        Toast.makeText(HomeActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }

    private boolean containsPlace(List<PlaceDetails> list, PlaceDetails place) {
        for (PlaceDetails item : list) {
            if (item.getPlaceId().equals(place.getPlaceId())) {
                return true;
            }
        }
        return false;
    }

    // Recommendation Algorithm for getting recommended kinds for user
    private String determineRecommendedKinds(List<PlaceDetails>allPlacesList, List<PlaceDetails> firebasePlaceDetails, List<PlaceDetails> placeHistory) {
        if (allPlacesList.size() < 5){
            return "resorts,amusements,natural,food_courts";
        }

        Map<String, Double> kindScoreMap = new HashMap<>();

        // Assign scores based on favorite places
        for (PlaceDetails place : firebasePlaceDetails) {
            if (place.getKinds() != null) {
                String[] kinds = place.getKinds().split(",");
                for (String kind : kinds) {
                    kind = kind.trim();
                    if (EXCLUDED_KINDS.contains(kind)) continue; // Skip excluded kinds
                    double currentScore = kindScoreMap.containsKey(kind) ? kindScoreMap.get(kind) : 0.0;
                    kindScoreMap.put(kind, currentScore + FAVORITE_PLACE_SCORE);
                }
            }
        }

        // Assign scores based on place history
        for (PlaceDetails place : placeHistory) {
            if (place.getKinds() != null){
                String[] kinds = place.getKinds().split(",");
                for (String kind : kinds) {
                    kind = kind.trim();
                    if (EXCLUDED_KINDS.contains(kind)) continue; // Skip excluded kinds
                    double viewCountScore = place.getInsertCount() * VIEW_COUNT_WEIGHT;
                    double recentViewScore = calculateRecencyScore(place.getDateAdded()) * RECENT_VIEW_WEIGHT;

                    double currentScore = kindScoreMap.containsKey(kind) ? kindScoreMap.get(kind) : 0.0;
                    kindScoreMap.put(kind, currentScore + viewCountScore + recentViewScore);
                }
            }
        }

        // Convert map entries to a list and sort by score in descending order
        List<Map.Entry<String, Double>> kindScoreList = new ArrayList<>(kindScoreMap.entrySet());
        Collections.sort(kindScoreList, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> entry1, Map.Entry<String, Double> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });

        // Get the top 5 kinds or fewer if there aren't 5
        StringBuilder recommendedKinds = new StringBuilder();
        int count = 0;
        for (Map.Entry<String, Double> entry : kindScoreList) {
            if (count > 0) {
                recommendedKinds.append(","); // Append comma between kinds
            }
            recommendedKinds.append(entry.getKey());
            count++;
            if (count >= 4) break; // Stop if we have collected 5 kinds
        }

        if (!recommendedKinds.toString().isEmpty()) {
            Log.d("RecommendedKinds", recommendedKinds.toString());
        }

        // Return the combined kinds, or a default string if no kinds are found
        return recommendedKinds.toString().isEmpty() ? "resorts,amusements,natural,food_courts" : recommendedKinds.toString();
    }

    private double calculateRecencyScore(long dateAdded) {
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - dateAdded;
        long oneDayInMillis = 24 * 60 * 60 * 1000;
        long daysSinceViewed = timeDifference / oneDayInMillis;
        return BASE_RECENT_VIEW_SCORE / (daysSinceViewed + 1);
    }

    // Method to set up the notification bell
    public void setNotificationBell() {
        // Storing data into SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        notificationBell = findViewById(R.id.notification_btn);
        updateNotificationBellDrawable();
        boolean bothPermissions = hasAlarmPermissions() && hasNotificationPermission(HomeActivity.this);
        boolean savedPer = sharedPreferences.getBoolean("mpn", false);

        if (bothPermissions && savedPer) {
            notificationBell.setImageResource(R.drawable.baseline_notifications_active_24);
        } else {
            notificationBell.setImageResource(R.drawable.baseline_notifications_off_24);
        }
        Log.d(TAG, "Before bothPermissions: " + bothPermissions);
        Log.d(TAG, "Before savedPer: " + savedPer);


        notificationBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable currentDrawable = notificationBell.getDrawable();
                Drawable activeDrawable = ContextCompat.getDrawable(v.getContext(), R.drawable.baseline_notifications_active_24);
                Drawable offDrawable = ContextCompat.getDrawable(v.getContext(), R.drawable.baseline_notifications_off_24);


                boolean bothPermissions = hasAlarmPermissions() && hasNotificationPermission(HomeActivity.this);
                boolean savedPer = sharedPreferences.getBoolean("mpn", false);


                // Initialize the notification bell state
                isNotificationEnabled = bothPermissions && savedPer;


                // Toggle the state flag
                isNotificationEnabled = !isNotificationEnabled;
                SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putBoolean("mpn", isNotificationEnabled);

                myEdit.apply();
                // Handle notification based on the new state
                handleNotification(isNotificationEnabled);
                Log.d(TAG, "isNotificationEnabled: " + isNotificationEnabled);
                Log.d(TAG, "bothPermissions: " + bothPermissions);
                Log.d(TAG, "savedPer: " + savedPer);
                // Update the drawable based on the new state
                updateNotificationBellDrawable();
            }
        });
    }


    // Method to update the notification bell drawable
    private void updateNotificationBellDrawable() {
        Log.d(TAG, "updateNotificationBellDrawable: " + isNotificationEnabled);
        if (isNotificationEnabled) {
            notificationBell.setImageResource(R.drawable.baseline_notifications_active_24);
        } else {
            notificationBell.setImageResource(R.drawable.baseline_notifications_off_24);
        }
    }
    // Method to check if alarm permissions are granted
    public boolean hasAlarmPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            return alarmManager.canScheduleExactAlarms();
        }
        return true; // Permissions not needed on older versions
    }

    // Method to request alarm permissions
    private void getAlarmPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent requestPermissionIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                try {
                    startActivity(requestPermissionIntent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(HomeActivity.this, "No app can handle this request", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Method to check if notification permissions are granted
    public boolean hasNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // No need to check for older versions
    }

    // Method to request notification permissions
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFICATION_PERMISSION);
        }
    }

    // Method to handle notifications
    public void handleNotification(boolean isChecked) {
        DatabaseHandler db = new DatabaseHandler(HomeActivity.this, null, null, 1);
        // Storing data into SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("settings",MODE_PRIVATE);
        notificationBell = findViewById(R.id.notification_btn);
        // If checked, ask for permissions and schedule reminders/notifications
        if (isChecked) {
            getAlarmPermissions();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!hasNotificationPermission(HomeActivity.this)) {
                    requestNotificationPermission();
                }
            }
            try {
                db.scheduleNotification(HomeActivity.this);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            notificationBell.setImageResource(R.drawable.baseline_notifications_active_24);
        } else {
            // If not checked, delete all reminders
            db.cancelAllReminders(HomeActivity.this);
            notificationBell.setImageResource(R.drawable.baseline_notifications_off_24);
        }

        // Update shared preference
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putBoolean("mpn", isChecked);
        myEdit.apply();
    }
}


