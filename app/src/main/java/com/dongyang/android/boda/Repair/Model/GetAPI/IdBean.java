package com.dongyang.android.boda.Repair.Model.GetAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/*
      "id": {
              "kind": "youtube#video",
              "videoId": "TnIV89bMf6A"
              },
 */

public class IdBean {

    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("videoId")
    @Expose
    private String videoId;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
