package sg.edu.np.mad.travelhub;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    ArrayList<Place> placeList = new ArrayList<>();
    ArrayList<Place> recommendedPlaceList = new ArrayList<>();

    Button currentActiveBtn;
    int color1;
    int color2;
    int color3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageButton chatButton = findViewById(R.id.chat_btn);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, Chatbot.class);
                startActivity(intent);
            }
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

//        ImageButton notificationBell = findViewById(R.id.notification_btn);
//        notificationBell.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(HomeActivity.this, ConvertCurrency.class));
//            }
//        });

        // Bottom Navigation View Logic to link to the different master activities
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavMenu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_calendar){
                startActivity(new Intent(this, ViewEvents.class));
                overridePendingTransition(0, 0);
                finish();
            } else if (item.getItemId() == R.id.bottom_currency) {
                startActivity(new Intent(this, ConvertCurrency.class));
                overridePendingTransition(0, 0);
                finish();
            } else if (item.getItemId() == R.id.bottom_profile) {
                startActivity(new Intent(this, Profile.class));
                overridePendingTransition(0, 0);
                finish();
            }
            return true;
        });

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

        // Logic for setting the color of each filter button when activated/selected
        for (Button btn : btnList) {
            btn.setOnClickListener(v -> {
                if(!(currentActiveBtn == btn)){
                    enableFilterBtn(btn, currentActiveBtn);
                    currentActiveBtn = btn;
                }
            });
        }


        TextView dropdown = findViewById(R.id.dropdown);
        String defaultOption = "All";
        dropdown.setText(defaultOption);

        // Creating a list to store all the available cities
        ArrayList<String> cityList = populateCityList();

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
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(HomeActivity.this, android.R.layout.simple_list_item_1, cityList);

            // Set Adapter
            listView.setAdapter(arrayAdapter);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Filter Array List
                    arrayAdapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // When item selected from list
                    // Set selected item on text view
                    dropdown.setText(arrayAdapter.getItem(position));
                    // Dismiss Dialog
                    dialog.dismiss();
                }
            });
        });

        setUpTopPlaceModels();
        setUpRecommendedPlaceModels();

        RecyclerView recommended_Places_Recyclerview = findViewById(R.id.recommendedPlaces);
        Top_Places_Recyclerview_Adapter recommendedPlaceAdapter = new Top_Places_Recyclerview_Adapter(this, recommendedPlaceList);
        recommended_Places_Recyclerview.setAdapter(recommendedPlaceAdapter);
        recommended_Places_Recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        RecyclerView top_Places_Recyclerview = findViewById(R.id.top_places_recyclerview);
        Recommended_Places_Recyclerview_Adapter topPlaceAdapter = new Recommended_Places_Recyclerview_Adapter(this, placeList);
        top_Places_Recyclerview.setAdapter(topPlaceAdapter);
        top_Places_Recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        adjustRecyclerViewHeight(top_Places_Recyclerview);


        // Search Bar Place Logic Here (Meant for stage 2 Reference, Please Ignore
//        SearchView searchView = findViewById(R.id.searchView);
//        searchView.clearFocus();
//        searchView.setOnQueryTextListener(new OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                filterList(newText);
//                return true;
//            }
//        });

//        RecyclerView recyclerView = findViewById(R.id.SearchRecyclerView);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @NonNull
    private static ArrayList<String> populateCityList() {
        ArrayList<String> cityList = new ArrayList<>();
        cityList.add("All");
//        cityList.add("Current Location");
        cityList.add("Singapore, Singapore");
        cityList.add("Aspen, USA");
        cityList.add("New York City, USA");
        cityList.add("Los Angeles, USA");
        cityList.add("Chicago, USA");
        cityList.add("Houston, USA");
        cityList.add("Phoenix, USA");
        cityList.add("Philadelphia, USA");
        cityList.add("San Antonio, USA");
        cityList.add("San Diego, USA");
        cityList.add("Dallas, USA");
        cityList.add("San Jose, USA");
        cityList.add("Austin, USA");
        cityList.add("Jacksonville, USA");
        cityList.add("Fort Worth, USA");
        cityList.add("Columbus, USA");
        cityList.add("Charlotte, USA");
        cityList.add("Indianapolis, USA");
        cityList.add("Seattle, USA");
        cityList.add("Denver, USA");
        cityList.add("Washington, USA");
        cityList.add("Boston, USA");
        cityList.add("El Paso, USA");
        cityList.add("Nashville, USA");
        cityList.add("Detroit, USA");
        cityList.add("Oklahoma City, USA");
        cityList.add("Portland, USA");
        cityList.add("Las Vegas, USA");
        cityList.add("Memphis, USA");
        cityList.add("Louisville, USA");
        cityList.add("Baltimore, USA");
        cityList.add("Milwaukee, USA");
        cityList.add("Albuquerque, USA");
        cityList.add("Tucson, USA");
        cityList.add("Fresno, USA");
        cityList.add("Sacramento, USA");
        cityList.add("Kansas City, USA");
        cityList.add("Long Beach, USA");
        cityList.add("Mesa, USA");
        cityList.add("Atlanta, USA");
        cityList.add("Colorado Springs, USA");
        cityList.add("Virginia Beach, USA");
        cityList.add("Raleigh, USA");
        cityList.add("Omaha, USA");
        cityList.add("Miami, USA");
        cityList.add("Oakland, USA");
        cityList.add("Minneapolis, USA");
        cityList.add("Tulsa, USA");
        cityList.add("Wichita, USA");
        return cityList;
    }

