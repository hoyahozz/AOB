package com.dongyang.android.aob.User;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dongyang.android.aob.Introduction.Model.CheckSuccess;
import com.dongyang.android.aob.LoadingDialog;
import com.dongyang.android.aob.Map.Service.MeasureService;
import com.dongyang.android.aob.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 측정 다이얼로그 구성 클래스

public class InfoMeasurementDialog extends Dialog {
    public InfoMeasurementDialog(@NonNull Context context, Bitmap bitmap, int timer, String s_time, double avg_speed, double sum_dist, double kcal) {
        super(context);

        TextView tv_date, tv_dist, tv_speed, tv_time, tv_kcal;
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 없애기
        setCancelable(false); // 외부 화면 터치, 뒤로가기를 눌러도 다어얼로그 종료 X
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 투명 배경화면으로
        setContentView(R.layout.dialog_info_measurement);

        ImageView mapImage = findViewById(R.id.dialog_info_measurement_image);
        ImageButton cancelButton = findViewById(R.id.dialog_info_measurement_close);

        tv_date = findViewById(R.id.dialog_info_measurement_date);
        tv_dist = findViewById(R.id.dialog_info_measurement_dist);
        tv_speed = findViewById(R.id.dialog_info_measurement_speed);
        tv_time = findViewById(R.id.dialog_info_measurement_time);
        tv_kcal = findViewById(R.id.dialog_info_measurement_kcal);

        String tv_s_time = s_time.substring(0 , s_time.length() - 6);

        Log.d("Dialog ::", s_time);

        tv_date.setText(tv_s_time);
        tv_dist.setText(String.valueOf(sum_dist) + " km");
        tv_speed.setText(String.valueOf(avg_speed) + " km/h");
        tv_time.setText(String.valueOf(timer) + " 초");
        tv_kcal.setText(String.valueOf(kcal) + " kcal");



        String mapImage_String;

        if (bitmap != null) {
            mapImage.setImageBitmap(bitmap);
        }
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


    }
}
