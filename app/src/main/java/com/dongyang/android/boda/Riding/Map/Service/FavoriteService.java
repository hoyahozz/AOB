package com.dongyang.android.boda.Riding.Map.Service;




import com.dongyang.android.boda.Riding.Map.Model.Favorite.Favorite;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FavoriteService {

    String FAVORITE_URL = "http://172.16.100.129/";

    @FormUrlEncoded
    @POST("boda/map/favorite.php")
    Call<List<Favorite>> postData(
            @Field("id") String id
    );
}
