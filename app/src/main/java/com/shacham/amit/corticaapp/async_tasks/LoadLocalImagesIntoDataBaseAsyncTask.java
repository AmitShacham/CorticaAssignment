package com.shacham.amit.corticaapp.async_tasks;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.shacham.amit.corticaapp.database.DatabaseContract;
import com.shacham.amit.corticaapp.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LoadLocalImagesIntoDataBaseAsyncTask extends AsyncTask<Void, Void, Void> {

    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

    private Activity mActivity;
    private AsyncTaskCallback mCallback;
    private Cursor mCursor;
    private long mStartTime;
    private long mEndTime;

    public LoadLocalImagesIntoDataBaseAsyncTask(Activity activity, AsyncTaskCallback callback) {
        mActivity = activity;
        mCallback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        loadLocalImages();
        insertLocalImagesToDatabase();
        return null;
    }

    private void loadLocalImages() {
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_TAKEN};
        mCursor = mActivity.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_TAKEN + " DESC");
    }

    private void insertLocalImagesToDatabase() {
        DatabaseHelper databaseHelper = new DatabaseHelper(mActivity);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        mStartTime = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        mCursor.moveToFirst();
        Calendar calendar = Calendar.getInstance();
        do {
            int columnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            values.put(DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_NAME, columnIndex);

            int dateColumn = mCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
            int dateInMS = mCursor.getInt(dateColumn);
            calendar.setTimeInMillis(dateInMS);
            values.put(DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_DATE, FORMATTER.format(calendar.getTime()));

            Uri imageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + mCursor.getInt(columnIndex));
            values.put(DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_URI, imageUri.toString());

            db.insert(DatabaseContract.LocalImageDBEntry.TABLE_NAME, null, values);
        } while (mCursor.moveToNext());

        mEndTime = System.currentTimeMillis();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        long timeTaken = mEndTime - mStartTime;
        mCallback.loadLocalImagesFinished(mCursor.getCount(), timeTaken);
        mCursor.close();
    }

    public interface AsyncTaskCallback {
        void loadLocalImagesFinished(int numberOfImages, long amountOfTimeTaken);
    }
}
