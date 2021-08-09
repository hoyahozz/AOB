package com.dongyang.android.boda.Riding.Map.Service;


import com.dongyang.android.boda.Riding.Map.Model.Bike.SeoulBike;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BikeService {

    @GET("{apikey}/json/bikeList/1/500/")
    Call<SeoulBike> getBike(@Path
    ("apikey") String apikey);
}
