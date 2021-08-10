package com.dongyang.android.boda.Repair.Service;

import com.dongyang.android.boda.Introduction.Model.CheckSuccess;
import com.dongyang.android.boda.Introduction.Model.User;
import com.dongyang.android.boda.Repair.Model.Type;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SendService {

    String SEND_URL = "http://172.16.100.129/";

    @FormUrlEncoded
    @POST("bicyle")
    Call<Type> sendImage(
            @Field("image") String image
    );
}
