package com.dongyang.android.boda.User.Service;




import com.dongyang.android.boda.Introduction.Model.CheckSuccess;
import com.dongyang.android.boda.User.Model.Favorite;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FavoriteService {

    String FAVORITE_URL = "http://192.168.0.4/";

    @FormUrlEncoded
    @POST("boda/map/favorite.php")
    Call<List<Favorite>> getFavorite(
            @Field("command") String command,
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("boda/map/favorite.php")
    Call<CheckSuccess> insertFavorite(
            @Field("command") String command,
            @Field("id") String id
    );
}
