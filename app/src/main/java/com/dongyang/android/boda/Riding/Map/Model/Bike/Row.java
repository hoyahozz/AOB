package com.dongyang.android.boda.Riding.Map.Model.Bike;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

public class Row implements ClusterItem {
    @SerializedName("rackTotCnt")
    @Expose
    private int rackTotCnt;
    @SerializedName("stationName")
    @Expose
    private String stationName;
    @SerializedName("parkingBikeTotCnt")
    @Expose
    private int parkingBikeTotCnt;
    @SerializedName("shared")
    @Expose
    private int shared;
    @SerializedName("stationLatitude")
    @Expose
    private double stationLatitude;
    @SerializedName("stationLongitude")
    @Expose
    private double stationLongitude;
    @SerializedName("stationId")
    @Expose
    private String stationId;
    private LatLng BIKE_LOCATION;

    public Row(double lat, double lng) {
        BIKE_LOCATION = new LatLng(lat, lng);
    }

    public Row(int rackTotCnt, String stationName, int parkingBikeTotCnt, int shared, double stationLatitude, double stationLongitude, String stationId, LatLng BIKE_LOCATION) {
        this.rackTotCnt = rackTotCnt;
        this.stationName = stationName;
        this.parkingBikeTotCnt = parkingBikeTotCnt;
        this.shared = shared;
        this.stationLatitude = stationLatitude;
        this.stationLongitude = stationLongitude;
        this.stationId = stationId;
        this.BIKE_LOCATION = BIKE_LOCATION;
    }

    @Override
    public LatLng getPosition() {
        return BIKE_LOCATION;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }


    public int getRackTotCnt() {
        return rackTotCnt;
    }

    public void setRackTotCnt(int rackTotCnt) {
        this.rackTotCnt = rackTotCnt;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public int getParkingBikeTotCnt() {
        return parkingBikeTotCnt;
    }

    public void setParkingBikeTotCnt(int parkingBikeTotCnt) {
        this.parkingBikeTotCnt = parkingBikeTotCnt;
    }

    public int getShared() {
        return shared;
    }

    public void setShared(int shared) {
        this.shared = shared;
    }

    public double getStationLatitude() {
        return stationLatitude;
    }

    public void setStationLatitude(double stationLatitude) {
        this.stationLatitude = stationLatitude;
    }

    public double getStationLongitude() {
        return stationLongitude;
    }

    public void setStationLongitude(double stationLongitude) {
        this.stationLongitude = stationLongitude;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }





    @Override
    public String toString() {
        return "SeoulBike{" +
                "rackTotCnt='" + rackTotCnt + '\'' +
                ", stationName='" + stationName + '\'' +
                ", parkingBikeTotCnt='" + parkingBikeTotCnt + '\'' +
                ", shared='" + shared + '\'' +
                ", stationLatitude=" + stationLatitude +
                ", stationLongitude=" + stationLongitude +
                ", stationId='" + stationId + '\'' +
                '}';
    }
}
