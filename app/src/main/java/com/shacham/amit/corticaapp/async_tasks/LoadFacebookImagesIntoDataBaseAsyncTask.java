package com.shacham.amit.corticaapp.async_tasks;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.google.gson.Gson;
import com.shacham.amit.corticaapp.database.DatabaseContract;
import com.shacham.amit.corticaapp.database.DatabaseHelper;
import com.shacham.amit.corticaapp.json_parsing.Wrapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LoadFacebookImagesIntoDataBaseAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final SimpleDateFormat INPUT_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+SSSS");
    private static final SimpleDateFormat OUTPUT_FORMAT = LoadLocalImagesIntoDataBaseAsyncTask.FORMATTER;

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
        insertFacebookImagesToDatabase();
        return null;
    }

    private void insertFacebookImagesToDatabase() {
        DatabaseHelper databaseHelper = new DatabaseHelper(mActivity);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        mStartTime = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        Gson gson = new Gson();
        Wrapper wrapper = gson.fromJson(mJsonResponse, Wrapper.class);
        List<Wrapper.Photos.Data> listOfPhotos = wrapper.getPhotos().getData();
        for (Wrapper.Photos.Data photoData : listOfPhotos) {
            values.put(DatabaseContract.FacebookImageDBEntry.COLUMN_NAME_IMAGE_NAME, photoData.getId());
            List<Wrapper.Photos.Data.Image> imagesList = photoData.getImages();
            for (Wrapper.Photos.Data.Image image : imagesList) {
                values.put(DatabaseContract.FacebookImageDBEntry.COLUMN_NAME_IMAGE_URI, image.getImageUri());
            }

            String imageDate = photoData.getImageDate();
            values.put(DatabaseContract.FacebookImageDBEntry.COLUMN_NAME_IMAGE_DATE, changeDateFormat(imageDate));

            db.insert(DatabaseContract.FacebookImageDBEntry.TABLE_NAME, null, values);
        }

        mEndTime = System.currentTimeMillis();
        mNumberOfImages = listOfPhotos.size();
    }

    public String changeDateFormat(String dateString) {
        Date date;
        String result = null;

        try {
            date = INPUT_FORMAT.parse(dateString);
            result = OUTPUT_FORMAT.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
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
