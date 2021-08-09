package com.dongyang.android.boda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.dongyang.android.boda.Introduction.Activity.LoginActivity;
import com.dongyang.android.boda.Main.MainActivity;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences pref = getSharedPreferences("mine", MODE_PRIVATE);
        String id = pref.getString("id", "").toString();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(id == "") {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2500);
    }
}