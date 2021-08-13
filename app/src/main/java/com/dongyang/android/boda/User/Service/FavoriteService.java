package com.dongyang.android.boda.User.Service;




import com.dongyang.android.boda.Introduction.Model.CheckSuccess;
import com.dongyang.android.boda.User.Model.Favorite;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FavoriteService {

    String FAVORITE_URL = "http://192.168.0.4/";
    // String FAVORITE_URL = "http://122.32.165.55/"; // 라즈베리파이 서버


    // 로컬호스트

    @GET("boda/userinfo/favorite/custom-favorite.php")
    Call<List<Favorite>> getFavorite(
            @Query("id") String id
    );

    @FormUrlEncoded
    @POST("boda/userinfo/favorite/custom-favorite.php")
    Call<CheckSuccess> insertFavorite(
            @Field("id") String id,
            @Field("latitude") double latitude,
            @Field("longitude") double longitude,
            @Field("title") String title,
            @Field("content") String content
    );

    @DELETE("boda/userinfo/favorite/custom-favorite.php")
    Call<CheckSuccess> deleteFavorite(
            @Query("id") String id,
            @Query("fnum") int fnum
    );

    // 라즈베리파이 서버

//    @FormUrlEncoded
//    @POST("android/userinfo/favorite.php")
//    Call<List<Favorite>> getFavorite(
//            @Field("command") String command,
//            @Field("id") String id
//    );
//
//    @FormUrlEncoded
//    @POST("android/userinfo/favorite.php")
//    Call<CheckSuccess> insertFavorite(
//            @Field("command") String command,
//            @Field("id") String id
//    );

}
