package com.dongyang.android.aob.Map.Service;


import com.dongyang.android.aob.BuildConfig;
import com.dongyang.android.aob.Introduction.Model.CheckSuccess;
import com.dongyang.android.aob.Map.Model.Measurement.Measure;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MeasureService {

    String MEASURE_URL = BuildConfig.SERVER; // 라즈베리파이 서버

    @GET("android/userinfo/measure/measurement.php")
    Call<List<Measure>> getMeasure(
            @Query("id") String id
    );

    @FormUrlEncoded
    @POST("android/userinfo/measure/measurement.php")
    Call<CheckSuccess> insertMeasure(
            @Field("id") String id,
            @Field("image") String image,
            @Field("time") int time,
            @Field("start_time") String start_time,
            @Field("end_time") String end_time,
            @Field("avg_speed") double avg_speed,
            @Field("dist") double dist,
            @Field("kcal") double kcal
    );

    @DELETE("android/userinfo/measure/measurement.php")
    Call<CheckSuccess> deleteMeasure(
            @Query("mnum") int mnum,
            @Query("id") String id
    );
}
