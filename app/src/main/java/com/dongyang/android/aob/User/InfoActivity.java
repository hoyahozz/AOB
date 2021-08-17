package com.dongyang.android.aob.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dongyang.android.aob.Introduction.Activity.LoginActivity;
import com.dongyang.android.aob.R;
import com.squareup.picasso.Picasso;

public class InfoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView user_profile;
    private CardView user_cardView;
    private Button logout;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        logout = findViewById(R.id.info_logout);
        toolbar = findViewById(R.id.info_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        editor = pref.edit();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });



        /*
        user_cardView = findViewById(R.id.info_user_cardView);
        user_profile = findViewById(R.id.info_user_profile);
        // Load an image using Picasso library
        Picasso.with(getApplicationContext())
                .load(R.drawable.item_user_image)
                .into(user_profile);

        // Load an image using Glide library
        Glide.with(getApplicationContext())
                .load(R.drawable.item_user_image)
                .into(user_profile);
         */
    }

    public void logout() {
        editor.clear();
        Intent intent = new Intent(InfoActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // 상위 액티비티 모두 종료
        startActivity(intent);
    }

}