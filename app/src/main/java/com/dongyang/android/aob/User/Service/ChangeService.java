package com.dongyang.android.aob.User.Service;


import com.dongyang.android.aob.BuildConfig;
import com.dongyang.android.aob.Introduction.Model.CheckSuccess;
import com.dongyang.android.aob.User.Model.Favorite;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChangeService {

    String CHANGE_URL = BuildConfig.SERVER; // 라즈베리파이 서버

    @FormUrlEncoded
    @POST("android/userinfo/change/userUpdate.php")
    Call<CheckSuccess> updateUser(
            @Field("id") String id,
            @Field("image") int image,
            @Field("name") String name,
            @Field("number") String number,
            @Field("email") String email,
            @Field("sos") String sos
    );

    @FormUrlEncoded
    @POST("android/userinfo/change/userDelete.php")
    Call<CheckSuccess> deleteUser(
            @Field("id") String id
    );

}
