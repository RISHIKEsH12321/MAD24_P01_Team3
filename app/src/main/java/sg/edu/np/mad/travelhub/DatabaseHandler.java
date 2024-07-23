package sg.edu.np.mad.travelhub;

import static android.icu.text.ListFormatter.Type.OR;
import static android.media.tv.TvContract.AUTHORITY;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "travelhub.db";
    private static final String EVENTS = "events";
    //Tables
    private static final String EVENTS_TABLE = "events";
    private static final String ATTACHMENT_IMAGES_TABLE = "attachment_images";
    private static final String ITINERARY_EVENTS_TABLE = "itinerary_events";
    private static final String ITEMS_TABLE = "items";
    private static final String REMINDER_TABLE = "reminder";
    //Columns
    private static final String EVENT_ID = "id";
    private static final String EVENT_NAME = "name";
    private static final String DATE = "date";
    private static final String CATEGORY = "category";
    private static final String IMAGE_URI = "image_uri";
    private static final String IMAGE_ID = "image_id";
    private static final String ITINERARY_EVENT = "itinerary_event";
    private static final String ITINERARY_EVENT_ID = "itinerary_event_id";
    private static final String ITEM = "item";
    private static final String ITEM_ID = "item_id";
    private static final String TICKED = "ticked";
    private static final String NOTE = "note";
    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";
    private static final String IT_NOTE = "it_note";
    private static final String REMINDER_ID = "reminder_id";
    private static final String REMINDER_TITLE = "reminder_title";
    private static final String REMINDER_TIME = "reminder_time";
    private Context context;
    private SQLiteDatabase db;
    private DatabaseReference mDatabase;


    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }



    @Override
    public void onCreate(SQLiteDatabase db){

        Log.i("Database Operations", "Creating a Table.");
        try {

            // Creating the EVENTS table
            String CREATE_EVENTS_TABLE = "CREATE TABLE " + EVENTS_TABLE +
                    "(" + EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + EVENT_NAME + " TEXT,"
                    + DATE + " DATE,"
                    + CATEGORY + " TEXT,"
                    + NOTE + " TEXT"
                    + ")";

//            // Creating the ATTACHMENT_IMAGES table
            String CREATE_ATTACHMENT_IMAGES_TABLE =
                    "CREATE TABLE " + ATTACHMENT_IMAGES_TABLE +
                    "(" + EVENT_ID + " TEXT,"
                    + IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + IMAGE_URI + " TEXT,"
                    + "FOREIGN KEY(" + EVENT_ID + ") REFERENCES " + EVENTS_TABLE + "(" + EVENT_ID + ")"
                    + ")";

            // Creating the ITINERARY_EVENTS table
            String CREATE_ITINERARY_EVENTS_TABLE = "CREATE TABLE " + ITINERARY_EVENTS_TABLE +
                    "(" + EVENT_ID  + " TEXT ,"
                    + ITINERARY_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ITINERARY_EVENT + " TEXT,"
                    + START_TIME + " TEXT,"
                    + END_TIME + " TEXT,"
                    + IT_NOTE + " TEXT,"
                    + "FOREIGN KEY(" + EVENT_ID + ") REFERENCES " + EVENTS_TABLE + "(" + EVENT_ID + ")"
                    + ")";
            //Create the Items Table
            String CREATE_ITEMS_TABLE = "CREATE TABLE " + ITEMS_TABLE +
                    "(" + EVENT_ID + " TEXT,"
                    + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ITEM + " TEXT,"
                    + TICKED + " BOOLEAN,"
                    + "FOREIGN KEY(" + EVENT_ID + ") REFERENCES " + EVENTS_TABLE + "(" + EVENT_ID + ")"
                    + ")";

            //Create The Reminder Table
            String CREATE_REMINDER_TABLE = "CREATE TABLE " + REMINDER_TABLE +
                    "(" + EVENT_ID + " TEXT,"
                    + REMINDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + REMINDER_TITLE + " TEXT,"
                    + REMINDER_TIME + " DATETIME,"
                    + DATE + " DATETIME,"
                    + "FOREIGN KEY(" + EVENT_ID + ") REFERENCES " + EVENTS_TABLE + "(" + EVENT_ID + ")"
                    + ")";


            // Execute the SQL statements
            db.execSQL(CREATE_EVENTS_TABLE);
            db.execSQL(CREATE_ATTACHMENT_IMAGES_TABLE);
            db.execSQL(CREATE_ITINERARY_EVENTS_TABLE);
            db.execSQL(CREATE_ITEMS_TABLE);
            db.execSQL(CREATE_REMINDER_TABLE);

            Log.i("Database Operations", "Tables created successfully.");
//          db.close();
        } catch (SQLiteException e) {
            Log.i("Database Operations", "Error creating tables", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + EVENTS);
        onCreate(db);
    }

    //  Add a user record
    public void addEvent(Context context, CompleteEvent completeEvent) throws ParseException {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues eventBaseValues = new ContentValues();
        StringBuilder notes = new StringBuilder();
        for (String x:completeEvent.notesList) {
            notes.append(x).append("!NOTES_APPENDING_BANANA!");
        }

        //Creating Event
        eventBaseValues.put(EVENT_NAME,completeEvent.eventName);
        eventBaseValues.put(DATE,completeEvent.date);
        eventBaseValues.put(CATEGORY,completeEvent.category);
        eventBaseValues.put(NOTE, notes.toString());

//        db.insert(EVENTS, null, eventBaseValues);
        long eventId = db.insert(EVENTS, null, eventBaseValues);

        if (eventId != -1) {

            if (completeEvent.itineraryEventList != null && !completeEvent.itineraryEventList.isEmpty()){
                for (ItineraryEvent itineraryEvent : completeEvent.itineraryEventList) {
                    ContentValues itineraryValues = new ContentValues();
                    itineraryValues.put(EVENT_ID, eventId);
                    itineraryValues.put(ITINERARY_EVENT, itineraryEvent.eventName);
                    itineraryValues.put(START_TIME, formatTime(itineraryEvent.startHour, itineraryEvent.startMin));
                    itineraryValues.put(END_TIME, formatTime(itineraryEvent.endHour, itineraryEvent.endMin));
                    itineraryValues.put(IT_NOTE, itineraryEvent.eventNotes);
                    db.insert(ITINERARY_EVENTS_TABLE, null, itineraryValues);
                }
            }

            if (completeEvent.attachmentImageList != null && !completeEvent.attachmentImageList.isEmpty()) {
                for (ImageAttachment imageUri : completeEvent.attachmentImageList) {
                    ContentValues values = new ContentValues();
                    values.put(EVENT_ID, eventId);
                    values.put(IMAGE_URI, imageUri.getURI().toString());
                    Log.i("Database Operations", "Creating image");

                    db.insert(ATTACHMENT_IMAGES_TABLE, null, values);
                }
            }

            if (completeEvent.toBringItems != null && !completeEvent.toBringItems.isEmpty()) {

                for (ToBringItem s : completeEvent.toBringItems) {
                    ContentValues itemvalues = new ContentValues();
                    itemvalues.put(EVENT_ID, eventId);
                    itemvalues.put(TICKED, false);
                    itemvalues.put(ITEM, s.itemID);
                    db.insert(ITEMS_TABLE, null, itemvalues);

                }
            }

            Log.d("ADDING REMINDER TO Database", "Added Reminder Condition: " + (completeEvent.reminderList != null && !completeEvent.toBringItems.isEmpty()));

            cancelAllReminders(context);
            if (completeEvent.reminderList != null && !completeEvent.reminderList.isEmpty()) {
                for (Reminder s : completeEvent.reminderList) {

                    // Create a datetime string from date and time
                    String dateTimeString = completeEvent.date + " " + s.reminderTime;
                    //SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm", Locale.getDefault());
                    SimpleDateFormat formatter = new SimpleDateFormat("MMM d yyyy HH:mm");
                    Date date = formatter.parse(dateTimeString);

                    ContentValues reminderValues = new ContentValues();
                    reminderValues.put(EVENT_ID, eventId);
                    reminderValues.put(REMINDER_TITLE, s.reminderTitle);
                    reminderValues.put(REMINDER_TIME, s.reminderTime);
                    reminderValues.put(DATE, date.toString());



//                    scheduleNotification(context, s, date);
                    Log.d("NOTIFICATIONS", "dateTimeString: " + dateTimeString);
//                    long result = insertReminder(reminderValues);
//
//                    if (result != -1) {
//                        Log.d("ADDING REMINDER TO Database", "Added Reminder: " + s.toString());
//                    } else {
//                        Log.d("ADDING REMINDER TO Database", "Failed to add reminder: " + s.toString());
//                    }
                    // Insert reminder into database
                    db.insert(REMINDER_TABLE, null, reminderValues);
                    Log.d("ADDING REMINDER TO Database", "Added Reminder: " + s.toString());
                }
            }
            scheduleNotification(context);
        }


//      db.close();
    }

    public Uri getImageUri(){
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + ATTACHMENT_IMAGES_TABLE;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            String uriString = cursor.getString((int)cursor.getColumnIndex(IMAGE_URI));
            Log.d("Image Insert", "Image URI: " + uriString);

            return Uri.parse(uriString);
        }
        cursor.close();
        return null;
    }

    public void dropTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseHandler.EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseHandler.ATTACHMENT_IMAGES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseHandler.ITINERARY_EVENTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseHandler.ITEMS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseHandler.REMINDER_TABLE);
        onCreate(db);
