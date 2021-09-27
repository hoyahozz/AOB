package com.dongyang.android.aob.Weather.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Items {

    /*
    "items":{
        "item":[{"baseDate":"20210920","baseTime":"0500","category":"TMP","fcstDate":"20210920","fcstTime":"0600","fcstValue":"20","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"UUU","fcstDate":"20210920","fcstTime":"0600","fcstValue":"-1.9","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"VVV","fcstDate":"20210920","fcstTime":"0600","fcstValue":"1.4","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"VEC","fcstDate":"20210920","fcstTime":"0600","fcstValue":"125","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"WSD","fcstDate":"20210920","fcstTime":"0600","fcstValue":"2.4","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"SKY","fcstDate":"20210920","fcstTime":"0600","fcstValue":"1","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"PTY","fcstDate":"20210920","fcstTime":"0600","fcstValue":"0","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"POP","fcstDate":"20210920","fcstTime":"0600","fcstValue":"0","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"PCP","fcstDate":"20210920","fcstTime":"0600","fcstValue":"강수없음","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"REH","fcstDate":"20210920","fcstTime":"0600","fcstValue":"85","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"SNO","fcstDate":"20210920","fcstTime":"0600","fcstValue":"적설없음","nx":55,"ny":127}]
        }
    */

    @SerializedName("item")
    @Expose
    public ArrayList<Item> item = new ArrayList<>();

    public ArrayList<Item> getItem() {
        return item;
    }

    public void setItem(ArrayList<Item> item) {
        this.item = item;
    }

    public class Item {

    /*
    "item":[
        {
        "baseDate":"20210920",
        "baseTime":"0500",
        "category":"TMP",
        "fcstDate":"20210920",
        "fcstTime":"0600",
        "fcstValue":"20",
        "nx":55,"ny":127
        },
        {
        "baseDate":"20210920",
        "baseTime":"0500",
        "category":"UUU",
        "fcstDate":"20210920",
        "fcstTime":"0600",
        "fcstValue":"-1.9",
        "nx":55,"ny":127
        },
        {
        "baseDate":"20210920",
        "baseTime":"0500",
        "category":"VVV",
        "fcstDate":"20210920",
        "fcstTime":"0600",
        "fcstValue":"1.4",
        "nx":55,
        "ny":127
        },
        {
        "baseDate":"20210920",
        "baseTime":"0500",
        "category":"VEC",
        "fcstDate":"20210920",
        "fcstTime":"0600",
        "fcstValue":"125",
        "nx":55,
        "ny":127
        },
        {
        "baseDate":"20210920",
        "baseTime":"0500",
        "category":"WSD",
        "fcstDate":"20210920",
        "fcstTime":"0600",
        "fcstValue":"2.4",
        "nx":55,
        "ny":127
        },
        {
        "baseDate":"20210920",
        "baseTime":"0500",
        "category":"SKY",
        "fcstDate":"20210920",
        "fcstTime":"0600",
        "fcstValue":"1",
        "nx":55,
        "ny":127
        },
        {
        "baseDate":"20210920",
        "baseTime":"0500",
        "category":"PTY",
        "fcstDate":"20210920",
        "fcstTime":"0600",
        "fcstValue":"0",
        "nx":55,
        "ny":127
        },
        {
        "baseDate":"20210920",
        "baseTime":"0500",
        "category":"POP",
        "fcstDate":"20210920",
        "fcstTime":"0600",
        "fcstValue":"0",
        "nx":55,
        "ny":127
        },
        {
        "baseDate":"20210920",
        "baseTime":"0500",
        "category":"PCP",
        "fcstDate":"20210920",
        "fcstTime":"0600",
        "fcstValue":"강수없음",
        "nx":55,
        "ny":127
        },
        {
        "baseDate":"20210920",
        "baseTime":"0500",
        "category":"REH",
        "fcstDate":"20210920",
        "fcstTime":"0600",
        "fcstValue":"85",
        "nx":55,
        "ny":127
        },
        {
        "baseDate":"20210920",
        "baseTime":"0500",
        "category":"SNO",
        "fcstDate":"20210920",
        "fcstTime":"0600",
        "fcstValue":"적설없음",
        "nx":55,
        "ny":127
        }
    ]
    */

        @SerializedName("baseDate")
        @Expose
        public String baseDate;
        @SerializedName("baseTime")
        @Expose
        public String baseTime;
        @SerializedName("category")
        @Expose
        public String category;
        @SerializedName("fcstDate")
        @Expose
        public String fcstDate;
        @SerializedName("fcstTime")
        @Expose
        public String fcstTime;
        @SerializedName("fcstValue")
        @Expose
        public String fcstValue;
        @SerializedName("nx")
        @Expose
        public Integer nx;

        @SerializedName("ny")
        @Expose
        public Integer ny;

        public String getBaseDate() {
            return baseDate;
        }

        public void setBaseDate(String baseDate) {
            this.baseDate = baseDate;
        }

        public String getBaseTime() {
            return baseTime;
        }

        public void setBaseTime(String baseTime) {
            this.baseTime = baseTime;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getFcstDate() {
            return fcstDate;
        }

        public void setFcstDate(String fcstDate) {
            this.fcstDate = fcstDate;
        }

        public String getFcstTime() {
            return fcstTime;
        }

        public void setFcstTime(String fcstTime) {
            this.fcstTime = fcstTime;
        }

        public String getFcstValue() {
            return fcstValue;
        }

        public void setFcstValue(String fcstValue) {
            this.fcstValue = fcstValue;
        }

        public Integer getNx() {
            return nx;
        }

        public void setNx(Integer nx) {
            this.nx = nx;
        }

        public Integer getNy() {
            return ny;
        }

        public void setNy(Integer ny) {
            this.ny = ny;
        }

        @Override
        public String toString() {
            return "item{" +
                    "baseDate='" + baseDate + '\'' +
                    ",baseTime='" + baseTime + '\'' +
                    ",category='" + category + '\'' +
                    ",fcstDate='" + fcstDate + '\'' +
                    ",fcstTime='" + fcstTime + '\'' +
                    ",fcstValue=" + fcstValue + '\'' +
                    ",nx=" + nx +
                    ",ny=" + ny +
                    '}';
        }
    }

}
