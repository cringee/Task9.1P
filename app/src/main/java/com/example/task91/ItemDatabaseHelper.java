package com.example.task91;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItemDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "item_database";
    private static final String TABLE_NAME = "items";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_POST_TYPE = "post_type";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_MARKER_LOCATION = "marker_location";

    public ItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_POST_TYPE + " TEXT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_MARKER_LOCATION + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST_TYPE, item.getPostType());
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_PHONE, item.getPhone());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_DATE, item.getDate());
        //values.put(COLUMN_LOCATION, item.getLocation());
        values.put(COLUMN_MARKER_LOCATION, item.getMarkerLocation());
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        int idIndex = cursor.getColumnIndex(COLUMN_ID);
        int postTypeIndex = cursor.getColumnIndex(COLUMN_POST_TYPE);
        int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
        int phoneIndex = cursor.getColumnIndex(COLUMN_PHONE);
        int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
        int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
        int locationIndex = cursor.getColumnIndex(COLUMN_LOCATION);
        int markerLocationIndex = cursor.getColumnIndex(COLUMN_MARKER_LOCATION);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(idIndex);
            String postType = (postTypeIndex >= 0) ? cursor.getString(postTypeIndex) : "";
            String name = (nameIndex >= 0) ? cursor.getString(nameIndex) : "";
            String phone = (phoneIndex >= 0) ? cursor.getString(phoneIndex) : "";
            String description = (descriptionIndex >= 0) ? cursor.getString(descriptionIndex) : "";
            String date = (dateIndex >= 0) ? cursor.getString(dateIndex) : "";
            String location = (locationIndex >= 0) ? cursor.getString(locationIndex) : "";
            String markerLocation = (markerLocationIndex >= 0) ? cursor.getString(markerLocationIndex) : "";

            // Split the marker location into latitude and longitude values
            String[] latLngValues = markerLocation.split(",");
            double latitude = Double.parseDouble(latLngValues[0].trim());
            double longitude = Double.parseDouble(latLngValues[1].trim());

            Item item = new Item(postType, name, phone, description, date);
            item.setId(id);
            item.setLatitude(latitude);
            item.setLongitude(longitude);
            items.add(item);
        }
        cursor.close();
        db.close();
        return items;
    }

    public void removeItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(item.getId())});
        db.close();
    }
}

class Item implements Serializable {
    private String postType;
    private String name;
    private String phone;
    private String description;
    private String date;
    //private String location;
    private double latitude;
    private double longitude;
    private long id;

    public Item(String postType, String name, String phone, String description, String date) {
        this.postType = postType;
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.date = date;
        //this.location = location;
        this.id = id;
    }

    public String getPostType() {
        return postType;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public String getMarkerLocation() {
        return latitude + ", " + longitude;
    }
}