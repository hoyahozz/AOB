package com.dongyang.android.boda.Repair.Model;

public class Youtube {
    private String title;
    private String url;

    // 우리가 출력해야하는 데이터는 타이틀과 썸네일밖에 존재하지 않으므로, 따로 파싱하여 나누어 둠.

    public Youtube(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
