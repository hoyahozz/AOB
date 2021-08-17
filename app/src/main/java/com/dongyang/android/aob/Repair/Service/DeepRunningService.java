package com.dongyang.android.aob.Repair.Service;

import com.dongyang.android.aob.BuildConfig;
import com.dongyang.android.aob.Repair.Model.Result;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface DeepRunningService {

    String SEND_URL = BuildConfig.DEEP_RUNNING_SERVER;

    @Multipart
    @POST("bicycle")
    Call<Result> sendImage(@Part MultipartBody.Part file);
}
