package com.dongyang.android.boda.Repair.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Type {

    @SerializedName("type")
    @Expose
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
