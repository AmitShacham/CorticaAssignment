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

            @SerializedName("images")
            private List<Image> images;
            @SerializedName("id")
            private String id;
            @SerializedName("created_time")
            private String imageDate;

            public List<Image> getImages() {
                return images;
            }

            public String getId() {
                return id;
            }

            public String getImageDate() {
                return imageDate;
            }

            public class Image {

                @SerializedName("source")
                private String imageUri;

                public String getImageUri() {
                    return imageUri;
                }
            }
        }

        @SerializedName("paging")
        private Paging paging;

        public class Paging {

            @SerializedName("cursors")
            private Cursors cursors;

            public Cursors getCursors() {
                return cursors;
            }

            public class Cursors {

                @SerializedName("after")
                private String after;

                public String getAfter() {
                    return after;
                }
            }

            @SerializedName("next")
            private String next;

            public String getNext() {
                return next;
            }
        }

        public Paging getPaging() {
            return paging;
        }
    }
}