//    private void filterList(String text) {
//        List<String> filteredList = new ArrayList<>();
//        for (String place: placeList){
//            if (place.toLowerCase().contains(text.toLowerCase())){
//                filteredList.add(place);
//            }
//        }
//
//        if (filteredList.isEmpty()){
//            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void enableFilterBtn(Button activatedBtn, @Nullable Button deactivatedBtn){
        activatedBtn.setTextColor(color2);
        activatedBtn.setBackgroundColor(color3);

        if (deactivatedBtn != null){
            deactivatedBtn.setTextColor(getResources().getColor(R.color.unselectedFilterText));
            deactivatedBtn.setBackgroundColor(getResources().getColor(R.color.unselectedFilterBackground));
        }
    }

    private void setUpTopPlaceModels(){
        String[] places = getResources().getStringArray(R.array.top_places);

        for (String s : places) {
            String[] placeAttributes = s.split(";");

            if (placeAttributes.length == 6) {
                String name = placeAttributes[0].trim();
                String city = placeAttributes[1].trim();
                String state = placeAttributes[2].trim();
                String description = placeAttributes[3].trim();
                double rating = Double.parseDouble(placeAttributes[4].trim());
                String imgUrl = placeAttributes[5].trim();

                // Create a new Place object with the parsed attributes
                Place place = new Place(name, city, state, description, rating, imgUrl);

                // Add the new Place object to the placeList
                placeList.add(place);
            } else {
                Log.e("setUpPlaceModels", "Invalid place format: " + s);
            }
        }
    }

    private void setUpRecommendedPlaceModels(){
        String[] places = getResources().getStringArray(R.array.recommended_places);

        for (String s : places) {
            String[] placeAttributes = s.split(";");

            if (placeAttributes.length == 6) {
                String name = placeAttributes[0].trim();
                String city = placeAttributes[1].trim();
                String state = placeAttributes[2].trim();
                String description = placeAttributes[3].trim();
                double rating = Double.parseDouble(placeAttributes[4].trim());
                String imgUrl = placeAttributes[5].trim();

                // Create a new Place object with the parsed attributes
                Place place = new Place(name, city, state, description, rating, imgUrl);

                // Add the new Place object to the placeList
                recommendedPlaceList.add(place);
            } else {
                Log.e("setUpPlaceModels", "Invalid place format: " + s);
            }
        }
    }

    // Function to dynamically adjust RecyclerView height
//    private void adjustRecyclerViewHeight(final RecyclerView recyclerView) {
//        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                RecyclerView.Adapter adapter = recyclerView.getAdapter();
//                if (adapter != null) {
//                    int totalHeight = 0;
//                    for (int i = 0; i < adapter.getItemCount(); i++) {
//                        View listItem = adapter.onCreateViewHolder(recyclerView, adapter.getItemViewType(i)).itemView;
//                        listItem.measure(View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
//                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//                        totalHeight += listItem.getMeasuredHeight();
//                    }
//                    ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
//                    params.height = totalHeight + (recyclerView.getItemDecorationCount() * 10); // Add extra space for dividers
//                    params.height += recyclerView.getPaddingTop() + recyclerView.getPaddingBottom(); // Include padding
//                    recyclerView.setLayoutParams(params);
//                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                }
//            }
//        });
//    }
}

