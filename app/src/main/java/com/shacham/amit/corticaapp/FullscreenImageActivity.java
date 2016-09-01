package com.shacham.amit.corticaapp;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class FullscreenImageActivity extends AppCompatActivity implements
        View.OnClickListener {

    public static final String KEY_IMAGE_DATE = "image_date";
    public static final String KEY_IMAGE_URI = "image_uri";

    private TextView mImageDate;
    private ImageView mFullscreenImage;
    private Button mBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulscreen_image);

        initViews();
        initListeners();

        setImageInfo();
    }

    private void initViews() {
        mImageDate = (TextView) findViewById(R.id.fullscreen_image_date);
        mFullscreenImage = (ImageView) findViewById(R.id.fullscreen_image);
        mBackButton = (Button) findViewById(R.id.fullscreen_back_button);
    }

    private void initListeners() {
        mBackButton.setOnClickListener(this);
    }

    private void setImageInfo() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mImageDate.setText(extras.getString(KEY_IMAGE_DATE));
            Picasso.with(this)
                    .load(Uri.parse(extras.getString(KEY_IMAGE_URI)))
                    .noFade()
                    .into(mFullscreenImage);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fullscreen_back_button:
                onBackPressed();
                break;
        }
    }
}
