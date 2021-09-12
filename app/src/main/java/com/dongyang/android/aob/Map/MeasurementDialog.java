package com.dongyang.android.aob.Map;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dongyang.android.aob.Introduction.Model.CheckSuccess;
import com.dongyang.android.aob.LoadingDialog;
import com.dongyang.android.aob.Map.Service.MeasureService;
import com.dongyang.android.aob.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 측정 다이얼로그 구성 클래스

public class MeasurementDialog extends Dialog {
    public MeasurementDialog(@NonNull Context context, Bitmap bitmap, String userId, int f_timer, double sum_dist, double kcal) {
        super(context);

        LoadingDialog loadingDialog;

        loadingDialog = new LoadingDialog(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 없애기
        setCancelable(false); // 외부 화면 터치, 뒤로가기를 눌러도 다어얼로그 종료 X
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 투명 배경화면으로
        setContentView(R.layout.dialog_map_measurement);

        Button insertButton = findViewById(R.id.dialog_measurement_insert);
        Button cancelButton = findViewById(R.id.dialog_measurement_cancel);
        ImageView mapImage = findViewById(R.id.dialog_measurement_image);
        String mapImage_String;

        if (bitmap != null) {
            mapImage.setImageBitmap(bitmap);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

//        mapImage_String = "&image="+ byteArrayToBinaryString(bytes);
        mapImage_String = Base64.encodeToString(bytes, Base64.DEFAULT);

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingDialog.show();

                Gson gson = new GsonBuilder().setLenient().create();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(MeasureService.MEASURE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                MeasureService retrofitAPI = retrofit.create(MeasureService.class);


                // 데이터베이스 저장
                retrofitAPI.insertMeasure(userId, mapImage_String, f_timer, sum_dist, 10.00).enqueue(new Callback<CheckSuccess>() {
                    @Override
                    public void onResponse(Call<CheckSuccess> call, retrofit2.Response<CheckSuccess> response) {
                        if (response.isSuccessful()) {
                            Log.d("Measurement", "Response");
                            loadingDialog.dismiss();
                            dismiss();
                        } else {
                            Toast.makeText(context, "오류 발생", Toast.LENGTH_SHORT);
                            loadingDialog.dismiss();
                            dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<CheckSuccess> call, Throwable t) {
                        Log.d("Measurement", "Not Response");
                        loadingDialog.dismiss();
                        t.printStackTrace();
                    }
                });
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


    }

    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }

    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }

}
