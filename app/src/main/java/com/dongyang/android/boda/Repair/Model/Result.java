package com.dongyang.android.boda.Repair.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    // 딥 러닝 서버로부터 결과물을 받아오고, 그 값을 여기에 넣는 구조

    @SerializedName("result")
    @Expose
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
