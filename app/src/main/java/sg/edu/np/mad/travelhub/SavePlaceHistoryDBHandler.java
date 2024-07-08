package sg.edu.np.mad.travelhub;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.gson.Gson;

public class SavePlaceHistoryDBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PlaceHistory.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "PlaceHistory";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_PLACE_DETAILS = "placeDetails";
    private SQLiteDatabase db;

    public SavePlaceHistoryDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_ADDRESS + " TEXT,"
                + COLUMN_PLACE_DETAILS + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);

        Log.d("Table Creation", "true");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert PlaceDetails into the database
    public void insertPlaceDetails(PlaceDetails place) {
        // Check if the place already exists in the database
        if (!isPlaceExists(place)) {
            place.setHistory(true);
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, place.getName());
            values.put(COLUMN_ADDRESS, cleanAddress(place.getAddress()));
            values.put(COLUMN_PLACE_DETAILS, new Gson().toJson(place));
            db.insert(TABLE_NAME, null, values);
            db.close();
        }
    }

    // Retrieve PlaceDetails by ID from the database
    public PlaceDetails getPlaceDetails(int id) {
        db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_PLACE_DETAILS},
                COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        PlaceDetails place = new Gson().fromJson(cursor.getString(0), PlaceDetails.class);
        cursor.close();
        return place;
    }

    // Retrieve all PlaceDetails from the database
    public List<PlaceDetails> getAllPlaceDetails() {
        List<PlaceDetails> placeList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            // Check if the cursor is valid and contains the expected column
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex(COLUMN_PLACE_DETAILS);
                if (columnIndex != -1) {
                    // looping through all rows and adding to list
                    while (cursor.moveToNext()) {
                        String placeDetailsJson = cursor.getString(columnIndex);
                        PlaceDetails place = new Gson().fromJson(placeDetailsJson, PlaceDetails.class);
                        placeList.add(place);
                    }
                }
            }
        } finally {
            // Close cursor and database connection
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return placeList;
    }

    public List<PlaceDetails> getPlacesByQuery(String query) {
        List<PlaceDetails> placeList = new ArrayList<>();

        // SQL query to retrieve places where the name matches the query
        String selectQuery = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_NAME + " LIKE '%" + query + "%'";

        db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            // Check if the cursor is valid and contains the expected column
            if (cursor != null) {
                int placeDetailsIndex = cursor.getColumnIndex(COLUMN_PLACE_DETAILS);
                if (placeDetailsIndex != -1) {
                    // Loop through all rows and add to list
                    while (cursor.moveToNext()) {
                        String placeDetailsJson = cursor.getString(placeDetailsIndex);
                        PlaceDetails place = new Gson().fromJson(placeDetailsJson, PlaceDetails.class);
                        placeList.add(place);
                    }
                }
            }
        } finally {
            // Close cursor and database connection
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return placeList;
    }

    // Retrieve all primary and secondary texts from the database
    public List<Map<String, String>> getAllPrimaryAndSecondaryTexts() {
        List<Map<String, String>> textsList = new ArrayList<>();
        String selectQuery = "SELECT " + COLUMN_NAME + ", " + COLUMN_ADDRESS + " FROM " + TABLE_NAME;

        db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Check if the cursor is valid and contains expected columns
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
            int addressIndex = cursor.getColumnIndex(COLUMN_ADDRESS);

            // Check if the columns exist in the cursor
            if (nameIndex != -1 && addressIndex != -1) {
                // looping through all rows and adding to list
                while (cursor.moveToNext()) {
                    String name = cursor.getString(nameIndex);
                    String address = cursor.getString(addressIndex);

                    Map<String, String> textMap = new HashMap<>();
                    textMap.put("primary", name);
                    textMap.put("secondary", address);

                    textsList.add(textMap);
                }
            }

            // Close cursor
            cursor.close();
        }

        // Close database connection
        db.close();

        return textsList;
    }

    // Retrieve primary and secondary texts (name and address) matching the query
    public List<Map<String, String>> getPrimaryAndSecondaryTextsByQuery(String query) {
        List<Map<String, String>> textsList = new ArrayList<>();

        // SQL query to retrieve name and address columns where name or address matches the query
        String selectQuery = "SELECT " + COLUMN_NAME + ", " + COLUMN_ADDRESS +
                " FROM " + TABLE_NAME +
                " WHERE " + COLUMN_NAME + " LIKE '%" + query + "%'";

        db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            // Check if the cursor is valid and contains expected columns
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int addressIndex = cursor.getColumnIndex(COLUMN_ADDRESS);

                // Check if the columns exist in the cursor
                if (nameIndex != -1 && addressIndex != -1) {
                    // Loop through all rows and add matching primary and secondary texts to list
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(nameIndex);
                        String address = cursor.getString(addressIndex);

                        Map<String, String> textMap = new HashMap<>();
                        textMap.put("primary", name);
                        textMap.put("secondary", address);

                        textsList.add(textMap);
                    }
                }
            }
        } finally {
            // Close cursor and database connection
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return textsList;
    }

    // Check if the place already exists in the database
    private boolean isPlaceExists(PlaceDetails place) {
        db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_NAME + " = ? AND " +
                COLUMN_ADDRESS + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{place.getName(), place.getAddress()});

        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();

        return exists;
    }

    // Method to clear all data from the table
    public void clearTable() {
        // Ensure db is not null before executing SQL commands
        if (db != null) {
            db.execSQL("DELETE FROM " + TABLE_NAME); // Clear all data from the table
        } else {
            Log.e("SavePlaceHistoryDBHandler", "Database is null. Cannot clear table.");
        }
    }

    // Method to delete a place by ID
    public void deletePlaceById(int placeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?",
                new String[]{String.valueOf(placeId)});
        db.close();
    }

    // Method to delete a place by name
    public void deletePlaceByName(String placeName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_NAME + " = ?",
                new String[]{placeName});
        db.close();
    }

    // Method to clean the address (remove numeric part at the end)
    private String cleanAddress(String address) {
        // Assuming the address format is "18 Raffles, Singpore 11234455"
        // Split the address by spaces
        String[] parts = address.split("\\s+");
        // Check if the last part is numeric (assuming it's a zip code or similar)
        if (parts.length > 1 && parts[parts.length - 1].matches("\\d+")) {
            // Join all parts except the last one (numeric part)
            StringBuilder cleanAddress = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                if (i > 0) {
                    cleanAddress.append(" ");
                }
                cleanAddress.append(parts[i]);
            }
            return cleanAddress.toString();
        }
        return address; // Return original address if no numeric part found
    }
}
