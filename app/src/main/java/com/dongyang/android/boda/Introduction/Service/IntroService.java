package com.dongyang.android.boda.Introduction.Service;

import com.dongyang.android.boda.Introduction.Model.CheckSuccess;
import com.dongyang.android.boda.Introduction.Model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IntroService {

    String INTRO_URL = "http://192.168.0.4/";

    @FormUrlEncoded
    @POST("boda/intro/register.php")
    Call<CheckSuccess> signUp(
            @Field("id") String id,
            @Field("pw") String pw,
            @Field("name") String name,
            @Field("number") String number,
            @Field("email") String email,
            @Field("sos") String sos
    );

    @FormUrlEncoded
    @POST("boda/intro/login.php")
    Call<User> login(
            @Field("id") String id,
            @Field("pw") String pw
    );

    @FormUrlEncoded
    @POST("boda/intro/idValidate.php")
    Call<CheckSuccess> idValidate(
            @Field("id") String id
    );
}
