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

public interface FavoriteService {

    String FAVORITE_URL = BuildConfig.SERVER; // 라즈베리파이 서버

    @GET("android/userinfo/favorite/custom-favorite.php")
    Call<List<Favorite>> getFavorite(
            @Query("id") String id
    );

    @FormUrlEncoded
    @POST("android/userinfo/favorite/custom-favorite.php")
    Call<CheckSuccess> insertFavorite(
            @Field("id") String id,
            @Field("latitude") double latitude,
            @Field("longitude") double longitude,
            @Field("title") String title,
            @Field("content") String content
    );

    @DELETE("android/userinfo/favorite/custom-favorite.php")
    Call<CheckSuccess> deleteFavorite(
            @Query("id") String id,
            @Query("fnum") int fnum
    );

}
