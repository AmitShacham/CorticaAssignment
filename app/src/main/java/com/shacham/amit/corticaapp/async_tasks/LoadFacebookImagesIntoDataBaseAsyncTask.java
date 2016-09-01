package com.shacham.amit.corticaapp.async_tasks;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.shacham.amit.corticaapp.database.DatabaseContract;
import com.shacham.amit.corticaapp.database.DatabaseHelper;
import com.shacham.amit.corticaapp.json_parsing.Wrapper;

import java.util.List;

public class LoadFacebookImagesIntoDataBaseAsyncTask extends AsyncTask<Void, Void, Void> {

    private Activity mActivity;
    private AsyncTaskCallback mCallback;
    private String mJsonResponse;
    private int mNumberOfImages;
    private long mStartTime;
    private long mEndTime;

    public LoadFacebookImagesIntoDataBaseAsyncTask(Activity activity, AsyncTaskCallback callback, String jsonResponse) {
        mActivity = activity;
        mCallback = callback;
        mJsonResponse = jsonResponse;
    }

    @Override
    protected Void doInBackground(Void... params) {
        insertLocalImagesToDatabase();
        return null;
    }

    private void insertLocalImagesToDatabase() {
        DatabaseHelper databaseHelper = new DatabaseHelper(mActivity);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        mStartTime = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        Gson gson = new Gson();
        Wrapper wrapper = gson.fromJson(mJsonResponse, Wrapper.class);
        List<Wrapper.Photos.Data> listOfPhotos = wrapper.getPhotos().getData();
        for (Wrapper.Photos.Data photoData : listOfPhotos) {
            values.put(DatabaseContract.FacebookImageDBEntry.COLUMN_NAME_IMAGE_NAME, photoData.getId());
            values.put(DatabaseContract.FacebookImageDBEntry.COLUMN_NAME_IMAGE_URI, photoData.getLink());
            // TODO: Add image date

            db.insert(DatabaseContract.FacebookImageDBEntry.TABLE_NAME, null, values);
        }

        mEndTime = System.currentTimeMillis();
        mNumberOfImages = listOfPhotos.size();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        long timeTaken = mEndTime - mStartTime;
        mCallback.loadFacebookImagesFinished(mNumberOfImages, timeTaken);
    }

    public interface AsyncTaskCallback {
        void loadFacebookImagesFinished(int numberOfImages, long amountOfTimeTaken);
    }
}
