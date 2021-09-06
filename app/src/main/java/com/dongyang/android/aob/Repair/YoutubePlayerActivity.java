package com.dongyang.android.aob.Repair;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.dongyang.android.aob.BuildConfig;
import com.dongyang.android.aob.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubePlayerActivity
        extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener { // https://developers.google.com/youtube/android?hl=ko
    private YouTubePlayerView y_playerview;
    private YouTubePlayer y_player;
    private String MY_KEY = BuildConfig.GOOGLE_API_KEY; // API KEY

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_play);

        y_playerview = (YouTubePlayerView) findViewById(R.id.youtube_playerview);
        y_playerview.initialize(MY_KEY, this);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) { // player 초기화 실패
        Log.d("YoutubePlay", "Initialization Fail. error: " + youTubeInitializationResult);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) { // player 초기화 성공
        y_player = youTubePlayer;

        Intent intent = getIntent();
        y_player.loadVideo(intent.getStringExtra("videoId")); // videoId에 맞는 비디오 재생
    }
}