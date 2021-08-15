package com.dongyang.android.boda.Repair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dongyang.android.boda.R;
import com.dongyang.android.boda.Repair.Adapter.YoutubeAdapter;
import com.dongyang.android.boda.Repair.Model.GetAPI.ItemsBean;
import com.dongyang.android.boda.Repair.Model.GetAPI.YoutubeAPI;
import com.dongyang.android.boda.Repair.Model.Youtube;
import com.dongyang.android.boda.Repair.Service.YoutubeService;
import com.dongyang.android.boda.User.Adapter.Decoration.FavoriteDecoration;
import com.dongyang.android.boda.User.Adapter.FavoriteAdapter;
import com.dongyang.android.boda.User.FavoriteActivity;
import com.dongyang.android.boda.User.Model.Favorite;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class YoutubeActivity extends AppCompatActivity {

    private String MY_KEY = "AIzaSyBWkLZrAIKRiM4gwNqc0am8Jdy0ZO0OGRQ"; // API KEY
    private String RESULT; // 딥 러닝 서버에서 받아온 결과값
    private String Q; // 유튜브 검색값
    private int MAX_RESULTS = 30; // 받아올 유튜브 리스트의 최대값


    private RecyclerView y_recyclerView; // 리사이클러뷰
    private YoutubeAdapter y_adapter; // 어댑터
    private List<Youtube> y_datas = new ArrayList<>(); // 출력 데이터를 담을 배열


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        RESULT = getIntent().getStringExtra("result"); // 이전 프래그먼트(RepairFragment) 에서 받아온 결과값

        switch (RESULT) { // RESULT 값에 따라 검색값도 바뀌게 설정
            case "tire" :
                Q = "자전거 타이어 고치는 방법";
                break;
            case "saddle" :
                Q = "자전거 안장 교체";
                break;
            case "handle" :
                Q = "자전거 핸들 고치는 방법";
                break;
            case "chain" :
                Q = "자전거 체인 수리";
                break;
            default :
                Q = "자전거 타이어 수리";
                break;
        }


        // 리사이클러뷰 사이즈 지정 및 레이아웃 설정
        y_recyclerView = findViewById(R.id.youtube_recyclerView);
        y_recyclerView.setHasFixedSize(true);
        y_recyclerView.addItemDecoration(new DividerItemDecoration(YoutubeActivity.this, DividerItemDecoration.VERTICAL)); // 구분선 지정
        y_recyclerView.setLayoutManager(new LinearLayoutManager(YoutubeActivity.this));

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.youtube_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 타이틀 제거
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 활성화
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back); // 뒤로가기 아이콘 활성화

        Gson gson = new GsonBuilder().setLenient().create(); // JSON 형식으로 데이터를 받을 때 필요

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YoutubeService.YOUTUBE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson)) // gson 부여
                .build();

        YoutubeService retrofitAPI = retrofit.create(YoutubeService.class); // 유튜브 서비스와 연결

        retrofitAPI.getList(MY_KEY,"snippet", Q, "video", MAX_RESULTS).enqueue(new Callback<YoutubeAPI>() {
                        // API KEY, snippet 영역을 받아옴, 검색값, 비디오만, MAX_RESULT 값 만큼 유튜브 서버에 요청
            @Override
            public void onResponse(Call<YoutubeAPI> call, Response<YoutubeAPI> response) {
                if (response.isSuccessful()) { // 성공적으로 받아왔을 때
                    Log.d("YOUTUBE", "Response");
                    YoutubeAPI data = response.body(); // 받아온 데이터를 변수에 저장

                    List<ItemsBean> youtubeList = data.getItems(); // 변수에서 아이템 리스트들을 받아온다.

                    for(int i = 0; i < youtubeList.size(); i++) {
                        String title = youtubeList.get(i).getSnippet().getTitle(); // 타이틀 파싱
                        String url =  youtubeList.get(i).getSnippet().getThumbnails().getMedium().getUrl(); // 썸네일 url 파싱

                        y_datas.add(new Youtube(title, url)); // 필요한 데이터만 추가
                        y_adapter = new YoutubeAdapter(YoutubeActivity.this, y_datas); // 어댑터 설정
                        y_recyclerView.setAdapter(y_adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<YoutubeAPI> call, Throwable t) {
                Log.d("YOUTUBE", "Response");
                t.printStackTrace();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}