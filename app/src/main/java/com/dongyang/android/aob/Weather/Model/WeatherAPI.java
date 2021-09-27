package com.dongyang.android.aob.Weather.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherAPI {

    /*
    {"response":{"header":{"resultCode":"00","resultMsg":"NORMAL_SERVICE"},"body":{"dataType":"JSON","items":{"item":[{"baseDate":"20210920","baseTime":"0500","category":"TMP","fcstDate":"20210920","fcstTime":"0600","fcstValue":"20","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"UUU","fcstDate":"20210920","fcstTime":"0600","fcstValue":"-1.9","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"VVV","fcstDate":"20210920","fcstTime":"0600","fcstValue":"1.4","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"VEC","fcstDate":"20210920","fcstTime":"0600","fcstValue":"125","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"WSD","fcstDate":"20210920","fcstTime":"0600","fcstValue":"2.4","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"SKY","fcstDate":"20210920","fcstTime":"0600","fcstValue":"1","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"PTY","fcstDate":"20210920","fcstTime":"0600","fcstValue":"0","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"POP","fcstDate":"20210920","fcstTime":"0600","fcstValue":"0","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"PCP","fcstDate":"20210920","fcstTime":"0600","fcstValue":"강수없음","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"REH","fcstDate":"20210920","fcstTime":"0600","fcstValue":"85","nx":55,"ny":127},{"baseDate":"20210920","baseTime":"0500","category":"SNO","fcstDate":"20210920","fcstTime":"0600","fcstValue":"적설없음","nx":55,"ny":127}]},"pageNo":1,"numOfRows":11,"totalCount":742}}}
    */

    @SerializedName("response")
    @Expose
    public Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

}