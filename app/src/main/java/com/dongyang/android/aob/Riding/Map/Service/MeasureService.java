package com.dongyang.android.aob.Riding.Map.Service;


import com.dongyang.android.aob.BuildConfig;
import com.dongyang.android.aob.Introduction.Model.CheckSuccess;
import com.dongyang.android.aob.Riding.Map.Model.Bike.SeoulBike;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MeasureService {

    String MEASURE_URL = BuildConfig.SERVER; // 라즈베리파이 서버

    @FormUrlEncoded
    @POST("/android/userinfo/measure/measure.php")
    Call<CheckSuccess> insertMeasure(
            @Field("mnum") int mnum,
            @Field("id") String id,
            @Field("iamge") String image,
            @Field("time") String time,
            @Field("dist") String dist,
            @Field("kcal") double kcal
    );

    @DELETE("android/userinfo/measure/measure.php")
    Call<CheckSuccess> deleteMeasure(
            @Query("mnum") int mnum,
            @Query("id") String id
    );
}
