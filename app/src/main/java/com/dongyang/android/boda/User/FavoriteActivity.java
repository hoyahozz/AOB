package com.dongyang.android.boda.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.dongyang.android.boda.R;
import com.dongyang.android.boda.User.Adapter.Decoration.FavoriteDecoration;
import com.dongyang.android.boda.User.Adapter.FavoriteAdapter;
import com.dongyang.android.boda.User.Model.Favorite;
import com.dongyang.android.boda.User.Service.FavoriteService;

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
    FavoriteDecoration favoriteDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Toolbar toolbar = findViewById(R.id.favorite_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        f_recyclerView = findViewById(R.id.favorite_recyclerView);
        f_refresh = findViewById(R.id.favorite_swipe);
        favoriteDecoration = new FavoriteDecoration(30); // 아이템 간 간격 지정

        f_recyclerView.setHasFixedSize(true);
        f_recyclerView.addItemDecoration(new DividerItemDecoration(FavoriteActivity.this, DividerItemDecoration.VERTICAL)); // 구분선 지정
        f_recyclerView.addItemDecoration(favoriteDecoration); // 간격 지정
        f_recyclerView.setLayoutManager(new LinearLayoutManager(FavoriteActivity.this));

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
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_profile:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getFavorite() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FavoriteService.FAVORITE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FavoriteService retrofitAPI = retrofit.create(FavoriteService.class);

        retrofitAPI.getFavorite("select","hoya").enqueue(new Callback<List<Favorite>>() {
            @Override
            public void onResponse(Call<List<Favorite>> call, Response<List<Favorite>> response) {
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
                }

            }

            @Override
            public void onFailure(Call<List<Favorite>> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }
}