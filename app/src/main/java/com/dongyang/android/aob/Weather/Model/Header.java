package com.dongyang.android.aob.Weather.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Header {

    /*
    "header":{
        "resultCode":"00",
        "resultMsg":"NORMAL_SERVICE"
        }

    */

    @SerializedName("resultCode")
    @Expose
    public String resultCode;

    @SerializedName("resultMsg")
    @Expose
    public String resultMsg;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

}