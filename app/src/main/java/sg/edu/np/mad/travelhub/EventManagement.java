package sg.edu.np.mad.travelhub;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.media.metrics.Event;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class EventManagement extends AppCompatActivity {
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private static final int PICK_FILE_REQUEST_CODE = 001;
//    public static final String ACTION_REQUEST_SCHEDULE_EXACT_ALARM = ;

    private static final String TAG = "Attachment";
    int hour;
    int min;

    int startHour,startMinute,endHour,endMinute;
    String startAmOrPmm, endAmOrPml;
    String editEventID;

    // Data Inputs
    EditText EMtitle;
    Spinner EMcategoryDropdown;
    ArrayList<ImageAttachment> attachmentImageList;
    ArrayList<ItineraryEvent> itineraryEventList;
    ArrayList<ToBringItem> toBringItems;
    ArrayList<String> notesList;
    ArrayList<Reminder> reminderList;
    ImageButton finalSaveButton;
    ImageButton editEventButton;
    ImageButton management;
    ImageButton bring;
    ImageButton attachment;
    ImageButton reminder;
    ImageButton notes;
    ImageButton backbtn;
    //Image Container
    LinearLayout attachmentContainer;

    // Adapters
    ArrayAdapter<CharSequence> spinnerAdapter;
    EventAdapter mAdapter;
    BringItemAdapter itemAdapter;
    NotesAdapter notesAdapter;
    ReminderAdapter remidnerAdapter;

    //Adapter Container
    RecyclerView eventRvView;
    RecyclerView bringItemRvView;
    RecyclerView notesContainer;
    RecyclerView reminderContainer;
    //Deleted Items temp Variable
    ItineraryEvent deletedEvent = null;
    ToBringItem deletedItem = null;
    String deletedNote = null;
    Reminder deletedReminder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_management);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.EMmain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        createNotificationChannel();
        initViewsAndAdapters();

        SharedPreferences preferences = getSharedPreferences("spinner_preferences", MODE_PRIVATE);
        int selectedSpinnerPosition = preferences.getInt("selected_spinner_position", 0);
        String selectedTheme = getResources().getStringArray(R.array.themes)[selectedSpinnerPosition];
        int color1;
        int color2;
        switch (selectedTheme) {
            case "Default":
                color1 = getResources().getColor(R.color.main_orange);
                color2 = getResources().getColor(R.color.main_orange);
                break;
            case "Watermelon":
                color1 = getResources().getColor(R.color.wm_green);
                color2 = getResources().getColor(R.color.wm_red);
                break;
            case "Neon":
                color1 = getResources().getColor(R.color.nn_pink);
                color2 = getResources().getColor(R.color.nn_cyan);
                break;
            case "Protanopia":
                color1 = getResources().getColor(R.color.pro_purple);
                color2 = getResources().getColor(R.color.pro_green);
                break;
            case "Deuteranopia":
                color1 = getResources().getColor(R.color.deu_yellow);
                color2 = getResources().getColor(R.color.deu_blue);
                break;
            case "Tritanopia":
                color1 = getResources().getColor(R.color.tri_orange);
                color2 = getResources().getColor(R.color.tri_green);
                break;
            default:
                color1 = getResources().getColor(R.color.main_orange);
                color2 = getResources().getColor(R.color.main_orange);
                break;
        }

        //Change color for all drawables
        // Get drawables
//        Drawable addBtnDrawable = ContextCompat.getDrawable(this, R.drawable.add_btn);
//        Drawable plusDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_add_24_small);
//        // Apply tint color only to the add_btn drawable
//        Drawable wrappedAddBtnDrawable = DrawableCompat.wrap(addBtnDrawable);
//        DrawableCompat.setTint(wrappedAddBtnDrawable, color2);
//        // Create a LayerDrawable and add both drawables to it
//        Drawable[] layers = new Drawable[2];
//        layers[0] = wrappedAddBtnDrawable;
//        layers[1] = plusDrawable;
//        LayerDrawable layerDrawable = new LayerDrawable(layers);
        Drawable addBtnDrawable = ContextCompat.getDrawable(this, R.drawable.add_btn);
        Drawable wrappedAddBtnDrawable = DrawableCompat.wrap(addBtnDrawable);
        DrawableCompat.setTint(wrappedAddBtnDrawable, color2);

        // Set the LayerDrawable to the ImageButton
        management.setBackground(wrappedAddBtnDrawable);
        bring.setBackground(wrappedAddBtnDrawable);
        attachment.setBackground(wrappedAddBtnDrawable);
        reminder.setBackground(wrappedAddBtnDrawable);
        notes.setBackground(wrappedAddBtnDrawable);

        Drawable arrow = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_ios_24);
        arrow.setTint(color1);
        backbtn.setImageDrawable(arrow);

