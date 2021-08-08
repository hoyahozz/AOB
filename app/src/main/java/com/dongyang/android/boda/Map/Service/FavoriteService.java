package com.dongyang.android.boda.Map.Service;




import com.dongyang.android.boda.Map.Model.Favorite.Favorite;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FavoriteService {

    String FAVORITE_URL = "http://192.168.0.4/";

    @FormUrlEncoded
    @POST("favorite.php")
    Call<List<Favorite>> postData(
            @Field("id") String id
    );
}
