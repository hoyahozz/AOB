package com.dongyang.android.aob.Map.Model.Bike;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class BikeItem implements ClusterItem {

    private final LatLng position;
    private final int rackTotCnt;
    private final String stationName;
    private final int parkingBikeTotCnt;
    private final Bitmap icon;

    public BikeItem(double lat, double lng, int rackTotCnt, String stationName, int parkingBikeTotCnt, Bitmap icon) {
        position = new LatLng(lat, lng);
        this.rackTotCnt = rackTotCnt;
        this.stationName = stationName;
        this.parkingBikeTotCnt = parkingBikeTotCnt;
        this.icon = icon;
    }

    @Override
    public LatLng getPosition() {
        return position;
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

    public String getStationName() {
        return stationName;
    }

    public int getParkingBikeTotCnt() {
        return parkingBikeTotCnt;
    }

    public Bitmap getIcon() {
        return icon;
    }
}
