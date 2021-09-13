package com.dongyang.android.aob.Map.Model.Measurement;

import com.google.gson.annotations.SerializedName;

public class Measure {

    @SerializedName("success")
    private boolean success;
    @SerializedName("mnum")
    private int mnum;
    @SerializedName("id")
    private String id;
    @SerializedName("image")
    private String image;
    @SerializedName("time")
    private int time;
    @SerializedName("start_time")
    private String start_time;
    @SerializedName("end_time")
    private String end_time;
    @SerializedName("avg_speed")
    private double avg_speed;
    @SerializedName("dist")
    private double dist;
    @SerializedName("kcal")
    private double kcal;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getMnum() {
        return mnum;
    }

    public void setMnum(int mnum) {
        this.mnum = mnum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public double getAvg_speed() {
        return avg_speed;
    }

    public void setAvg_speed(double avg_speed) {
        this.avg_speed = avg_speed;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public double getKcal() {
        return kcal;
    }

    public void setKcal(double kcal) {
        this.kcal = kcal;
    }

    public Measure(boolean success, int mnum, String id, String image, int time, String start_time, String end_time, double avg_speed, double dist, double kcal) {
        this.success = success;
        this.mnum = mnum;
        this.id = id;
        this.image = image;
        this.time = time;
        this.start_time = start_time;
        this.end_time = end_time;
        this.avg_speed = avg_speed;
        this.dist = dist;
        this.kcal = kcal;
    }
}
