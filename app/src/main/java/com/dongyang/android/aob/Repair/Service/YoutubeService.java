package com.dongyang.android.aob.Repair.Service;

import com.dongyang.android.aob.Repair.Model.GetAPI.YoutubeAPI;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YoutubeService {

    String YOUTUBE_URL = "https://www.googleapis.com/";

    @GET("youtube/v3/search")
    Call<YoutubeAPI> getList(
            @Query("key") String key,
            @Query("part") String part,
            @Query("q") String q,
            @Query("type") String type,
            @Query("maxResults") int maxResults
            );
}
