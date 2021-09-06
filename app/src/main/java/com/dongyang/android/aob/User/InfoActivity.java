package com.dongyang.android.aob.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dongyang.android.aob.Introduction.Activity.LoginActivity;
import com.dongyang.android.aob.R;
import com.dongyang.android.aob.Map.Model.Measurement.Measure;
import com.dongyang.android.aob.Map.Service.MeasureService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayInputStream;
import java.util.List;

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
    private String mapImg = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        userName = pref.getString("name", "김이엘").toString();
        userId = pref.getString("id", "").toString();

        // userId = "hoya";

        logout = findViewById(R.id.info_logout);
        toolbar = findViewById(R.id.info_toolbar);
        mapImage = findViewById(R.id.info_mapImage);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        editor = pref.edit();

        if (userId != "")
            setMapImage();

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

    public void setMapImage() {
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MeasureService.MEASURE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MeasureService retrofitAPI = retrofit.create(MeasureService.class);

        retrofitAPI.getMeasure(userId).enqueue(new Callback<List<Measure>>() {
            @Override
            public void onResponse(Call<List<Measure>> call, Response<List<Measure>> response) {
                // loadingDialog.dismiss();
                if (response.isSuccessful()) { // 성공적으로 받아왔을 때
                    Log.d("Measure", "Response");
                    List<Measure> data = response.body();

                    if (data.size() > 0) {
                        Log.d("Measure", String.valueOf(data.get(0).getMnum()));
                        Log.d("Measure", data.get(0).getId());
                        Log.d("Measure", String.valueOf(data.get(0).getTime()));
                        Log.d("Measure", String.valueOf(data.get(0).getDist()));
                        Log.d("Measure", String.valueOf(data.get(0).getKcal()));

                        mapImg = data.get(0).getImage();
                        if (mapImg != "") {
                            Log.d("Measure", "Img ON");
                        }
                        Bitmap bitmap = StringToBitMaps(mapImg);
                        mapImage.setImageBitmap(bitmap);
                        mapImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "데이터가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<List<Measure>> call, Throwable t) {
                // loadingDialog.dismiss();
                Toast.makeText(getApplicationContext(), "서버 네트워크가 닫혀있습니다.", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    public static byte[] binaryStringToByteArray(String s) {
        int count = s.length() / 8;
        byte[] b = new byte[count];
        for (int i = 1; i < count; ++i) {
            String t = s.substring((i - 1) * 8, i * 8);
            b[i - 1] = binaryStringToByte(t);
        }
        return b;
    }

    public static byte binaryStringToByte(String s) {
        byte ret = 0, total = 0;
        for (int i = 0; i < 8; ++i) {
            ret = (s.charAt(7 - i) == '1') ? (byte) (1 << i) : 0;
            total = (byte) (ret | total);
        }
        return total;
    }

    public static Bitmap StringToBitmap(String ImageString) {
        try {
            byte[] bytes = binaryStringToByteArray(ImageString);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            Bitmap bitmap = BitmapFactory.decodeStream(bais);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public Bitmap StringToBitMaps(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


}