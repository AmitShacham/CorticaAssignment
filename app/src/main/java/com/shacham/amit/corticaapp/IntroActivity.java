package com.shacham.amit.corticaapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.shacham.amit.corticaapp.async_tasks.LoadFacebookImagesIntoDataBaseAsyncTask;
import com.shacham.amit.corticaapp.async_tasks.LoadLocalImagesIntoDataBaseAsyncTask;
import com.shacham.amit.corticaapp.database.DatabaseContract;
import com.shacham.amit.corticaapp.database.DatabaseHelper;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.entities.Photo;
import com.sromku.simple.fb.listeners.OnActionListener;
import com.sromku.simple.fb.listeners.OnLoginListener;

import java.util.List;

public class IntroActivity extends Activity implements
        LoadLocalImagesIntoDataBaseAsyncTask.AsyncTaskCallback,
        LoadFacebookImagesIntoDataBaseAsyncTask.AsyncTaskCallback,
        View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private SimpleFacebook mSimpleFacebook;

    private Button mGetStartedButton;
    private LinearLayout mLoadingLayout;
    private LinearLayout mFacebookLoginLayout;
    private Button mGetFacebookImagesButton;
    private Button mNoFacebookImagesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_intro);

        initViews();
        initListeners();
        initSimpleFacebook();

        if (wereImagesLoaded()) {
            startMainActivity();
        }
    }

    private void initViews() {
        mGetStartedButton = (Button) findViewById(R.id.get_started_button);
        mLoadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        mFacebookLoginLayout = (LinearLayout) findViewById(R.id.facebook_login_layout);
        mGetFacebookImagesButton = (Button) findViewById(R.id.get_facebook_images_button);
        mNoFacebookImagesButton = (Button) findViewById(R.id.no_facebook_images_button);
    }

    private void initListeners() {
        mGetStartedButton.setOnClickListener(this);
        mGetFacebookImagesButton.setOnClickListener(this);
        mNoFacebookImagesButton.setOnClickListener(this);
    }

    private void initSimpleFacebook() {
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(getString(R.string.facebook_app_id))
                .setPermissions(new Permission[]{Permission.USER_PHOTOS})
                .build();
        SimpleFacebook.setConfiguration(configuration);
    }

    private boolean wereImagesLoaded() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String countQuery = "SELECT count(*) FROM " + DatabaseContract.LocalImageDBEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_started_button:
                checkForPermission();
                break;
            case R.id.get_facebook_images_button:
                loginToFacebook();
                break;
            case R.id.no_facebook_images_button:
                startMainActivity();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void checkForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            loadLocalImagesToDataBase();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadLocalImagesToDataBase();
                } else {
                    askForFacebookImages();
                }
                break;
            }
        }
    }

    private void loadLocalImagesToDataBase() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        ((TextView) mLoadingLayout.findViewById(R.id.loading_text)).setText(getString(R.string.loading_local_images));
        LoadLocalImagesIntoDataBaseAsyncTask asyncTask = new LoadLocalImagesIntoDataBaseAsyncTask(this, this);
        asyncTask.execute();
    }

    @Override
    public void loadLocalImagesFinished(int numberOfImages, long amountOfTimeTaken) {
        mLoadingLayout.setVisibility(View.GONE);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.sp_were_local_images_loaded), true);
        editor.putInt(getString(R.string.sp_number_of_local_images), numberOfImages);
        editor.putLong(getString(R.string.sp_amount_of_time_taken_local), amountOfTimeTaken);
        editor.apply();

        askForFacebookImages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }

    private void askForFacebookImages() {
        mFacebookLoginLayout.setVisibility(View.VISIBLE);
    }

    private void loginToFacebook() {
        mSimpleFacebook.login(new OnLoginListener() {
            @Override
            public void onLogin(String accessToken, List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {
                getImages();
            }

            @Override
            public void onCancel() {
                startMainActivity();
            }

            @Override
            public void onException(Throwable throwable) {
                startMainActivity();
            }

            @Override
            public void onFail(String reason) {
                startMainActivity();
            }
        });
    }

    public void getImages() {
        Bundle params = new Bundle();
        params.putCharSequence("fields", "created_time,images");
        params.putCharSequence("limit", "99");

        mSimpleFacebook.get("me", "photos", params, new OnActionListener<List<Photo>>() {
            @Override
            public void onComplete(List<Photo> response) {
                super.onComplete(response);
                loadFacebookImagesToDataBase(response);
            }

            @Override
            public void onException(Throwable throwable) {
                super.onException(throwable);
                startMainActivity();
            }

            @Override
            public void onFail(String reason) {
                super.onFail(reason);
                startMainActivity();
            }
        });
    }

    private void loadFacebookImagesToDataBase(List<Photo> response) {
        mLoadingLayout.setVisibility(View.VISIBLE);
        ((TextView) mLoadingLayout.findViewById(R.id.loading_text)).setText(getString(R.string.loading_facebook_images));
        LoadFacebookImagesIntoDataBaseAsyncTask asyncTask = new LoadFacebookImagesIntoDataBaseAsyncTask(this, this, response);
        asyncTask.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void loadFacebookImagesFinished(int numberOfImages, long amountOfTimeTaken) {
        mLoadingLayout.setVisibility(View.GONE);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.sp_were_facebook_images_loaded), true);
        editor.putInt(getString(R.string.sp_number_of_facebook_images), numberOfImages);
        editor.putLong(getString(R.string.sp_amount_of_time_taken_facebook), amountOfTimeTaken);
        editor.apply();

        startMainActivity();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
