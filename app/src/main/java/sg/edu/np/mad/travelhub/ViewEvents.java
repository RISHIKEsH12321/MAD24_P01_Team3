package sg.edu.np.mad.travelhub;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ViewEvents extends AppCompatActivity {
    ActivityResultLauncher<String[]> mPermissionResultLauncher;
    private boolean isReadPermissoin = false;
    private boolean isReaImage = false;
    private boolean isReaSelectedImage = false;
    private DatabaseHandler dbHandler;
    private ViewEventAdapter eventAdapter;
    private ArrayList<CompleteEvent> events;
    private RecyclerView eventsContainer;
    int color1;
    int color2;
    int color3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_events);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.VEmain), (v, insets) -> {
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

        //Get IDs
        ImageButton vebtn = findViewById(R.id.VEsaveButton);
        TextView title = findViewById(R.id.calendartitle);

        //Change Colors
        title.setTextColor(color2);
        Drawable arrowDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_add_24);
        arrowDrawable.setTint(color1);
        vebtn.setImageDrawable(arrowDrawable);

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
        bottomNavigationView.setSelectedItemId(R.id.bottom_calendar);
        bottomNavigationView.setOnApplyWindowInsetsListener(null);
        bottomNavigationView.setPadding(0,0,0,0);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home){
                startActivity(new Intent(this, HomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0, 0);
                finish(); // Finish current activity if going back to HomeActivity
                return true;
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

        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> o) {
                        if (o.get(Manifest.permission.READ_EXTERNAL_STORAGE) != null){
                            isReadPermissoin = o.get(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                        if (o.get(Manifest.permission.READ_MEDIA_IMAGES) != null){
                            isReaImage = o.get(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                        if (o.get(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) != null){
                            isReaSelectedImage = o.get(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
                        }

                    }
        });
        requestPermissions();
        //Initialise dbhandlet to get and delete events
        dbHandler = new DatabaseHandler(this, null, null, 1);
//        dbHandler.dropTable();
        CalendarView calendarView = findViewById(R.id.VECalenderView);
        //Container to store all events on that day
        eventsContainer = findViewById(R.id.VEEventsContainer);

        // Get today's date
        long currentTimeMillis = System.currentTimeMillis();
        calendarView.setDate(currentTimeMillis, true, true);

        // Get and display today's event
        String selectedDate = getFormattedDate(currentTimeMillis);
        displayEvents(dbHandler.getEventONDate(selectedDate));


        //Buttons for new event & Scan Qr Code
        ImageView addEvent = findViewById(R.id.VEsaveButton);
        ImageView scanQrCode = findViewById(R.id.VEscanQrCode);

        //Go to Create New Activity
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewEvent(v);
            }
        });

        scanQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });


        //When New Date is Picked, Get and Display that days events
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                int displayMonth = month + 1;
                String selectedDate = getFormattedDate(year, displayMonth, dayOfMonth);

                Log.d("VIEW EVENTS DATE", "Date: "+selectedDate);
                events = dbHandler.getEventONDate(selectedDate);
                Log.d("VIEW EVENTS DATA", "DATA LOG: " + events);

                displayEvents(events);

            }
        });
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume Up to Turn on Flash");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String jsonData = result.getContents();
            String eventSummary = generateEventSummary(jsonData);

            AlertDialog.Builder builder = new AlertDialog.Builder(ViewEvents.this);
            builder.setTitle("Event Details")
                    .setMessage(eventSummary) // Use the summary string here
                    .setPositiveButton("Add to Calendar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Log.d("QR CODE JSON", "Event Data: \n" + jsonData);
                            Intent intent = new Intent(getApplicationContext(), EventManagement.class);
                            intent.putExtra("purpose", "ScanToCreate");
                            intent.putExtra("CompleteEvent", jsonToCompleteEvent(jsonData));
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    });

    public static String generateEventSummary(String jsonData) {
        String summary = "";

        try {
            // Parse the JSON data
            JSONObject eventData = new JSONObject(jsonData);

            // Extract values
            String eventName = eventData.optString("eventName", "N/A");
            String date = eventData.optString("date", "N/A");
            String category = eventData.optString("category", "N/A");

            JSONArray itineraryEventList = eventData.optJSONArray("itineraryEventList");
            JSONArray toBringItems = eventData.optJSONArray("toBringItems");
            JSONArray reminderList = eventData.optJSONArray("reminderList");
            JSONArray notesList = eventData.optJSONArray("notesList");

            int itineraryEventCount = (itineraryEventList != null) ? itineraryEventList.length() : 0;
            int itemCount = (toBringItems != null) ? toBringItems.length() : 0;
            int reminderCount = (reminderList != null) ? reminderList.length() : 0;
            int notesCount = (notesList != null) ? notesList.length() : 0;

            // Create the summary string
            summary = String.format(
                    "Event Name: %s\n" +
                    "Date: %s\n" +
                    "Category: %s\n" +
                    "Itinerary Events: %d\n" +
                    "Items to Bring: %d\n" +
                    "Reminders: %d\n" +
                    "Notes: %d",
                    eventName, date, category, itineraryEventCount, itemCount, reminderCount, notesCount);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return summary;
    }

    private void displayEvents(ArrayList<CompleteEvent> events){
        Log.d("IMAGEATTACHMENTINIMAGES", "displayEvents: " + events.toString());
        eventAdapter = new ViewEventAdapter(ViewEvents.this, events);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ViewEvents.this);

        eventsContainer.setLayoutManager(mLayoutManager);
        eventsContainer.setItemAnimator(new DefaultItemAnimator());
        eventsContainer.setAdapter(eventAdapter);

        //Delete that events id when Clicked from database and recyclerView
        eventAdapter.setOnItemClickListener(new ViewEventAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(int position) {
                // Capture event ID before removing item
                int id = Integer.parseInt(events.get(position).eventID);
                Log.d("DELETE EVENT TAG", "ID for deleted event = " + id);

                // Remove item from database
                dbHandler.deleteEventById(id);

                // Remove item from list and notify adapter
                events.remove(position);
                eventAdapter.notifyItemRemoved(position);
                eventAdapter.notifyItemRangeChanged(position, events.size());
            }
        });
    }

    private String getMonthFormat(int month) {
        if(month == 1) return "JAN";
        if(month == 2) return "FEB";
        if(month == 3) return "MAR";
        if(month == 4) return "APR";
        if(month == 5) return "MAY";
        if(month == 6) return "JUN";
        if(month == 7) return "JUL";
        if(month == 8) return "AUG";
        if(month == 9) return "SEP";
        if(month == 10) return "OCT";
        if(month == 11) return "NOV";
        if(month == 12) return "DEC";
        return "JAN";
    }


    private String getFormattedDate(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is zero-based
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return getFormattedDate(year, month, dayOfMonth);
    }

    private String getFormattedDate(int year, int month, int dayOfMonth) {
        return getMonthFormat(month) + " " + dayOfMonth + " " + year; // For database paramaeter
    }


    // Method to handle button click to go back
    public void goBack(View view) {
        finish(); // Close the current activity and return to the previous one
    }

    public void createNewEvent(View view){
        //Go to EventManageMent class to create new event
        Intent intent = new Intent(view.getContext(), EventManagement.class);
        intent.putExtra("purpose", "Create");
        startActivity(intent);
    }

    public void showQrCodeFragment(String jsonData) {
        Fragment qrCodeFragment = QrCodeFragment.newInstance(jsonData);
        FrameLayout qrFrag = findViewById(R.id.qrCodeFragmentContainer);
        qrFrag.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.qrCodeFragmentContainer, qrCodeFragment, "QrCodeFragment")
                .addToBackStack(null)
                .commit();
    }

    public CompleteEvent jsonToCompleteEvent(String jsonData){
        Gson gson = new Gson();
        return gson.fromJson(jsonData, CompleteEvent.class);
    }

    private void requestPermissions() {
        isReadPermissoin = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;

        isReaImage = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED;

        isReaSelectedImage = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        ) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionRequest = new ArrayList<String>();
        Log.d("requestPermissions", "READ_EXTERNAL_STORAGE: " + isReadPermissoin);
        Log.d("requestPermissions", "READ_MEDIA_IMAGES: " + isReaImage);
        if (!isReadPermissoin) {
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!isReaImage) {
            permissionRequest.add(Manifest.permission.READ_MEDIA_IMAGES);
        }
        if (!isReaSelectedImage) {
            permissionRequest.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
        }

        if (!permissionRequest.isEmpty()) {
            mPermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
        }
    }
}
