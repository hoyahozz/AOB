package com.dongyang.android.aob;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.dongyang.android.aob.Introduction.Activity.LoginActivity;
import com.dongyang.android.aob.Main.MainActivity;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Boolean autoLogin = pref.getBoolean("autoLogin", false);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

//                 // 자동 로그인 여부 체크
//                if(autoLogin == false) {
//                    // 자동 로그인 하지 않았을 경우 로그인으로 이동하며 세션 영역 내 정보 삭제
//                    editor.clear();
//                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    // 자동 로그인 체크하였을 경우 바로 메인 액티비티로 이동
//                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2500);
    }
}