//        db.close();
    }

    public ArrayList<CompleteEvent> getEventONDate(String date){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<CompleteEvent> evtList = new ArrayList<>();

        String query = "SELECT * FROM " + EVENTS_TABLE + " WHERE " + DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{date}); // Add the date as a parameter

        if (cursor.moveToFirst()) {
            do {
                // Create a CompleteEvent object from cursor data
                CompleteEvent completeEvent = new CompleteEvent();
                String eventID = cursor.getString((int)cursor.getColumnIndex(EVENT_ID));
                completeEvent.eventName = cursor.getString((int)cursor.getColumnIndex(EVENT_NAME));
                completeEvent.eventID = eventID;
                completeEvent.date = cursor.getString((int)cursor.getColumnIndex(DATE));
                completeEvent.category = cursor.getString((int)cursor.getColumnIndex(CATEGORY));


                // Extract notes, reminders, and to-bring items by parsing the stored strings
                String notesString = cursor.getString((int)cursor.getColumnIndex(NOTE));
                if (notesString != null && !notesString.isEmpty()) {
                    completeEvent.notesList = new ArrayList<>(Arrays.asList(notesString.split("!NOTES_APPENDING_BANANA!")));
                }
//                String remindersString = cursor.getString((int)cursor.getColumnIndex(REMINDER));
//                if (remindersString != null && !remindersString.isEmpty()) {
//                    completeEvent.reminderList = new ArrayList<>(Arrays.asList(remindersString.split("!REMINDER_APPENDING_BANANA!")));
//                }




                String queryItems = "SELECT * FROM " + ITEMS_TABLE + " WHERE " + EVENT_ID + " = ?";

                Cursor ItemsCursor = db.rawQuery(queryItems,new String[]{eventID});
                ArrayList<ToBringItem> itemsList = new ArrayList<>();
                if (ItemsCursor.moveToFirst()) {
                    do {
                        ToBringItem item = new ToBringItem();
                        int tickedInt = ItemsCursor.getInt((int) ItemsCursor.getColumnIndex(TICKED));
                        item.ticked = (tickedInt == 1);
                        item.eventID = ItemsCursor.getString((int) ItemsCursor.getColumnIndex(EVENT_ID));
                        item.itemID = ItemsCursor.getString((int) ItemsCursor.getColumnIndex(ITEM_ID));
                        item.itemName = ItemsCursor.getString((int) ItemsCursor.getColumnIndex(ITEM));
                        itemsList.add(item);
                    } while (ItemsCursor.moveToNext());
                }
                completeEvent.toBringItems = itemsList;
                ItemsCursor.close();


                String queryItEvents = "SELECT " +
                        "* FROM " + ITINERARY_EVENTS_TABLE +
                        " WHERE " + EVENT_ID + " = ?";  // Use prepared statement

                ArrayList<ItineraryEvent> itineraryEventsList = new ArrayList<>();
                Cursor eventCursor = db.rawQuery(queryItEvents, new String[]{eventID});
                if (eventCursor.moveToFirst()) {
                    do {
                        ItineraryEvent itineraryEvent = new ItineraryEvent();
                        itineraryEvent.eventName = eventCursor.getString((int) eventCursor.getColumnIndexOrThrow(ITINERARY_EVENT));
                        itineraryEvent.eventID = eventCursor.getString((int) eventCursor.getColumnIndexOrThrow(EVENT_ID));
                        itineraryEvent.eventNotes = eventCursor.getString((int) eventCursor.getColumnIndexOrThrow(IT_NOTE));

                        // Parsing start time
                        String startTimeString = eventCursor.getString((int)eventCursor.getColumnIndexOrThrow(START_TIME));
                        String[] startTimeParts = startTimeString.split(":");
                        itineraryEvent.startHour = startTimeParts[0];
                        itineraryEvent.startMin = startTimeParts[1];

                        // Parsing end time
                        String endTimeString = eventCursor.getString((int)eventCursor.getColumnIndexOrThrow(END_TIME));
                        String[] endTimeParts = endTimeString.split(":");
                        itineraryEvent.endHour = endTimeParts[0];
                        itineraryEvent.endMin = endTimeParts[1];

                        itineraryEventsList.add(itineraryEvent);
                    } while (eventCursor.moveToNext());
                }
                eventCursor.close();

                completeEvent.itineraryEventList = itineraryEventsList;



                String queryItImages = "SELECT * FROM " + ATTACHMENT_IMAGES_TABLE + " WHERE " + EVENT_ID + " = ?";

                ArrayList<ImageAttachment> imgUriEventsList = new ArrayList<>();
                Cursor ImageCursor = db.rawQuery(queryItImages, new String[]{eventID});

                if (ImageCursor.moveToFirst()) {
                    do {

                        ImageAttachment imageAttachment = new ImageAttachment();
                        // Assuming URI is the only column retrieved
                        String imageUriString = ImageCursor.getString((int) ImageCursor.getColumnIndexOrThrow(IMAGE_URI));
                        imageAttachment.setURI(String.valueOf(Uri.parse(imageUriString)));
                        // Assuming EventId is the correct field name
                        imageAttachment.setEventId(ImageCursor.getString((int) ImageCursor.getColumnIndexOrThrow(EVENT_ID)));
                        imageAttachment.setImageId(ImageCursor.getString((int) ImageCursor.getColumnIndexOrThrow(IMAGE_ID)));
                        Log.d("IMAGEATTACHMENTINIMAGES", "ID: " + imageAttachment.getImageId());
                        imgUriEventsList.add(imageAttachment);
                    } while (ImageCursor.moveToNext());
                }

                ImageCursor.close();  // Close the cursor after use
                completeEvent.attachmentImageList = imgUriEventsList;

                String queryReminders = "SELECT * FROM " + REMINDER_TABLE + " WHERE " + EVENT_ID + " = ?";

                ArrayList<Reminder> reminderList = new ArrayList<>();
                Cursor ReminderCursor = db.rawQuery(queryReminders, new String[]{eventID});

                if (ReminderCursor.moveToFirst()) {
                    do {

                        Reminder reminder = new Reminder();
                        reminder.eventID = ReminderCursor.getString((int) ReminderCursor.getColumnIndexOrThrow(EVENT_ID));
                        reminder.reminderTitle = ReminderCursor.getString((int) ReminderCursor.getColumnIndexOrThrow(REMINDER_TITLE));
                        reminder.reminderId = ReminderCursor.getString((int) ReminderCursor.getColumnIndexOrThrow(REMINDER_ID));
                        reminder.reminderTime = ReminderCursor.getString((int) ReminderCursor.getColumnIndexOrThrow(REMINDER_TIME));


//                        Log.d("IMAGEATTACHMENTINIMAGES", "ID: " + imageAttachment.ImageId);
                        reminderList.add(reminder);
                    } while (ReminderCursor.moveToNext());
                }

                ReminderCursor.close();  // Close the cursor after use
                completeEvent.reminderList = reminderList;


                evtList.add(completeEvent);
                Log.d("VIEW EVENTS DATA", "Data LOG DATABASE:" + completeEvent.toString());

            } while (cursor.moveToNext());

        }else{
            Log.d("getEventONDate", "No events found for the given date.");
        }


        cursor.close();
        return evtList;
    }


    public void deleteEventById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        cancelAllReminders(context);
        try {
            // Begin a transaction
            db.beginTransaction();

            // Delete from ITEMS_TABLE
            db.delete(ITEMS_TABLE, EVENT_ID + "=?", new String[]{String.valueOf(id)});

            // Delete from ITINERARY_EVENTS_TABLE
            db.delete(ITINERARY_EVENTS_TABLE, EVENT_ID + "=?", new String[]{String.valueOf(id)});

            // Delete from ATTACHMENT_IMAGES_TABLE
            db.delete(ATTACHMENT_IMAGES_TABLE, EVENT_ID + "=?", new String[]{String.valueOf(id)});

            // Delete from EVENTS_TABLE
            db.delete(EVENTS_TABLE, EVENT_ID + "=?", new String[]{String.valueOf(id)});

            // Delete from REMINDER_TABLE
            db.delete(REMINDER_TABLE, EVENT_ID + "=?", new String[]{String.valueOf(id)});

            // Set transaction as successful
            db.setTransactionSuccessful();
            scheduleNotification(context);
        } catch (SQLiteException e) {
            Log.e("Database Operations", "Error deleting event", e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } finally {
            // End the transaction
            db.endTransaction();
        }

        Log.i("Database Operations", "Event and related data deleted successfully.");
    }


    public void updateEvent(Context context, CompleteEvent completeEvent) throws ParseException {
        SQLiteDatabase db = getWritableDatabase();
        cancelAllReminders(context);
        // Begin transaction
        db.beginTransaction();
        try {
            // Delete existing data in related tables
            deleteRelatedData(db, completeEvent.eventID);

            // Update EVENTS table
            ContentValues eventBaseValues = new ContentValues();
            StringBuilder notes = new StringBuilder();
            for (String note : completeEvent.notesList) {
                notes.append(note).append("!NOTES_APPENDING_BANANA!");
            }
            eventBaseValues.put(EVENT_NAME, completeEvent.eventName);
            eventBaseValues.put(DATE, completeEvent.date);
            eventBaseValues.put(CATEGORY, completeEvent.category);
            eventBaseValues.put(NOTE, notes.toString());

            db.update(EVENTS_TABLE, eventBaseValues, EVENT_ID + " = ?", new String[]{String.valueOf(completeEvent.eventID)});

            // Insert new data into related tables
            insertRelatedData(db, completeEvent);

            // Set transaction as successful
            db.setTransactionSuccessful();
        } finally {
            // End transaction
            db.endTransaction();
        }
        scheduleNotification(context);
    }

    private void deleteRelatedData(SQLiteDatabase db, String eventId) {
        // Delete data from related tables except EVENTS table
        db.delete(ATTACHMENT_IMAGES_TABLE, EVENT_ID + " = ?", new String[]{String.valueOf(eventId)});
        db.delete(ITINERARY_EVENTS_TABLE, EVENT_ID + " = ?", new String[]{String.valueOf(eventId)});
        db.delete(ITEMS_TABLE, EVENT_ID + " = ?", new String[]{String.valueOf(eventId)});
        db.delete(REMINDER_TABLE, EVENT_ID + " = ?", new String[]{String.valueOf(eventId)});
    }

    private void insertRelatedData(SQLiteDatabase db, CompleteEvent completeEvent) throws ParseException {
        // Insert data into related tables
        String eventId = completeEvent.eventID;

        if (completeEvent.attachmentImageList != null && !completeEvent.attachmentImageList.isEmpty()) {
            for (ImageAttachment imageUri : completeEvent.attachmentImageList) {
                ContentValues values = new ContentValues();
                values.put(EVENT_ID, eventId);
                values.put(IMAGE_URI, imageUri.getURI().toString());
                db.insert(ATTACHMENT_IMAGES_TABLE, null, values);
            }
        }

        if (completeEvent.itineraryEventList != null && !completeEvent.itineraryEventList.isEmpty()) {
            for (ItineraryEvent itineraryEvent : completeEvent.itineraryEventList) {
                ContentValues itineraryValues = new ContentValues();
                itineraryValues.put(EVENT_ID, eventId);
                itineraryValues.put(ITINERARY_EVENT, itineraryEvent.eventName);
                itineraryValues.put(START_TIME, formatTime(itineraryEvent.startHour, itineraryEvent.startMin));
                itineraryValues.put(END_TIME, formatTime(itineraryEvent.endHour, itineraryEvent.endMin));
                itineraryValues.put(IT_NOTE, itineraryEvent.eventNotes);
                db.insert(ITINERARY_EVENTS_TABLE, null, itineraryValues);
            }
        }

        if (completeEvent.toBringItems != null && !completeEvent.toBringItems.isEmpty()) {
            for (ToBringItem item : completeEvent.toBringItems) {
                ContentValues itemValues = new ContentValues();
                itemValues.put(EVENT_ID, eventId);
                itemValues.put(ITEM, item.itemName);
                itemValues.put(TICKED, false); // Example: set ticked status
                db.insert(ITEMS_TABLE, null, itemValues);
            }
        }

        if (completeEvent.reminderList != null && !completeEvent.reminderList.isEmpty()) {
            for (Reminder reminder : completeEvent.reminderList) {
                // Create a datetime string from date and time
                String dateTimeString = completeEvent.date + " " + reminder.reminderTime;
                //SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm", Locale.getDefault());
                SimpleDateFormat formatter = new SimpleDateFormat("MMM d yyyy HH:mm");
                Date date = formatter.parse(dateTimeString);

                ContentValues reminderValues = new ContentValues();
                reminderValues.put(EVENT_ID, eventId);
                reminderValues.put(REMINDER_TITLE, reminder.reminderTitle);
                reminderValues.put(REMINDER_TIME, reminder.reminderTime);
                reminderValues.put(DATE, date.toString());
                db.insert(REMINDER_TABLE, null, reminderValues);
            }
        }
    }



    public boolean checkItem(String id) {
        SQLiteDatabase db = getWritableDatabase();
        boolean newTickedValue = false;

        Log.d("DATABASE ITEM UPDATE", "checkItem id: " +id);
        try {
            // Begin a transaction
            db.beginTransaction();

            // Prepare the update query to toggle the ticked value
            String sql = "UPDATE " + ITEMS_TABLE + " SET " + TICKED + " = CASE WHEN " + TICKED + " = 0 THEN 1 ELSE 0 END WHERE " + ITEM_ID + " = ?";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1, id);




            // Execute the update
            statement.executeUpdateDelete();

            // Mark the transaction as successful
            db.setTransactionSuccessful();

            // Query the updated value
            String query = "SELECT " + TICKED + " FROM " + ITEMS_TABLE + " WHERE " + ITEM_ID + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{id});
            if (cursor.moveToFirst()) {
                int tickedIndex = cursor.getColumnIndex(TICKED);
                if (tickedIndex != -1) {
                    newTickedValue = cursor.getInt(tickedIndex) != 0;
                    Log.d("DATABASE ITEM UPDATE", "checkItem ticked: " +newTickedValue);

                }
            }
            cursor.close();
        } catch (SQLiteException e) {
            Log.e("Database Operations", "Error updating item", e);
        } finally {
            // End the transaction
            db.endTransaction();
        }

        return newTickedValue;
    }



    private String formatTime(String hour, String minute) {
        return String.format(hour + ":" + minute);
    }
    private String[] unFormatTime(String time) {
        return time.split(":");
    }

    private void scheduleNotification(Context context) throws ParseException {
        Log.d("NOTIFICATION", "scheduleNotification called");

        ArrayList<Reminder> reminderList  = getReminders();;
        Log.d("NOTIFICATION", "reminderList: " + reminderList);
        Log.d("NOTIFICATION", "getReminders(): " + getReminders());

        for (Reminder reminder:reminderList) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            Date date = dateFormat.parse(reminder.reminderTime);

            Date currentDate = new Date();
            if (date.before(currentDate)) {
                continue; // Skip the rest of the loop if the date is before the current date and time
            }
            String eventName = getEventNameById(reminder.eventID);
            Log.d("NOTIFICATION", "eventName: "+ eventName);
            Log.d("NOTIFICATION", "scheduleNotification: Proceeding");
            Intent intent = new Intent(context, ReminderBroadcast.class);
            String title = reminder.reminderTitle;
            intent.putExtra("titleExtra", title);
            intent.putExtra("titleMain", eventName);
            intent.putExtra("reminderId", reminder.reminderId);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    Integer.parseInt(reminder.reminderId),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );


            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    try {
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                date.getTime(),
                                pendingIntent
                        );
                        Log.d("NOTIFICATION", "Date: " + date.toString());
                        Log.d("NOTIFICATION", "Milliseconds: " + date.getTime());
                    } catch (SecurityException e) {
                        // Handle the exception if the permission is not granted
                        Log.e("NOTIFICATION", e.toString());
                        // Optionally, inform the user about the issue
                    }
                }
            }

