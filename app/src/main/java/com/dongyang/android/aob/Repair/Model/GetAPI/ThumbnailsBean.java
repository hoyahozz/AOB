package com.dongyang.android.aob.Repair.Model.GetAPI;

import com.google.gson.annotations.SerializedName;

public class ThumbnailsBean {
    /**
     * default : {"url":"https://i.ytimg.com/vi/bw8gQjJqUNY/default.jpg","width":120,"height":90}
     * medium : {"url":"https://i.ytimg.com/vi/bw8gQjJqUNY/mqdefault.jpg","width":320,"height":180}
     * high : {"url":"https://i.ytimg.com/vi/bw8gQjJqUNY/hqdefault.jpg","width":480,"height":360}
     * standard : {"url":"https://i.ytimg.com/vi/bw8gQjJqUNY/sddefault.jpg","width":640,"height":480}
     * maxres : {"url":"https://i.ytimg.com/vi/bw8gQjJqUNY/maxresdefault.jpg","width":1280,"height":720}
     */

    @SerializedName("default")
    private DefaultBean defaultX;
    @SerializedName("medium")
    private MediumBean medium;
    @SerializedName("high")
    private HighBean high;

    public DefaultBean getDefaultX() {
        return defaultX;
    }

    public void setDefaultX(DefaultBean defaultX) {
        this.defaultX = defaultX;
    }

    public MediumBean getMedium() {
        return medium;
    }

    public void setMedium(MediumBean medium) {
        this.medium = medium;
    }

    public HighBean getHigh() {
        return high;
    }

    public void setHigh(HighBean high) {
        this.high = high;
    }

    public static class DefaultBean {
        /**
         * url : https://i.ytimg.com/vi/bw8gQjJqUNY/default.jpg
         * width : 120
         * height : 90
         */

        @SerializedName("url")
        private String url;
        @SerializedName("width")
        private int width;
        @SerializedName("height")
        private int height;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public static class MediumBean {
        /**
         * url : https://i.ytimg.com/vi/bw8gQjJqUNY/mqdefault.jpg
         * width : 320
         * height : 180
         */

        @SerializedName("url")
        private String url;
        @SerializedName("width")
        private int width;
        @SerializedName("height")
        private int height;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public static class HighBean {
        /**
         * url : https://i.ytimg.com/vi/bw8gQjJqUNY/hqdefault.jpg
         * width : 480
         * height : 360
         */

        @SerializedName("url")
        private String url;
        @SerializedName("width")
        private int width;
        @SerializedName("height")
        private int height;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

}
