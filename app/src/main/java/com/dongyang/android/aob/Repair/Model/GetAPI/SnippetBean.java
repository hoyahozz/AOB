package com.dongyang.android.aob.Repair.Model.GetAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/*
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

public class SnippetBean {
    @SerializedName("publishedAt")
    @Expose
    private String publishedAt;
    @SerializedName("channelId")
    @Expose
    private String channelId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("thumbnails")
    private ThumbnailsBean thumbnails;
    @SerializedName("channelTitle")
    @Expose
    private String channelTitle;
    @SerializedName("liveBroadcastContent")
    @Expose
    private String liveBroadcastContent;
    @SerializedName("publishTime")
    @Expose
    private String publishTime;

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ThumbnailsBean getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(ThumbnailsBean thumbnails) {
        this.thumbnails = thumbnails;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getLiveBroadcastContent() {
        return liveBroadcastContent;
    }

    public void setLiveBroadcastContent(String liveBroadcastContent) {
        this.liveBroadcastContent = liveBroadcastContent;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }
}
