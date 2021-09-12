package com.dongyang.android.aob.Map.Model.Measurement;

public class Measure {

    private boolean success;
    private int mnum;
    private String id;
    private String image;
    private int time;
    private double dist;
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

    public Measure(boolean success, int mnum, String id, String image, int time, double dist, double kcal) {
        this.success = success;
        this.mnum = mnum;
        this.id = id;
        this.image = image;
        this.time = time;
        this.dist = dist;
        this.kcal = kcal;
    }
}
