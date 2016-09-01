package com.shacham.amit.corticaapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ImagesDatabase.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String CREATE_TABLE_LOCAL_IMAGES =
            "CREATE TABLE " + DatabaseContract.LocalImageDBEntry.TABLE_NAME + " (" +
                    DatabaseContract.LocalImageDBEntry._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_NAME + TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_DATE + TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_URI + TEXT_TYPE + " )";

    private static final String CREATE_TABLE_FACEBOOK_IMAGES =
            "CREATE TABLE " + DatabaseContract.FacebookImageDBEntry.TABLE_NAME + " (" +
                    DatabaseContract.FacebookImageDBEntry._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContract.FacebookImageDBEntry.COLUMN_NAME_IMAGE_NAME + TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.FacebookImageDBEntry.COLUMN_NAME_IMAGE_DATE + TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.FacebookImageDBEntry.COLUMN_NAME_IMAGE_URI + TEXT_TYPE + " )";

    private static final String DELETE_LOCAL_IMAGES =
            "DROP TABLE IF EXISTS " + DatabaseContract.LocalImageDBEntry.TABLE_NAME;

    private static final String DELETE_FACEBOOK_IMAGES =
            "DROP TABLE IF EXISTS " + DatabaseContract.FacebookImageDBEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LOCAL_IMAGES);
        db.execSQL(CREATE_TABLE_FACEBOOK_IMAGES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_LOCAL_IMAGES);
        db.execSQL(DELETE_FACEBOOK_IMAGES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
