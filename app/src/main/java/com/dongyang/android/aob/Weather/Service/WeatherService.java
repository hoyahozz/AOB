package com.dongyang.android.aob.Weather.Service;

import com.dongyang.android.aob.Weather.Model.WeatherAPI;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    String WEATHER_URL = "http://apis.data.go.kr/";

    @GET("1360000/VilageFcstInfoService_2.0/getVilageFcst")
    Call<WeatherAPI> getList(
            @Query("serviceKey") String serviceKey,
            @Query("pageNo") Integer pageNo,
            @Query("numOfRows") Integer numOfRows,
            @Query("dataType") String dataType,
            @Query("base_date") Integer base_date,
            @Query("base_time") String base_time,
            @Query("nx") Integer nx,
            @Query("ny") Integer ny
    );
}
