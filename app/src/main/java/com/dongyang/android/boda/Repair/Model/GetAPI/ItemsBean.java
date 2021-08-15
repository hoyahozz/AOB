package com.dongyang.android.boda.Repair.Model.GetAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/*
    "items":
            {
            "kind": "youtube#searchResult",
            "etag": "xITtJgKMK0_qxhjuLCzmvRf4h_w",
            "id": {
            "kind": "youtube#video",
            "videoId": "TnIV89bMf6A"
            },
            "snippet": {
            "publishedAt": "2020-10-13T10:33:46Z",
            "channelId": "UC7vp_WW0hDx-9_y5_oGwWJA",
            "title": "자전거 타이어 교체하는 방법 3분 초간단 정리",
            "description": "자전거 #펑크 #타이어 비즈니스 문의 blackmoonriding@gmail.com 루나틱이 운영하는 카페에 놀러오세요 https://cafe.naver.com/cyclingmagazine.",
            "thumbnails": {
            "default": {
            "url": "https://i.ytimg.com/vi/TnIV89bMf6A/default.jpg",
            "width": 120,
            "height": 90
            },
            "medium": {
            "url": "https://i.ytimg.com/vi/TnIV89bMf6A/mqdefault.jpg",
            "width": 320,
            "height": 180
            },
            "high": {
            "url": "https://i.ytimg.com/vi/TnIV89bMf6A/hqdefault.jpg",
            "width": 480,
            "height": 360
            }
            },
            "channelTitle": "루나틱Cycling",
            "liveBroadcastContent": "none",
            "publishTime": "2020-10-13T10:33:46Z"
            }
 */



public class ItemsBean {
    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("etag")
    @Expose
    private String etag;
    @SerializedName("id")
    @Expose
    private IdBean id;
    @SerializedName("snippet")
    @Expose
    private SnippetBean snippet;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public IdBean getId() {
        return id;
    }

    public void setId(IdBean id) {
        this.id = id;
    }

    public SnippetBean getSnippet() {
        return snippet;
    }

    public void setSnippet(SnippetBean snippet) {
        this.snippet = snippet;
    }
}
