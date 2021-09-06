package com.dongyang.android.aob.Map.Service;


import com.dongyang.android.aob.Map.Model.Bike.SeoulBike;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BikeService {

    String SEOUL_URL = "http://openapi.seoul.go.kr:8088/";

    @GET("{apikey}/json/bikeList/{start}/{end}/")
    Call<SeoulBike> getBike(
            @Path("apikey") String apikey,
            @Path("start") int start,
            @Path("end") int end
            );
}