//            Toast.makeText(context, title + " " + date, Toast.LENGTH_LONG).show();

        }

    }

    private void cancelAllReminders(Context context) {
        ArrayList<Reminder> reminderList = getReminders();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (Reminder reminder : reminderList) {
            Intent intent = new Intent(context, ReminderBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    Integer.parseInt(reminder.reminderId),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );

            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Log.d("NOTIFICATION", "Cancelled reminder with ID: " + reminder.reminderId);
        }
    }

    private String getEventNameById(String eventID){
        String eventName;
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT " + EVENT_NAME + " FROM " + EVENTS_TABLE + " WHERE " + EVENT_ID + " = ?";

//        String queryReminders = "SELECT * FROM " + REMINDER_TABLE + " WHERE " + EVENT_ID + " = ?";

        Cursor eventCursor = db.rawQuery(query, new String[]{eventID});


        if (eventCursor.moveToFirst()) {
            do {
                eventName = eventCursor.getString(eventCursor.getColumnIndexOrThrow(EVENT_NAME));
                return eventName;
            } while (eventCursor.moveToNext());
        }

        eventCursor.close();  // Close the cursor after use
        return "Error Finding Event Name";
    }

    private ArrayList<Reminder> getReminders(){

        SQLiteDatabase db = getReadableDatabase();

        String queryReminders = "SELECT * FROM " + REMINDER_TABLE;

        ArrayList<Reminder> reminderList = new ArrayList<>();
        Cursor notificationCursor = db.rawQuery(queryReminders,null);

        try {


            if (notificationCursor != null && notificationCursor.moveToFirst()) {
                do {
                    String eventID = notificationCursor.getString(notificationCursor.getColumnIndexOrThrow(EVENT_ID));
                    String reminderTitle = notificationCursor.getString(notificationCursor.getColumnIndexOrThrow(REMINDER_TITLE));
                    String reminderDateTime = notificationCursor.getString(notificationCursor.getColumnIndexOrThrow(DATE));
                    String reminderId = notificationCursor.getString(notificationCursor.getColumnIndexOrThrow(REMINDER_ID));

                    Reminder reminder = new Reminder(reminderTitle, eventID, reminderDateTime,reminderId);
                    Log.d("NOTIFICATION", "getReminders in function: " + reminder.toString());
                    reminder.reminderId = reminderId;

                    reminderList.add(reminder);
                } while (notificationCursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (notificationCursor != null) {
                notificationCursor.close();
            }
        }

        return reminderList;
    }

    public void getEventsFromFirebase(final FirebaseCallback callback) {
        mDatabase.child("Event").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<CompleteEvent> events = new ArrayList<>();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    CompleteEvent event = eventSnapshot.child("eventDetails").getValue(CompleteEvent.class);
                    if (event != null) {
                        event.eventID = eventSnapshot.getKey();
                        events.add(event);
                    }
                }
                callback.onCallback(events);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    public interface FirebaseCallback {
        void onCallback(ArrayList<CompleteEvent> events);
    }
}