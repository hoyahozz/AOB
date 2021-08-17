package com.dongyang.android.aob.Riding.Map.Model.Bike;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SeoulBike {

    @SerializedName("rentBikeStatus")
    @Expose
    private RentBikeStatus rentBikeStatus;

    public RentBikeStatus getRentBikeStatus() {
        return rentBikeStatus;
    }

    public void setRentBikeStatus(RentBikeStatus rentBikeStatus) {
        this.rentBikeStatus = rentBikeStatus;
    }
}
