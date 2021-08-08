package com.dongyang.android.boda.Map.Model.Bike;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class BikeItem implements ClusterItem {

    private final LatLng position;
    private final int rackTotCnt;
    private final String stationName;
    private final int parkingBikeTotCnt;

    public BikeItem(double lat, double lng, int rackTotCnt, String stationName, int parkingBikeTotCnt) {
        position = new LatLng(lat, lng);
        this.rackTotCnt = rackTotCnt;
        this.stationName = stationName;
        this.parkingBikeTotCnt = parkingBikeTotCnt;
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

}
