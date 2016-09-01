package com.shacham.amit.corticaapp.json_parsing;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Wrapper {

    @SerializedName("photos")
    private Photos photos;

    public Photos getPhotos() {
        return photos;
    }

    public class Photos {

        @SerializedName("data")
        private List<Data> data;

        public List<Data> getData() {
            return data;
        }

        public class Data {

            @SerializedName("link")
            private String link;
            @SerializedName("id")
            private String id;

            public String getLink() {
                return link;
            }

            public String getId() {
                return id;
            }
        }
    }
}
