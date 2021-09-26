package com.dongyang.android.aob.Introduction.Service;

import com.dongyang.android.aob.BuildConfig;
import com.dongyang.android.aob.Introduction.Model.CheckSuccess;
import com.dongyang.android.aob.Introduction.Model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IntroService {

    String INTRO_URL = BuildConfig.SERVER; // 라즈베리파이 서버

    // 라즈베리파이 서버
    @FormUrlEncoded
    @POST("/android/intro/register.php")
    Call<CheckSuccess> signUp(
            @Field("id") String id,
            @Field("pw") String pw,
            @Field("image") int image,
            @Field("name") String name,
            @Field("number") String number,
            @Field("email") String email,
            @Field("sos") String sos
    );

    @GET("/android/intro/login.php")
    Call<User> login(
            @Query("id") String id,
            @Query("pw") String pw
    );

    @GET("/android/intro/idValidate.php")
    Call<CheckSuccess> idValidate(
            @Query("id") String id
    );
}
