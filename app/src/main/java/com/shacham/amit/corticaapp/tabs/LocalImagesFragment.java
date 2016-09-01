package com.shacham.amit.corticaapp.tabs;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shacham.amit.corticaapp.R;
import com.shacham.amit.corticaapp.database.DatabaseContract;
import com.shacham.amit.corticaapp.database.DatabaseHelper;
import com.shacham.amit.corticaapp.database.DatabaseImage;

public class LocalImagesFragment extends TabFragment {

    private boolean mWereLocalImagesLoaded;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWereLocalImagesLoaded = mSharedPrefs.getBoolean(getString(R.string.sp_were_local_images_loaded), false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (!mWereLocalImagesLoaded) {
            showNoImagesError();
        } else {
            readLocalImagesFromDatabase();
            insertImagesIntoGridView();
        }

        return view;
    }

    private void readLocalImagesFromDatabase() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.LocalImageDBEntry._ID,
                DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_NAME,
                DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_URI,
                DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_DATE
        };

        String sortOrder = DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_NAME + " ASC";

        Cursor cursor = db.query(
                DatabaseContract.LocalImageDBEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        cursor.moveToFirst();

        do {
            long imageId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.LocalImageDBEntry._ID));
            String imageName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_NAME));
            String imageUriString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_URI));
            Uri imageUri = Uri.parse(imageUriString);
            String imageDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_DATE));
            mImageList.add(new DatabaseImage(imageUri, imageDate));
        } while (cursor.moveToNext());

        cursor.close();
    }
}
