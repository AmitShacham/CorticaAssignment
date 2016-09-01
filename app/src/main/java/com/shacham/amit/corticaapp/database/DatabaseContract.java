package com.shacham.amit.corticaapp.database;

import android.provider.BaseColumns;

public final class DatabaseContract {

    private DatabaseContract() {
    }

    public static class LocalImageDBEntry implements BaseColumns {
        public static final String TABLE_NAME = "local_images";
        public static final String COLUMN_NAME_IMAGE_NAME = "image_name";
        public static final String COLUMN_NAME_IMAGE_URI = "image_uri";
        public static final String COLUMN_NAME_IMAGE_DATE = "image_date";
    }

    public static class FacebookImageDBEntry implements BaseColumns {
        public static final String TABLE_NAME = "facebook_images";
        public static final String COLUMN_NAME_IMAGE_NAME = "image_name";
        public static final String COLUMN_NAME_IMAGE_URI = "image_uri";
        public static final String COLUMN_NAME_IMAGE_DATE = "image_date";
    }
}
