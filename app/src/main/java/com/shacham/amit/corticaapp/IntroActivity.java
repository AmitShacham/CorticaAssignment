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
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.shacham.amit.corticaapp.async_tasks.LoadFacebookImagesIntoDataBaseAsyncTask;
import com.shacham.amit.corticaapp.async_tasks.LoadLocalImagesIntoDataBaseAsyncTask;
import com.shacham.amit.corticaapp.database.DatabaseContract;
import com.shacham.amit.corticaapp.database.DatabaseHelper;

import org.json.JSONObject;

public class IntroActivity extends Activity implements
        LoadLocalImagesIntoDataBaseAsyncTask.AsyncTaskCallback,
        LoadFacebookImagesIntoDataBaseAsyncTask.AsyncTaskCallback,
        View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private Button mGetStartedButton;
    private LinearLayout mLoadingLayout;
    private LoginButton mFacebookLoginButton;
    private CallbackManager mCallbackManager;
    private LinearLayout mFacebookLoginLayout;
    private Button mNoFacebookImagesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_intro);

        initViews();
        initListeners();

        mCallbackManager = CallbackManager.Factory.create();

        if (wereImagesLoaded()) {
            startMainActivity();
        }
    }

    private void initViews() {
        mGetStartedButton = (Button) findViewById(R.id.get_started_button);
        mLoadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        mFacebookLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        mFacebookLoginLayout = (LinearLayout) findViewById(R.id.facebook_login_layout);
        mNoFacebookImagesButton = (Button) findViewById(R.id.no_facebook_images_button);
    }

    private void initListeners() {
        mGetStartedButton.setOnClickListener(this);
        mNoFacebookImagesButton.setOnClickListener(this);
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
        editor.commit();

        askForFacebookImages();
    }

    private void askForFacebookImages() {
        mFacebookLoginLayout.setVisibility(View.VISIBLE);

        mFacebookLoginButton.setReadPermissions("user_photos");
        mFacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                String responseString = response.getRawResponse();
                                loadFacebookImagesToDataBase(responseString);
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "photos{link}");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // Do nothing
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(IntroActivity.this, "Problem with Facebook login. Images from Facebook will not be shown.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadFacebookImagesToDataBase(String responseString) {
        mLoadingLayout.setVisibility(View.VISIBLE);
        ((TextView) mLoadingLayout.findViewById(R.id.loading_text)).setText(getString(R.string.loading_facebook_images));
        LoadFacebookImagesIntoDataBaseAsyncTask asyncTask = new LoadFacebookImagesIntoDataBaseAsyncTask(this, this, responseString);
        asyncTask.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void loadFacebookImagesFinished(int numberOfImages, long amountOfTimeTaken) {
        mLoadingLayout.setVisibility(View.GONE);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.sp_were_facebook_images_loaded), true);
        editor.putInt(getString(R.string.sp_number_of_facebook_images), numberOfImages);
        editor.putLong(getString(R.string.sp_amount_of_time_taken_facebook), amountOfTimeTaken);
        editor.commit();

        startMainActivity();
    }
}
