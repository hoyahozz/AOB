package com.dongyang.android.aob.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dongyang.android.aob.LoadingDialog;
import com.dongyang.android.aob.R;
import com.dongyang.android.aob.User.Adapter.Decoration.FavoriteDecoration;
import com.dongyang.android.aob.User.Adapter.FavoriteAdapter;
import com.dongyang.android.aob.User.Model.Favorite;
import com.dongyang.android.aob.User.Service.FavoriteService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView f_recyclerView;
    private FavoriteAdapter f_adapter;
    private SwipeRefreshLayout f_refresh;
    private List<Favorite> f_datas = new ArrayList<>();
    private SharedPreferences pref;
    private String userName, userId;
    FavoriteDecoration favoriteDecoration;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Toolbar toolbar = findViewById(R.id.favorite_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        // 세션 영역에서 유저 이름 받아오기
        pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        userName = pref.getString("name", "김이엘").toString();
        userId = pref.getString("id","").toString();

        f_recyclerView = findViewById(R.id.favorite_recyclerView);
        f_refresh = findViewById(R.id.favorite_swipe);
        favoriteDecoration = new FavoriteDecoration(30); // 아이템 간 간격 지정

        f_recyclerView.setHasFixedSize(true);
        f_recyclerView.addItemDecoration(new DividerItemDecoration(FavoriteActivity.this, DividerItemDecoration.VERTICAL)); // 구분선 지정
        // f_recyclerView.addItemDecoration(favoriteDecoration); // 간격 지정
        f_recyclerView.setLayoutManager(new LinearLayoutManager(FavoriteActivity.this));
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        getFavorite();

        // Refresh 했을 때 반응
        f_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                f_datas.clear();
                getFavorite();
                f_refresh.setRefreshing(false); // 새로고침을 멈춥니다.
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

    public void getFavorite() {

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FavoriteService.FAVORITE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        FavoriteService retrofitAPI = retrofit.create(FavoriteService.class);

        retrofitAPI.getFavorite(userId).enqueue(new Callback<List<Favorite>>() {
            @Override
            public void onResponse(Call<List<Favorite>> call, Response<List<Favorite>> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) { // 성공적으로 받아왔을 때
                    Log.d("Favorite", "Response");
                    List<Favorite> data = response.body();

                    for(int i = 0; i < data.size(); i ++) {

                        int fnum = data.get(i).getFnum();
                        String id = data.get(i).getId();
                        String title = data.get(i).getTitle();
                        String content = data.get(i).getContent();
                        Double latitude = data.get(i).getLatitude();
                        Double longitude = data.get(i).getLongitude();

                        f_datas.add(new Favorite(
                                fnum,
                                id,
                                latitude,
                                longitude,
                                title,
                                content));
                    }
                    f_adapter = new FavoriteAdapter(FavoriteActivity.this, f_datas);
                    f_recyclerView.setAdapter(f_adapter);
                } else {
                    Toast.makeText(getApplicationContext(), "데이터가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<List<Favorite>> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(getApplicationContext(), "서버 네트워크가 닫혀있습니다.", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });

    }
}