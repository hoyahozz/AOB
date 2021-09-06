package com.dongyang.android.aob.Repair;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dongyang.android.aob.BuildConfig;
import com.dongyang.android.aob.LoadingDialog;
import com.dongyang.android.aob.R;
import com.dongyang.android.aob.Repair.Adapter.YoutubeAdapter;
import com.dongyang.android.aob.Repair.Model.GetAPI.ItemsBean;
import com.dongyang.android.aob.Repair.Model.GetAPI.YoutubeAPI;
import com.dongyang.android.aob.Repair.Model.Youtube;
import com.dongyang.android.aob.Repair.Service.YoutubeService;
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

    private String MY_KEY = BuildConfig.GOOGLE_API_KEY; // API KEY
    private String RESULT; // 딥 러닝 서버에서 받아온 결과값
    private String Q; // 유튜브 검색값
    private int MAX_RESULTS = 30; // 받아올 유튜브 리스트의 최대값
    private LoadingDialog loadingDialog;


    private RecyclerView y_recyclerView; // 리사이클러뷰
    private YoutubeAdapter y_adapter; // 어댑터
    private List<Youtube> y_datas = new ArrayList<>(); // 출력 데이터를 담을 배열


    private Button y_searchButton; // 검색 버튼
    private EditText y_editText; // 입력한 검색어
    private String y_searchText; // 앞뒤 공백 없앤 검색어

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

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        searchTask(Q); // 딥러닝 출력 결과로 리스트 출력, 처음 리스트


        y_editText = (EditText) findViewById(R.id.youtube_editText); // 검색어를 입력하세요.
        y_searchButton = (Button) findViewById(R.id.youtube_searchButton); // 검색 버튼

        y_searchButton.setOnClickListener(new View.OnClickListener() { // 재검색
            @Override
            public void onClick(View v) {
                y_searchText = y_editText.getText().toString().trim(); // 공백만 넣었을 때 허용X

                if (y_searchText.getBytes().length != 0) { // 공백 아닐 때
                    y_datas.clear(); // 기존 리사이클러뷰 데이터 초기화
                    Q = y_searchText; // 검색어 갱신
                    searchTask(Q); // 재검색 키워드로 리스트 출력

                } else { // 공백일 때
                    Toast.makeText(getApplicationContext(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void searchTask(String q) { // 리스트 출력, q는 딥러닝 결과값 OR 재검색 키워드
        Gson gson = new GsonBuilder().setLenient().create(); // JSON 형식으로 데이터를 받을 때 필요
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YoutubeService.YOUTUBE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson)) // gson 부여
                .build();

        YoutubeService retrofitAPI = retrofit.create(YoutubeService.class); // 유튜브 서비스와 연결

        retrofitAPI.getList(MY_KEY,"snippet", q, "video", MAX_RESULTS).enqueue(new Callback<YoutubeAPI>() {
            // API KEY, snippet 영역을 받아옴, 검색값, 비디오만, MAX_RESULT 값 만큼 유튜브 서버에 요청
            @Override
            public void onResponse(Call<YoutubeAPI> call, Response<YoutubeAPI> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) { // 성공적으로 받아왔을 때
                    Log.d("YOUTUBE", "Response");
                    YoutubeAPI data = response.body(); // 받아온 데이터를 변수에 저장

                    List<ItemsBean> youtubeList = data.getItems(); // 변수에서 아이템 리스트들을 받아온다.

                    for(int i = 0; i < youtubeList.size(); i++) {
                        String title = youtubeList.get(i).getSnippet().getTitle(); // 타이틀 파싱
                        String url =  youtubeList.get(i).getSnippet().getThumbnails().getMedium().getUrl(); // 썸네일 url 파싱
                        String videoId = youtubeList.get(i).getId().getVideoId(); // videoId 파싱, 영상 재생시 필요

                        y_datas.add(new Youtube(title, url, videoId)); // 필요한 데이터만 추가
                        y_adapter = new YoutubeAdapter(YoutubeActivity.this, y_datas); // 어댑터 설정
                        y_recyclerView.setAdapter(y_adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<YoutubeAPI> call, Throwable t) {
                loadingDialog.dismiss();
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