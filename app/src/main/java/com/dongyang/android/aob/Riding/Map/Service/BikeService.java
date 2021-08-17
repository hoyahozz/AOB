package com.dongyang.android.aob.Riding.Map.Service;


import com.dongyang.android.aob.Riding.Map.Model.Bike.SeoulBike;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BikeService {

    String SEOUL_URL = "http://openapi.seoul.go.kr:8088/";

    @GET("{apikey}/json/bikeList/1/500/")
    Call<SeoulBike> getBike(@Path
    ("apikey") String apikey);
}