//        ImageButton savebtn = findViewById(R.id.saveButton);
        Drawable add = ContextCompat.getDrawable(this, R.drawable.baseline_check_24);
        add.setTint(color1);
        finalSaveButton.setImageDrawable(add);

        // Wrap the drawable to ensure compatibility
        Drawable wrappedArrow = DrawableCompat.wrap(arrow);

        // Set the tint
        DrawableCompat.setTint(wrappedArrow, color1);


        // Setting the Name of the event to the place selected
        Intent intent = getIntent();
        String purpose = intent.getStringExtra("purpose");
        switch (Objects.requireNonNull(purpose)) {
            case "Edit":
                // Code to handle the edit case
                CompleteEvent event = (CompleteEvent) intent.getSerializableExtra("CompleteEvent");
                if (event != null) {
                    populateData(event);
                }
                finalSaveButton.setVisibility(View.GONE);
                editEventID = event.eventID;
                break;

            case "ScanToCreate":
                // Code to handle the edit case
                CompleteEvent scanEvent = (CompleteEvent) intent.getSerializableExtra("CompleteEvent");
                if (scanEvent != null) {
                    populateData(scanEvent);
                }
                editEventButton.setVisibility(View.GONE);
                dateButton.setText(scanEvent.date);
                break;

            case "Create":
                // Code to handle the create case
                PlaceDetails place = (PlaceDetails) intent.getParcelableExtra("place");
                if (place != null) {
                    Log.d("PlaceName", place.getName());
                    EMtitle.setText(place.getName());
                }
                CompleteEvent emptyEvent = new CompleteEvent();
                populateData(emptyEvent);
                editEventButton.setVisibility(View.GONE);
                dateButton.setText(getTodaysDate());
                break;

            default:
                // Handle any unexpected values
                break;
        }


        // Setup other UI components and listeners
        setupUIComponents();


        // Get the parent layout
        ConstraintLayout parentLayout = findViewById(R.id.EMmain);

        // Set a touch listener on the parent layout
        parentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Clear focus from the EditText when the parent layout is touched
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    View currentFocus = getCurrentFocus();
                    if (currentFocus != null) {
                        currentFocus.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                    }
                }
                return true;
            }
        });


        //For Date Picker in Itinerary
        initDatePicker();

        //Image Display and Selection
