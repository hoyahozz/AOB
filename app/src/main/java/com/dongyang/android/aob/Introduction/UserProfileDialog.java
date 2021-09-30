package com.dongyang.android.aob.Introduction;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.dongyang.android.aob.R;

// 유저 프로필 설정 다이얼로그



public class UserProfileDialog extends Dialog {

    CardView GreenFemale, GreenMale, BlueFemale, BlueMale, PurpleFemale, PurpleMale;
    int selectImage = 1;

    private Select select;

    public UserProfileDialog(@NonNull Context context, Select select, ImageView profile) {
        super(context);
        this.select = select;

        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 없애기
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 투명 배경화면으로
        setContentView(R.layout.dialog_user_image);

        selectProfile(profile);

    }

    public interface Select{
        void clickProfile(int userImg);
    }

    public void selectProfile(ImageView profile) {

        GreenFemale = findViewById(R.id.dialog_user_image_green_female);
        GreenMale = findViewById(R.id.dialog_user_image_green_male);
        BlueFemale = findViewById(R.id.dialog_user_image_blue_female);
        BlueMale = findViewById(R.id.dialog_user_image_blue_male);
        PurpleFemale = findViewById(R.id.dialog_user_image_purple_female);
        PurpleMale = findViewById(R.id.dialog_user_image_purple_male);

        GreenFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage = 1;
                select.clickProfile(selectImage);
                profile.setImageResource(R.drawable.ic_profile_female_green);
                dismiss();
            }
        });

        GreenMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage = 2;
                select.clickProfile(selectImage);
                profile.setImageResource(R.drawable.ic_profile_male_green);
                dismiss();
            }
        });

        BlueFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage = 3;
                select.clickProfile(selectImage);
                profile.setImageResource(R.drawable.ic_profile_female_blue);
                dismiss();
            }
        });

        BlueMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage = 4;
                select.clickProfile(selectImage);
                profile.setImageResource(R.drawable.ic_profile_male_blue);
                dismiss();
            }
        });

        PurpleFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage = 5;
                select.clickProfile(selectImage);
                profile.setImageResource(R.drawable.ic_profile_female_purple);
                dismiss();
            }
        });

        PurpleMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage = 6;
                select.clickProfile(selectImage);
                profile.setImageResource(R.drawable.ic_profile_male_purple);
                dismiss();
            }
        });

    }
}
