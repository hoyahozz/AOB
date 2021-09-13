package com.dongyang.android.aob.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dongyang.android.aob.Introduction.Activity.LoginActivity;
import com.dongyang.android.aob.LoadingDialog;
import com.dongyang.android.aob.R;
import com.dongyang.android.aob.Map.Model.Measurement.Measure;
import com.dongyang.android.aob.Map.Service.MeasureService;
import com.dongyang.android.aob.User.Adapter.MeasurementAdapter;
import com.dongyang.android.aob.User.Model.Favorite;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InfoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView user_profile;
    private CardView user_cardView;
    private String userName, userId;
    private Button logout;
    private SharedPreferences pref;
    private ImageView mapImage;
    private SharedPreferences.Editor editor;
    private Bitmap bitmap;
    private Dialog loadingDialog;
    private RecyclerView m_recyclerView;
    private List<Measure> m_datas = new ArrayList<>();
    private MeasurementAdapter m_adapter;

    private int sum_dist = 0, sum_count = 0, sum_kcal = 0, sum_time = 0;
    private TextView tv_sum_dist, tv_sum_count, tv_sum_kcal, tv_sum_time, tv_user_name, tv_measure_check;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        loadingDialog = new LoadingDialog(this);

        pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        userName = pref.getString("name", "김이엘").toString();
        userId = pref.getString("id", "").toString();

        // userId = "hoya";

        logout = findViewById(R.id.info_logout);
        toolbar = findViewById(R.id.info_toolbar);
        m_recyclerView = findViewById(R.id.info_recyclerView);
        tv_sum_dist = findViewById(R.id.info_sum_dist);
        tv_sum_count = findViewById(R.id.info_sum_count);
        tv_sum_kcal = findViewById(R.id.info_sum_kcal);
        tv_sum_time = findViewById(R.id.info_sum_time);
        tv_user_name = findViewById(R.id.info_user_name);
        tv_measure_check = findViewById(R.id.info_no_measure);

        m_recyclerView.setHasFixedSize(true);
        m_recyclerView.addItemDecoration(new DividerItemDecoration(InfoActivity.this, DividerItemDecoration.VERTICAL)); // 구분선 지정
        m_recyclerView.setLayoutManager(new LinearLayoutManager(InfoActivity.this));


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        setText();

        editor = pref.edit();

        if (userId != "") {
            loadingDialog.show();
            tv_user_name.setText(userName);
            setMeasurement();
        }




        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });



        /*
        user_cardView = findViewById(R.id.info_user_cardView);
        user_profile = findViewById(R.id.info_user_profile);

        // Load an image using Glide library
        Glide.with(getApplicationContext())
                .load(R.drawable.item_user_image)
                .into(user_profile);
         */


    }

    public void logout() {

        //*(9/8 추가) 사진 초기화 코드
        try {
            File file = new File("/data/user/0/com.dongyang.android.aob/files/capture");
            File[] flist = file.listFiles();
            //Toast.makeText(getApplicationContext(),"파일 삭제",Toast.LENGTH_LONG).show();
            for (int i = 0; i < flist.length; i++) {
                String fname = flist[i].getName();
                if (fname.equals("file" + ".jpg")) {
                    flist[i].delete();
                }
            }
        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_LONG).show();
        }

        editor.clear();
        editor.commit();
        Intent intent = new Intent(InfoActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // 상위 액티비티 모두 종료
        startActivity(intent);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 버튼 눌렀을 때 종료
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setMeasurement() {
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MeasureService.MEASURE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MeasureService retrofitAPI = retrofit.create(MeasureService.class);


        retrofitAPI.getMeasure(userId).enqueue(new Callback<List<Measure>>() {
            @Override
            public void onResponse(Call<List<Measure>> call, Response<List<Measure>> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) { // 성공적으로 받아왔을 때
                    Log.d("Measure", "Response");
                    List<Measure> data = response.body();

                    Log.d("size", String.valueOf(data.size()));

                    if (data.size() > 0) { // 데이터가 존재할 떄
                        sum_count = data.size();

                        for (int i = 0; i < data.size(); i++) {

                            boolean success = data.get(i).isSuccess();
                            int mnum = data.get(i).getMnum();
                            String id = data.get(i).getId();
                            String image = data.get(i).getImage();
                            double dist = data.get(i).getDist();
                            int time = data.get(i).getTime();
                            String start_time = data.get(i).getStart_time();
                            String end_time = data.get(i).getEnd_time();
                            double avg_speed = data.get(i).getAvg_speed();
                            double kcal = data.get(i).getKcal();


                            sum_dist += (int) data.get(i).getDist();
                            sum_time += data.get(i).getTime();
                            sum_kcal += data.get(i).getKcal();

                            m_datas.add(new Measure(
                                    success,
                                    mnum,
                                    id,
                                    image,
                                    time,
                                    start_time,
                                    end_time,
                                    avg_speed,
                                    dist,
                                    kcal
                            ));
                        }

                        m_adapter = new MeasurementAdapter(InfoActivity.this, m_datas);
                        m_recyclerView.setAdapter(m_adapter);

                        sum_time /= 60;

                        setText();
                        m_recyclerView.setVisibility(View.VISIBLE);
                        tv_measure_check.setVisibility(View.GONE);
                    } else {
                        m_recyclerView.setVisibility(View.GONE);
                        tv_measure_check.setVisibility(View.VISIBLE);
                    }

                } else {
                    m_recyclerView.setVisibility(View.GONE);
                    tv_measure_check.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "데이터가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<List<Measure>> call, Throwable t) {
                loadingDialog.dismiss();
                tv_measure_check.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "서버 네트워크가 닫혀있습니다.", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }




        });
    }

    public void setText() {
        tv_sum_dist.setText(sum_dist + " km");
        tv_sum_time.setText(sum_time + " 분");
        tv_sum_count.setText(sum_count + " 회");
        tv_sum_kcal.setText(sum_kcal + " kcal");

        tv_sum_dist.setVisibility(View.VISIBLE);
        tv_sum_time.setVisibility(View.VISIBLE);
        tv_sum_count.setVisibility(View.VISIBLE);
        tv_sum_kcal.setVisibility(View.VISIBLE);
    }
}