//        ArrayList<ImageAttachment> attachmentImageList = new ArrayList<>();
//        ImageButton selectFileButton = findViewById(R.id.EMattchmentBtn);

        //Getting Image From Local Storage
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            try {
                                Uri fileUri = result.getData().getData();
                                Log.d(TAG, "onCreate: " + fileUri);

                                if (fileUri != null) {
                                    String mimeType = getContentResolver().getType(fileUri);
                                    if (mimeType != null && mimeType.startsWith("image/")) {
                                        ImageAttachment imageAttachment = new ImageAttachment();
                                        imageAttachment.setURI(String.valueOf(fileUri));

                                        imageAttachment.setExampleDrawable("plane_ticket_example");
                                        attachmentImageList.add(imageAttachment);

                                        ImageView imageView = new ImageView(EventManagement.this);

                                        //Displaying the drawble not the image URI
                                        // Use Glide to load the image into the ImageView
                                        Glide.with(EventManagement.this)
                                                .load(fileUri)
                                                .apply(new RequestOptions().override(LinearLayout.LayoutParams.WRAP_CONTENT, 100)) // Set height to 100dp
                                                .into(imageView);

                                        // Set layout parameters for ImageView
                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                200 // Set height to 100dp
                                        );
                                        imageView.setLayoutParams(layoutParams);
                                        showImageDialog(imageView, imageAttachment);
                                        imageView.setOnClickListener(v -> {
                                            showImageDialog(imageView, imageAttachment);
                                        });

                                        attachmentContainer.addView(imageView);
                                    } else {
                                        Toast.makeText(this, "Unsupported file type", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(this, "Error: Null URI", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(this, "Error selecting file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        attachment.setOnClickListener(view -> {
            Intent fileInput = new Intent(Intent.ACTION_GET_CONTENT);
            fileInput.setType("image/*");
            activityResultLauncher.launch(fileInput);
        });


        //Dialog For Adding Events
//        ImageButton btnAddEvent = findViewById(R.id.EMitineraryAddEventNameBtn);
        //Mangaging RecyclerView for Events
//        RecyclerView eventRvView =findViewById(R.id.EMrvViewItinerary);
//        ArrayList<ItineraryEvent> itineraryEventList = new ArrayList<ItineraryEvent>();
//        EventAdapter
//        mAdapter = new EventAdapter(itineraryEventList);

//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

//        eventRvView.setLayoutManager(mLayoutManager);
//        eventRvView.setItemAnimator(new DefaultItemAnimator());
//        eventRvView.setAdapter(mAdapter);
//        mAdapter.setOnItemClickListener(new EventAdapter.OnItemClickListener(){
//            @Override
//            public void onItemClick(int position) {
//                //Delete the item
//                itineraryEventList.remove(position);
//                //Notify Adapter
//                mAdapter.notifyItemRemoved(position);
//            }
//        });

//        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //Mangaing Adding Items
//        ImageButton btnAddBringItem = findViewById(R.id.EMitineraryAddBringItemBtn);
//        RecyclerView bringItemRvView =findViewById(R.id.EMrvViewBringList);
//        ArrayList<ToBringItem> toBringItems = new ArrayList<ToBringItem>();
//        BringItemAdapter
//        itemAdapter = new BringItemAdapter(toBringItems);

//        LinearLayoutManager itemLayoutManager = new LinearLayoutManager(this);
//
//        bringItemRvView.setLayoutManager(itemLayoutManager);
//        bringItemRvView.setItemAnimator(new DefaultItemAnimator());
//        bringItemRvView.setAdapter(itemAdapter);
//        itemAdapter.setOnItemClickListener(new BringItemAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                //Delete the item
//                toBringItems.remove(position);
//                //Notify Adapter
//                itemAdapter.notifyItemRemoved(position);
//            }
//        });
//
//        itemLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //Add Notes
//        ImageButton btnAddNotes = findViewById(R.id.EMnotesBtn);
//        RecyclerView notesContainer = findViewById(R.id.EMnotesItem);
//        ArrayList<String> notesList = new ArrayList<>();
        //NotesAdapter
//        notesAdapter = new NotesAdapter(notesList);

//        LinearLayoutManager notesLayoutManager = new LinearLayoutManager(this);

//        notesContainer.setLayoutManager(notesLayoutManager);
//        notesContainer.setItemAnimator(new DefaultItemAnimator());
//        notesContainer.setAdapter(notesAdapter);
//        notesAdapter.setOnItemClickListener(new NotesAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                //Delete the item
//                notesList.remove(position);
//                //Notify Adapter
//                notesAdapter.notifyItemRemoved(position);
//            }
//        });

        //Add Reminders
//        ImageButton btnAddReminder = findViewById(R.id.EMreminderAddBtn);
//        RecyclerView reminderContainer = findViewById(R.id.EMreminderItems);
//        ArrayList<Reminder> reminderList = new ArrayList<>();
//        ReminderAdapter
//        remidnerAdapter = new ReminderAdapter(reminderList);

//        LinearLayoutManager reminderLayoutManager = new LinearLayoutManager(this);

//        reminderContainer.setLayoutManager(reminderLayoutManager);
//        reminderContainer.setItemAnimator(new DefaultItemAnimator());
//        reminderContainer.setAdapter(remidnerAdapter);
//        remidnerAdapter.setOnItemClickListener(new ReminderAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                //Delete the item
//                reminderList.remove(position);
//                //Notify Adapter
//                remidnerAdapter.notifyItemRemoved(position);
//            }
//        });

        //Lists of data to be added to database
        //1. attachmentImageList
        //2. itineraryEventList
        //3. toBringItems
        //4. notesList
        //5. reminderList
        //6. Date
        //7. Complete Event Title

        //Adding to event and its data to database
        DatabaseHandler dbHandler = new DatabaseHandler(this, null, null, 1);
//        dbHandler.dropTable();

        //Goes to previous Activity
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(v);
            }
        });

        //Create Alert for Event Creation when clicking buttons
        management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(EventManagement.this).inflate(R.layout.em_itinerary_dialog_layout, null);

                Button startTimeButton = view.findViewById(R.id.EMitineraryStartTimeButton);
                Button endTimeButton = view.findViewById(R.id.EMitineraryEndTimeButton);
                TextInputEditText eventName = view.findViewById(R.id.EMitineraryAddEventName);
                TextInputEditText editNotes = view.findViewById(R.id.EMitineraryAddNotes);

