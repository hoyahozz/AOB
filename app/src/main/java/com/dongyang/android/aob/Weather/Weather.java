package com.dongyang.android.aob.Weather;

public class Weather {
    private String TMP; // 기온
    private String SKY; // 하늘 상태
    private String PTY; // 강수형태
    private String POP; // 강수확률

    public Weather(String TMP, String SKY, String PTY, String POP) {
        this.TMP = TMP;
        this.SKY = SKY;
        this.PTY = PTY;
        this.POP = POP;
    }

    public String getTMP() {
        return TMP;
    }

    public void setTMP(String TMP) {
        this.TMP = TMP;
    }

    public String getSKY() {
        return SKY;
    }

    public void setSKY(String SKY) {
        this.SKY = SKY;
    }

    public String getPTY() {
        return PTY;
    }

    public void setPTY(String PTY) {
        this.PTY = PTY;
    }

    public String getPOP() {
        return POP;
    }

    public void setPOP(String POP) {
        this.POP = POP;
    }
}
