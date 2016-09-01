package com.shacham.amit.corticaapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.shacham.amit.corticaapp.database.DatabaseContract;
import com.shacham.amit.corticaapp.database.DatabaseHelper;
import com.shacham.amit.corticaapp.tabs.PagerAdapter;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        TabLayout.OnTabSelectedListener,
        ViewPager.OnPageChangeListener {

    private boolean mWereLocalImagesLoaded;
    private boolean mWereFacebookImagesLoaded;
    private int mNumberOfLocalImages;
    private int mNumberOfFacebookImages;
    private long mAmountOfTimeTakenLocal;
    private long mAmountOfTimeTakenFacebook;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private ImageView mToolbarInfoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initListeners();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSupportActionBar(mToolbar);
        }

        initTabs();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mWereLocalImagesLoaded = sharedPref.getBoolean(getString(R.string.sp_were_local_images_loaded), false);
        mWereFacebookImagesLoaded = sharedPref.getBoolean(getString(R.string.sp_were_facebook_images_loaded), false);
        mNumberOfLocalImages = sharedPref.getInt(getString(R.string.sp_number_of_local_images), 0);
        mNumberOfFacebookImages = sharedPref.getInt(getString(R.string.sp_number_of_facebook_images), 0);
        mAmountOfTimeTakenLocal = sharedPref.getLong(getString(R.string.sp_amount_of_time_taken_local), 0);
        mAmountOfTimeTakenFacebook = sharedPref.getLong(getString(R.string.sp_amount_of_time_taken_facebook), 0);

        initViewPager();

//        readImagesFromDatabase();
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mToolbarInfoButton = (ImageView) findViewById(R.id.toolbar_info_button);
    }

    private void initListeners() {
        mTabLayout.setOnTabSelectedListener(this);
        mToolbarInfoButton.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(this);
    }

    private void initTabs() {
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.local_images)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.facebook_images)));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    private void initViewPager() {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(adapter);
    }

//    private void readImagesFromDatabase() {
//        DatabaseHelper databaseHelper = new DatabaseHelper(this);
//        SQLiteDatabase db = databaseHelper.getReadableDatabase();
//
//        // Define a projection that specifies which columns from the database
//        // you will actually use after this query.
//        String[] projection = {
//                DatabaseContract.LocalImageDBEntry._ID,
//                DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_NAME,
//                DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_URI,
//                DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_DATE
//        };
//
//        // Filter results WHERE "title" = 'My Title'
////        String selection = DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_NAME + " = ?";
////        String[] selectionArgs = { "My Title" };
//
//        // How you want the results sorted in the resulting Cursor
//        String sortOrder =
//                DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_NAME + " DESC";
//
//        Cursor cursor = db.query(
//                DatabaseContract.LocalImageDBEntry.TABLE_NAME,                     // The table to query
//                projection,                               // The columns to return
//                null, //selection,                                // The columns for the WHERE clause
//                null, //selectionArgs,                            // The values for the WHERE clause
//                null,                                     // don't group the rows
//                null,                                     // don't filter by row groups
//                sortOrder                                 // The sort order
//        );
//
//        cursor.moveToFirst();
//        long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.LocalImageDBEntry._ID));
//        String imageName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_NAME));
//        String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_URI));
//        String imageDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.LocalImageDBEntry.COLUMN_NAME_IMAGE_DATE));
//    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_info_button:
                showInfoDialog();
                break;
        }
    }

    private void showInfoDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Images Info");
        StringBuilder message = new StringBuilder();
        if (!mWereLocalImagesLoaded && !mWereFacebookImagesLoaded) {
            message.append(getString(R.string.no_images_to_show));
        } else {
            message.append(getString(R.string.local_images_info, String.valueOf(mNumberOfLocalImages), String.valueOf(mAmountOfTimeTakenLocal)));
        }
        if (mWereFacebookImagesLoaded) {
            message.append(getString(R.string.facebook_images_info, String.valueOf(mNumberOfFacebookImages), String.valueOf(mAmountOfTimeTakenFacebook)));
        }
        alertDialog.setMessage(message.toString());
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mTabLayout.getTabAt(position).select();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