//                startHour,startMinute,endHour,endMinute;


                startTimeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog startTimePicker = new TimePickerDialog(EventManagement.this, R.style.CustomTimePickerDialog,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        startHour = hourOfDay;
                                        startMinute = minute;
                                        startAmOrPmm = (hourOfDay < 12) ? "AM" : "PM";
                                        startTimeButton.setText(
                                                String.format("%02d:%02d %s",
                                                        (hourOfDay % 12 == 0) ? 12 : hourOfDay % 12,
                                                        minute,
                                                        (hourOfDay < 12) ? "AM" : "PM"));
                                    }
                                }, startHour, startMinute, false); // false for 12-hour format
                        startTimePicker.show();
                    }
                });


                endTimeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog endTimePicker = new TimePickerDialog(EventManagement.this, R.style.CustomTimePickerDialog,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        endHour = hourOfDay;
                                        endMinute = minute;
                                        endAmOrPml = (hourOfDay < 12) ? "AM" : "PM";
                                        endTimeButton.setText(
                                                String.format("%02d:%02d %s",
                                                        (hourOfDay % 12 == 0) ? 12 : hourOfDay % 12,
                                                        minute,
                                                        (hourOfDay < 12) ? "AM" : "PM"));
                                    }
                                }, endHour, endMinute, false);
                        endTimePicker.show();
                    }
                });

                // Alert Creation
                androidx.appcompat.app.AlertDialog alertDialog = new MaterialAlertDialogBuilder(EventManagement.this)
                        .setTitle("Add Event")
                        .setView(view)
                        .setPositiveButton("Save", null) // Set listener to null for now
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();

                // Show the dialog
                alertDialog.show();

                // Override the positive button click listener after showing the dialog
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String eventNameText = eventName.getText().toString();
                        String eventNotesText = editNotes.getText().toString();
                        String startTimeText = startTimeButton.getText().toString();
                        String endTimeText = endTimeButton.getText().toString();

                        if (!eventNameText.isEmpty() && !eventNotesText.isEmpty() && !startTimeText.equals("Select Time") && !endTimeText.equals("Select Time")) {
                            ItineraryEvent itineraryEvent = new ItineraryEvent(
                                    eventNameText,
                                    eventNotesText,
                                    String.format("%02d", startHour),
                                    String.format("%02d", startMinute),
                                    String.format("%02d", endHour),
                                    String.format("%02d", endMinute)
                            );

                            itineraryEventList.add(itineraryEvent);

                            mAdapter.notifyItemInserted(itineraryEventList.size() - 1);
                            alertDialog.dismiss(); // Close the dialog only if all inputs are valid
                        } else {
                            Toast.makeText(v.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                            // Keep the dialog open by not calling alertDialog.dismiss()
                        }
                    }
                });
            }
        });

        //Create Alert for Adding Item
        bring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(EventManagement.this).inflate(R.layout.em_to_bring_item_dialog_layout, null);

                // Getting all input parameters (Item Name)
                TextInputEditText itemName = view.findViewById(R.id.EMitineraryAddBringItemInput);

                // Alert Creation
                androidx.appcompat.app.AlertDialog alertDialog = new MaterialAlertDialogBuilder(EventManagement.this)
                        .setTitle("Add Item To List")
                        .setView(view)
                        .setPositiveButton("Save", null) // Set listener to null for now
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();

                // Show the dialog
                alertDialog.show();

                // Override the positive button click listener after showing the dialog
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Getting Values
                        if (!itemName.getText().toString().isEmpty()) {
                            ToBringItem toBringItem = new ToBringItem();
                            toBringItem.itemName = itemName.getText().toString();
                            toBringItems.add(toBringItem);

                            // Notify the adapter about the new item
                            itemAdapter.notifyItemInserted(toBringItems.size() - 1);
                            alertDialog.dismiss(); // Close the dialog only if there is input
                        } else {
                            Toast.makeText(v.getContext(), "No Input", Toast.LENGTH_SHORT).show();
                            // Keep the dialog open by not calling alertDialog.dismiss()
                        }
                    }
                });
            }
        });

        //Create Alert for Adding Note
        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(EventManagement.this).inflate(R.layout.em_to_bring_item_dialog_layout, null);


                //Getting all input parameters (Notes)
                TextInputEditText itemName = view.findViewById(R.id.EMitineraryAddBringItemInput);


                // Alert Creation
                androidx.appcompat.app.AlertDialog alertDialog = new MaterialAlertDialogBuilder(EventManagement.this)
                        .setTitle("Add Notes")
                        .setView(view)
                        .setPositiveButton("Save", null) // Set listener to null for now
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();

                // Show the dialog
                alertDialog.show();

                // Override the positive button click listener after showing the dialog
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Getting Values
                        if (!itemName.getText().toString().isEmpty()) {
                            String notes = itemName.getText().toString();

                            notesList.add(notes);

                            // Notify the adapter about the new item
                            notesAdapter.notifyItemInserted(notesList.size() - 1);
                            alertDialog.dismiss(); // Close the dialog only if there is input
                        } else {
                            Toast.makeText(v.getContext(), "No Input", Toast.LENGTH_SHORT).show();
                            // Keep the dialog open by not calling alertDialog.dismiss()
                        }
                    }
                });
            }
        });

        //Create Alert for Adding Reminder
        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(EventManagement.this).inflate(R.layout.em_create_reminder_dialog_layout, null);
                TextInputEditText itemName = view.findViewById(R.id.EMitineraryAddBringItemInput);
                TimePicker timePicker = view.findViewById(R.id.EMReminderTimePicker);

                // Set initial time for the TimePicker
                timePicker.setIs24HourView(false);
                Calendar calendar = Calendar.getInstance();
                timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                timePicker.setMinute(calendar.get(Calendar.MINUTE));

                // Alert Creation
                // Create the dialog without a positive button listener initially
                androidx.appcompat.app.AlertDialog alertDialog = new MaterialAlertDialogBuilder(EventManagement.this)
                        .setTitle("Add Reminder")
                        .setView(view)
                        .setPositiveButton("Save", null) // Set listener to null for now
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();

                // Show the dialog
                alertDialog.show();

                // Override the positive button click listener after showing the dialog
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!itemName.getText().toString().isEmpty()) {
                            Reminder reminder = new Reminder();
                            reminder.reminderTitle = itemName.getText().toString();
                            reminder.reminderTime = String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute());


                            reminderList.add(reminder);
                            remidnerAdapter.notifyItemInserted(reminderList.size() - 1);
                            alertDialog.dismiss(); // Close the dialog only if there is input
                        } else {
                            Toast.makeText(v.getContext(), "No Input", Toast.LENGTH_SHORT).show();
                            // Keep the dialog open by not calling alertDialog.dismiss()
                        }
                    }
                });

            }

        });

        //Puts all data in a CompleteEvent Item and send it to database to be added to the tables
        finalSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Selected Date
                String date = String.valueOf(dateButton.getText());

                // Get the selected item
                Object selectedItem = EMcategoryDropdown.getSelectedItem();
                // Convert the selected item to a string
                String category = selectedItem.toString();

                // Getting Title Of Entire Event
                String title = String.valueOf(EMtitle.getText());

                // Creating Complete Event Object
                CompleteEvent dbEvent = new CompleteEvent(
                        attachmentImageList,
                        itineraryEventList,
                        toBringItems,
                        notesList,
                        reminderList,
                        date,
                        category,
                        title
                );
                Log.d("ATTACHMENTS", attachmentImageList.toString());
                Log.d("ADDING REMINDER TO Database SAVING ", reminderList.toString());

                // Adding to Firebase
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    String currentUserId = firebaseUser.getUid();
                    DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference()
                            .child("Event")
                            .push(); // Using push() to create a new unique ID for the event

                    dbEvent.eventID = eventRef.getKey(); // Set the eventID to the generated Firebase key

                    // Convert CompleteEvent to EventDetails (assuming this method exists)
                    CompleteEvent.EventDetails eventDetails = dbEvent.toEventDetails();

                    // Update Firebase with event details
                    eventRef.child("eventDetails").setValue(eventDetails)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Also set the user ID
                                    eventRef.child("users").setValue(currentUserId)
                                            .addOnCompleteListener(userTask -> {
                                                if (userTask.isSuccessful()) {
                                                    Toast.makeText(EventManagement.this, "Event added successfully to Firebase", Toast.LENGTH_SHORT).show();
                                                    goBack(v);
                                                } else {
                                                    Toast.makeText(EventManagement.this, "Failed to add user ID to Firebase: " + userTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(EventManagement.this, "Failed to add event to Firebase: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(EventManagement.this, "No authenticated user found.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        //Puts all data in a CompleteEvent Item and send it to database to be replace the previous event with the same id
        editEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Selected Date
                String date = String.valueOf(dateButton.getText());

                // Get the selected item
                Object selectedItem = EMcategoryDropdown.getSelectedItem();
                // Convert the selected item to a string
                String category = selectedItem.toString();

                // Getting Title Of Entire Event
                String title = String.valueOf(EMtitle.getText());

                // Creating Complete Event Object
                CompleteEvent dbEvent = new CompleteEvent(
                        attachmentImageList,
                        itineraryEventList,
                        toBringItems,
                        notesList,
                        reminderList,
                        date,
                        category,
                        title);
                dbEvent.eventID = editEventID;

                Log.d("EDITING EVENT", "EDITING EVENT: " + dbEvent.toString());

                // Check if the event is a Firebase event
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    String currentUserId = firebaseUser.getUid();
                    DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference()
                            .child("Event")
                            .child(editEventID);

                    // Check if the event exists in Firebase
                    eventRef.child("eventDetails").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Event exists in Firebase, update Firebase
                                CompleteEvent.EventDetails eventDetails = dbEvent.toEventDetails();

                                eventRef.child("eventDetails").setValue(eventDetails)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(EventManagement.this, "Event updated successfully in Firebase", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(EventManagement.this, "Failed to update event in Firebase: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                            // Always update the local database
                            try {
                                dbHandler.updateEvent(EventManagement.this, dbEvent);
                                goBack(v);
                            } catch (SQLiteException e) {
                                Toast.makeText(EventManagement.this, "Error updating local database", Toast.LENGTH_SHORT).show();
                                Log.i("Database Operations", "Error updating tables", e);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(EventManagement.this, "Failed to check event existence in Firebase: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }
        //Date Input
    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }


    private void initDatePicker()
    {
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        EventManagement.this,
                        R.style.CustomDatePickerDialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
                                int month = monthOfYear+ 1;
                                String formattedDate = makeDateString(dayOfMonth, month,year);
                                dateButton.setText(formattedDate);

                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });


    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    private int getMonthNumber(String month)
    {
        switch (month.toUpperCase()) {
            case "JAN":
                return 1;
            case "FEB":
                return 2;
            case "MAR":
                return 3;
            case "APR":
                return 4;
            case "MAY":
                return 5;
            case "JUN":
                return 6;
            case "JUL":
                return 7;
            case "AUG":
                return 8;
            case "SEP":
                return 9;
            case "OCT":
                return 10;
            case "NOV":
                return 11;
            case "DEC":
                return 12;
            default:
                throw new IllegalArgumentException("Invalid month: " + month);
        }
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }


    //Go back Button
    // Method to handle button click to go back
    public void goBack(View view) {
//        Intent intent = new Intent(this, ViewEvents.class);
//        startActivity(intent); // Start ViewEvents activity

        finish(); // Close the current activity and return to the previous one
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PlanHub";
            String description = "Event Reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("PlanHub", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void populateData(CompleteEvent completeEvent) {

        Log.d("EDIT EVENT", "populateData: " + completeEvent.toString());
        EMtitle.setText(completeEvent.eventName);
        int spinnerPosition = spinnerAdapter.getPosition(completeEvent.category);
        EMcategoryDropdown.setSelection(spinnerPosition);
        dateButton.setText(completeEvent.date);

        // Clear existing lists before adding new data
        attachmentImageList.clear();
        if (completeEvent.attachmentImageList != null) {
            attachmentImageList.addAll(completeEvent.attachmentImageList);
        }
        for (ImageAttachment imageAttachment:attachmentImageList) {
            populateImages(imageAttachment);
        }

        itineraryEventList.clear();
        if (completeEvent.itineraryEventList != null) {
            itineraryEventList.addAll(completeEvent.itineraryEventList);
        }

        toBringItems.clear();
        if (completeEvent.toBringItems != null) {
            toBringItems.addAll(completeEvent.toBringItems);
        }

        notesList.clear();
        if (completeEvent.notesList != null) {
            notesList.addAll(completeEvent.notesList);
        }

        reminderList.clear();
        if (completeEvent.reminderList != null) {
            reminderList.addAll(completeEvent.reminderList);
        }

        mAdapter.notifyDataSetChanged();
        itemAdapter.notifyDataSetChanged();
        notesAdapter.notifyDataSetChanged();
        remidnerAdapter.notifyDataSetChanged();
    }

    private void showImageDialog(ImageView imageView, ImageAttachment imageAttachment){
        // Inflate the custom layout for the alert dialog
        View dialogView = LayoutInflater.from(EventManagement.this).inflate(R.layout.em_image_dialog, null);

        // Get the ImageView from the custom layout
        ImageView fullSizeImageView = dialogView.findViewById(R.id.EMfullSizeImageView);

        // Load full-size image into the ImageView using Glide
        Glide.with(EventManagement.this)
                .load(imageAttachment.getURI())
                .into(fullSizeImageView);

        // Create and configure the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(EventManagement.this);
        builder.setView(dialogView)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Delete",(dialog, which) ->{
                    // Remove image from the container
                    attachmentContainer.removeView(imageView);
                    // Remove image from the list
                    attachmentImageList.remove(imageAttachment);
                    dialog.dismiss();
                });


        // Show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void setupUIComponents() {
        // Setup UI components and listeners (e.g., RecyclerViews, buttons)
        setupEventRecyclerView();
        setupBringItemRecyclerView();
        setupNotesRecyclerView();
        setupReminderRecyclerView();
    }

    private void setupEventRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        eventRvView.setLayoutManager(mLayoutManager);
        eventRvView.setItemAnimator(new DefaultItemAnimator());
        eventRvView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(position -> {
            itineraryEventList.remove(position);
            mAdapter.notifyItemRemoved(position);
        });
        ItemTouchHelper eventTouchHelper = new ItemTouchHelper(eventSimpleCallback);
        eventTouchHelper.attachToRecyclerView(eventRvView);
    }

    ItemTouchHelper.SimpleCallback eventSimpleCallback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP |ItemTouchHelper.DOWN |ItemTouchHelper.START |ItemTouchHelper.END
            , ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getLayoutPosition();
            int toPosition = target.getLayoutPosition();

            Collections.swap(itineraryEventList, fromPosition, toPosition);

            mAdapter.notifyItemMoved(fromPosition, toPosition);


            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getLayoutPosition();

            switch (direction){
                case ItemTouchHelper.LEFT:
                    deletedEvent =  itineraryEventList.get(position);
                    itineraryEventList.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    Snackbar.make(eventRvView, "Deleted: " + deletedEvent.eventName , BaseTransientBottomBar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    itineraryEventList.add(position, deletedEvent);
                                    mAdapter.notifyItemInserted(position);
                                }
                            }).show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(EventManagement.this, R.color.swipe_Delete_red))
                    .addActionIcon(R.drawable.baseline_delete_24_white)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void setupBringItemRecyclerView() {
        LinearLayoutManager itemLayoutManager = new LinearLayoutManager(this);
        bringItemRvView.setLayoutManager(itemLayoutManager);
        bringItemRvView.setItemAnimator(new DefaultItemAnimator());
        bringItemRvView.setAdapter(itemAdapter);
        itemAdapter.setOnItemClickListener(position -> {
            toBringItems.remove(position);
            itemAdapter.notifyItemRemoved(position);
        });
        ItemTouchHelper bringItemItemTouchHelper = new ItemTouchHelper(bringItemSimpleCallback);
        bringItemItemTouchHelper.attachToRecyclerView(bringItemRvView);
    }

    ItemTouchHelper.SimpleCallback bringItemSimpleCallback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP |ItemTouchHelper.DOWN |ItemTouchHelper.START |ItemTouchHelper.END
            , ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getLayoutPosition();
            int toPosition = target.getLayoutPosition();

            Collections.swap(toBringItems, fromPosition, toPosition);

            itemAdapter.notifyItemMoved(fromPosition, toPosition);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getLayoutPosition();

            switch (direction){
                case ItemTouchHelper.LEFT:
                    deletedItem =  toBringItems.get(position);
                    toBringItems.remove(position);
                    itemAdapter.notifyItemRemoved(position);
                    Snackbar.make(bringItemRvView, "Deleted: " + deletedItem.itemName , BaseTransientBottomBar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    toBringItems.add(position, deletedItem);
                                    itemAdapter.notifyItemInserted(position);
                                }
                            }).show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(EventManagement.this, R.color.swipe_Delete_red))
                    .addActionIcon(R.drawable.baseline_delete_24_white)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void setupNotesRecyclerView() {
        LinearLayoutManager notesLayoutManager = new LinearLayoutManager(this);
        notesContainer.setLayoutManager(notesLayoutManager);
        notesContainer.setItemAnimator(new DefaultItemAnimator());
        notesContainer.setAdapter(notesAdapter);
        notesAdapter.setOnItemClickListener(position -> {
            notesList.remove(position);
            notesAdapter.notifyItemRemoved(position);
        });
        ItemTouchHelper noteItemTouchHelper = new ItemTouchHelper(noteSimpleCallback);
        noteItemTouchHelper.attachToRecyclerView(notesContainer);
    }

    ItemTouchHelper.SimpleCallback noteSimpleCallback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP |ItemTouchHelper.DOWN |ItemTouchHelper.START |ItemTouchHelper.END
            , ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getLayoutPosition();
            int toPosition = target.getLayoutPosition();

            Collections.swap(notesList, fromPosition, toPosition);

            notesAdapter.notifyItemMoved(fromPosition, toPosition);


            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getLayoutPosition();

            switch (direction){
                case ItemTouchHelper.LEFT:
                    deletedNote =  notesList.get(position);
                    notesList.remove(position);
                    notesAdapter.notifyItemRemoved(position);
                    Snackbar.make(notesContainer, "Deleted: " + deletedNote.toString() , BaseTransientBottomBar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    notesList.add(position, deletedNote);
                                    notesAdapter.notifyItemInserted(position);
                                }
                            }).show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(EventManagement.this, R.color.swipe_Delete_red))
                    .addActionIcon(R.drawable.baseline_delete_24_white)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void setupReminderRecyclerView() {
        LinearLayoutManager reminderLayoutManager = new LinearLayoutManager(this);
        reminderContainer.setLayoutManager(reminderLayoutManager);
        reminderContainer.setItemAnimator(new DefaultItemAnimator());
        reminderContainer.setAdapter(remidnerAdapter);
        remidnerAdapter.setOnItemClickListener(position -> {
            reminderList.remove(position);
            remidnerAdapter.notifyItemRemoved(position);
        });
        ItemTouchHelper reminderItemTouchHelper = new ItemTouchHelper(reminderSimpleCallback);
        reminderItemTouchHelper.attachToRecyclerView(reminderContainer);

    }

    ItemTouchHelper.SimpleCallback reminderSimpleCallback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP |ItemTouchHelper.DOWN |ItemTouchHelper.START |ItemTouchHelper.END
            , ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getLayoutPosition();
            int toPosition = target.getLayoutPosition();

            Collections.swap(reminderList, fromPosition, toPosition);

            remidnerAdapter.notifyItemMoved(fromPosition, toPosition);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getLayoutPosition();

            switch (direction){
                case ItemTouchHelper.LEFT:
                    deletedReminder =  reminderList.get(position);
                    reminderList.remove(position);
                    remidnerAdapter.notifyItemRemoved(position);
                    Snackbar.make(reminderContainer, "Deleted: " + deletedReminder.reminderTitle + "(" + deletedReminder.reminderTime +")" , BaseTransientBottomBar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    reminderList.add(position, deletedReminder);
                                    remidnerAdapter.notifyItemInserted(position);
                                }
                            }).show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(EventManagement.this, R.color.swipe_Delete_red))
                    .addActionIcon(R.drawable.baseline_delete_24_white)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void populateImages(ImageAttachment image) {

//        ImageAttachment imageAttachment = new ImageAttachment();
        String fileUri = image.getURI();

        ImageView imageView = new ImageView(EventManagement.this);

        //Displaying the drawble not the image URI
        // Use Glide to load the image into the ImageView
        Glide.with(EventManagement.this)
                .load(fileUri)
                .apply(new RequestOptions().override(LinearLayout.LayoutParams.WRAP_CONTENT, 100)) // Set height to 100dp
                .into(imageView);

        // Set layout parameters for ImageView
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                200 // Set height to 100dp
        );
        imageView.setLayoutParams(layoutParams);

        imageView.setOnClickListener(v -> {
            // Inflate the custom layout for the alert dialog
            View dialogView = LayoutInflater.from(EventManagement.this).inflate(R.layout.em_image_dialog, null);

            // Get the ImageView from the custom layout
            ImageView fullSizeImageView = dialogView.findViewById(R.id.EMfullSizeImageView);

            // Load full-size image into the ImageView using Glide
            Glide.with(EventManagement.this)
                    .load(fileUri)
                    .into(fullSizeImageView);

            // Create and configure the AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(EventManagement.this);
            builder.setView(dialogView)
                    .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("Delete",(dialog, which) ->{
                        // Remove image from the container
                        attachmentContainer.removeView(imageView);
                        // Remove image from the list
                        attachmentImageList.remove(image);
                        dialog.dismiss();
                    });


            // Show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });

        attachmentContainer.addView(imageView);
    }

    private void initViewsAndAdapters() {
        //Initialize final save and edit buttons
        finalSaveButton = findViewById(R.id.EMsaveButton);
        editEventButton = findViewById(R.id.EMeditButton);
        management = findViewById(R.id.EMitineraryAddEventNameBtn);
        bring = findViewById(R.id.EMitineraryAddBringItemBtn);
        attachment = findViewById(R.id.EMattchmentBtn);
        reminder = findViewById(R.id.EMreminderAddBtn);
        notes = findViewById(R.id.EMnotesBtn);
        backbtn = findViewById(R.id.backButton);
        //Image Container
        attachmentContainer = findViewById(R.id.EMattchmentContainer);
        //Recycler Views
        bringItemRvView = findViewById(R.id.EMrvViewBringList);
        eventRvView = findViewById(R.id.EMrvViewItinerary);
        notesContainer = findViewById(R.id.EMnotesItem);
        reminderContainer = findViewById(R.id.EMreminderItems);
        // Initialize data inputs
        EMtitle = findViewById(R.id.EMtitle);
        dateButton = findViewById(R.id.EMdatePicker);
        EMcategoryDropdown = findViewById(R.id.EMcategoryDropdown);
        attachmentImageList = new ArrayList<>();
        itineraryEventList = new ArrayList<>();
        toBringItems = new ArrayList<>();
        notesList = new ArrayList<>();
        reminderList = new ArrayList<>();
        // Initialize adapters
        spinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.event_categories,
                R.layout.em_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(R.layout.em_spinner_dropdown_item);
        EMcategoryDropdown.setAdapter(spinnerAdapter);

        mAdapter = new EventAdapter(itineraryEventList);
        itemAdapter = new BringItemAdapter(toBringItems);
        notesAdapter = new NotesAdapter(notesList);
        remidnerAdapter = new ReminderAdapter(reminderList);
    }
}