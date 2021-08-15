package com.dongyang.android.boda.Repair.Service;

import com.dongyang.android.boda.Repair.Model.Result;

import java.io.File;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface SendService {

    String SEND_URL = "http://dmuel.duckdns.org";

    @Multipart
    @POST("/bicycle")
    Call<Result> sendImage(@Part MultipartBody.Part file);
}
