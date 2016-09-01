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
import com.sromku.simple.fb.entities.Image;
import com.sromku.simple.fb.entities.Photo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LoadFacebookImagesIntoDataBaseAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final SimpleDateFormat OUTPUT_FORMAT = LoadLocalImagesIntoDataBaseAsyncTask.FORMATTER;

    private Activity mActivity;
    private AsyncTaskCallback mCallback;
    private List<Photo> mListOfImages;
    private int mNumberOfImages;
    private long mStartTime;
    private long mEndTime;

    public LoadFacebookImagesIntoDataBaseAsyncTask(Activity activity, AsyncTaskCallback callback, List<Photo> listOfImages) {
        mActivity = activity;
        mCallback = callback;
        mListOfImages = listOfImages;
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
        for (Photo photo : mListOfImages) {
            values.put(DatabaseContract.FacebookImageDBEntry.COLUMN_NAME_IMAGE_NAME, photo.getId());
            List<Image> imagesList = photo.getImages();
            if (imagesList.size() > 0) {
                for (Image image : imagesList) {
                    values.put(DatabaseContract.FacebookImageDBEntry.COLUMN_NAME_IMAGE_URI, image.getSource());
                }
            }
            Date imageDate = photo.getCreatedTime();
            values.put(DatabaseContract.FacebookImageDBEntry.COLUMN_NAME_IMAGE_DATE, changeDateFormat(imageDate));

            db.insert(DatabaseContract.FacebookImageDBEntry.TABLE_NAME, null, values);
        }

        mEndTime = System.currentTimeMillis();
        mNumberOfImages = mListOfImages.size();
    }

    public String changeDateFormat(Date date) {
        return OUTPUT_FORMAT.format(date);
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
