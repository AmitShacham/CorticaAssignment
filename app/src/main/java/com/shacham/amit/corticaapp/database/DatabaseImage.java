package com.shacham.amit.corticaapp.database;

import android.net.Uri;

public class DatabaseImage {

    private Uri mUri;
    private String mImageDate;

    public DatabaseImage(Uri uri, String imageDate) {
        mUri = uri;
        mImageDate = imageDate;
    }

    public Uri getUri() {
        return mUri;
    }

    public String getImageDate() {
        return mImageDate;
    }
}
