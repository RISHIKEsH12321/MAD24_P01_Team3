package sg.edu.np.mad.travelhub;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.gson.Gson;

public class SavePlaceHistoryDBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PlaceHistory.db";
    private static final int DATABASE_VERSION = 3;
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
                + COLUMN_PLACE_DETAILS + " TEXT,"
                + "insert_count INTEGER DEFAULT 0,"
                + "date_added INTEGER" + ")";  // Change to INTEGER
        db.execSQL(CREATE_TABLE);

        Log.d("Table Creation", "true");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Adding new columns to the existing table
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN insert_count INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN date_added INTEGER");
        }
        // If further versions require upgrades, handle them here
    }

    public void insertPlaceDetails(PlaceDetails place) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();

            if (isPlaceExists(place)) {
                int currentCount = getInsertCount(place);

                ContentValues values = new ContentValues();
                values.put("insert_count", currentCount + 1);
                values.put("date_added", getCurrentTimestamp());

                db.update(TABLE_NAME, values,
                        COLUMN_NAME + " = ? AND " + COLUMN_ADDRESS + " = ?",
                        new String[]{place.getName(), cleanAddress(place.getAddress())});
            } else {
                place.setHistory(true);

                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME, place.getName());
                values.put(COLUMN_ADDRESS, cleanAddress(place.getAddress()));
                values.put(COLUMN_PLACE_DETAILS, new Gson().toJson(place));
                values.put("insert_count", 1);
                values.put("date_added", getCurrentTimestamp());
                db.insert(TABLE_NAME, null, values);
            }
        } catch (Exception e) {
            Log.e("SavePlaceHistoryDBHandler", "Error inserting place details", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    // Retrieve PlaceDetails by ID from the database
    public PlaceDetails getPlaceDetails(int id) {
        db = getReadableDatabase();
        Cursor cursor = null;
        PlaceDetails place = null;

        try {
            cursor = db.query(TABLE_NAME, new String[]{COLUMN_PLACE_DETAILS},
                    COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                    null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                place = new Gson().fromJson(cursor.getString(0), PlaceDetails.class);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return place;
    }

    // Retrieve all PlaceDetails from the database, ordered by most recently viewed and then by count
    public List<PlaceDetails> getAllPlaceDetails() {
        List<PlaceDetails> placeList = new ArrayList<>();
        // SQL query to select and order by most recently viewed and then by count
        String selectQuery = "SELECT * FROM " + TABLE_NAME +
                " ORDER BY date_added DESC, insert_count DESC";

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex(COLUMN_PLACE_DETAILS);

                if (columnIndex != -1) {
                    // Looping through all rows and adding to list
                    while (cursor.moveToNext()) {
                        String placeDetailsJson = cursor.getString(columnIndex);
                        PlaceDetails place = new Gson().fromJson(placeDetailsJson, PlaceDetails.class);
                        placeList.add(place);
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return placeList;
    }

    public List<PlaceDetails> getPlacesByQuery(String query) {
        List<PlaceDetails> placeList = new ArrayList<>();

        // SQL query to retrieve places where the name matches the query
        String selectQuery = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_NAME + " LIKE '%" + query + "%'";

        db = getWritableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(selectQuery, null);
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
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return placeList;
    }

    // Retrieve all primary and secondary texts from the database, ordered by most recently viewed and then by count
    public List<Map<String, String>> getAllPrimaryAndSecondaryTexts() {
        List<Map<String, String>> textsList = new ArrayList<>();
        // SQL query to select and order by most recently viewed and then by count
        String selectQuery = "SELECT " + COLUMN_NAME + ", " + COLUMN_ADDRESS +
                " FROM " + TABLE_NAME +
                " ORDER BY date_added DESC, insert_count DESC";

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int addressIndex = cursor.getColumnIndex(COLUMN_ADDRESS);

                if (nameIndex != -1 && addressIndex != -1) {
                    // Loop through all rows and add to list
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
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

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
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(selectQuery, null);
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int addressIndex = cursor.getColumnIndex(COLUMN_ADDRESS);

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
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return textsList;
    }

    public void deletePlaceByName(String placeName) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            // Sanitize input
            placeName = placeName.trim();
            // Delete records where the name matches the given placeName
            int rowsAffected = db.delete(TABLE_NAME, COLUMN_NAME + " = ?", new String[]{placeName});
            Log.d("Delete Operation", "Rows affected: " + rowsAffected);
        } catch (Exception e) {
            Log.e("SavePlaceHistoryDBHandler", "Error deleting place by name", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    private boolean isPlaceExists(PlaceDetails place) {
        db = getReadableDatabase();
        Cursor cursor = null;

        try {
            // Check for null values in place details
            if (place.getName() == null || place.getAddress() == null) {
                Log.e("SavePlaceHistoryDBHandler", "Place name or address is null.");
                return false;
            }

            String name = place.getName().trim();
            String address = cleanAddress(place.getAddress().trim());

            String query = "SELECT COUNT(*) FROM " + TABLE_NAME +
                    " WHERE " + COLUMN_NAME + " = ? AND " +
                    COLUMN_ADDRESS + " = ?";
            cursor = db.rawQuery(query, new String[]{name, address});

            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                return (count > 0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    private int getInsertCount(PlaceDetails place) {
        db = getReadableDatabase();
        Cursor cursor = null;
        int count = 0;

        try {
            String query = "SELECT insert_count FROM " + TABLE_NAME +
                    " WHERE " + COLUMN_NAME + " = ? AND " + COLUMN_ADDRESS + " = ?";
            cursor = db.rawQuery(query, new String[]{place.getName(), cleanAddress(place.getAddress())});

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    private int getCurrentTimestamp() {
        return (int) (System.currentTimeMillis() / 1000);  // Convert milliseconds to seconds
    }

    private String cleanAddress(String address) {
        // Clean address as needed
        return address.trim();
    }
}
