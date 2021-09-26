package com.dongyang.android.aob.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongyang.android.aob.Introduction.Activity.LoginActivity;
import com.dongyang.android.aob.Introduction.Model.CheckSuccess;
import com.dongyang.android.aob.Introduction.UserProfileDialog;
import com.dongyang.android.aob.LoadingDialog;
import com.dongyang.android.aob.Main.MainActivity;
import com.dongyang.android.aob.R;
import com.dongyang.android.aob.User.Service.ChangeService;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserChangeActivity extends AppCompatActivity {

    private Button logout, quit, update;
    private EditText et_name, et_number, et_email, et_sos;
    private CardView cv_img;
    private TextView tv_id, tv_check;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ImageView iv_profile;
    private Toolbar toolbar;
    private Dialog userProfileDialog, loadingDialog;
    private String id, number, email, sos, name;
    private int userImage = 1;
    private AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_change);

        toolbar = findViewById(R.id.edit_toolbar);
        logout = findViewById(R.id.edit_logout);
        quit = findViewById(R.id.edit_quit);
        update = findViewById(R.id.edit_update);
        et_name = findViewById(R.id.edit_name);
        et_number = findViewById(R.id.edit_number);
        et_email = findViewById(R.id.edit_email);
        et_sos = findViewById(R.id.edit_sos);
        tv_id = findViewById(R.id.edit_id);
        tv_check = findViewById(R.id.edit_check);
        cv_img = findViewById(R.id.edit_user_cardView);
        iv_profile = findViewById(R.id.edit_user_profile);
        loadingDialog = new LoadingDialog(this);
        dialog = new AlertDialog.Builder(this);

        // 툴바 생성
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        editor = pref.edit();

        name = pref.getString("name", "");
        number = pref.getString("number", "");
        email = pref.getString("email", "");
        sos = pref.getString("sos", "");
        id = pref.getString("id", "오류");
        userImage = pref.getInt("image", 1);



        tv_id.setText(id);
        et_name.setText(name);
        et_number.setText(number);
        et_email.setText(email);
        et_sos.setText(sos);
        setUserImage(userImage);

        cv_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userProfileDialog = new UserProfileDialog(UserChangeActivity.this, new UserProfileDialog.Select() {
                    @Override
                    public void clickProfile(int userImg) {
                        userImage = userImg;
                    }
                }, iv_profile);
                userProfileDialog.show();
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.setMessage("정말 탈퇴하시겠어요?");

                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loadingDialog.show();
                        quit();
                    }
                });
                dialog.show();

            }
        });

        update.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View view) {

                String u_name = et_name.getText().toString();
                String u_email = et_email.getText().toString();
                String u_number = et_number.getText().toString();
                String u_sos = et_sos.getText().toString();

                if (u_name.equals("") || u_email.equals("") || u_number.equals("") || u_sos.equals("")) {
                    tv_check.setVisibility(View.VISIBLE);
                } else {
                    dialog.setMessage("수정시 현재 측정중인 기록이 사라집니다. 수정하시겠습니까?");

                    dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadingDialog.show();
                            update(u_name,u_number,u_email,u_sos);
                        }
                    });
                    dialog.show();
                }
            }
        });
    }

    // 메뉴별로 역할 지정
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 버튼 눌렀을 때 종료
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
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
        Intent intent = new Intent(UserChangeActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // 상위 액티비티 모두 종료
        startActivity(intent);
    }

    public void setUserImage(int userImg) {

        switch (userImg) {
            case 1:
                iv_profile.setImageResource(R.drawable.ic_profile_female_green);
                break;
            case 2:
                iv_profile.setImageResource(R.drawable.ic_profile_male_green);
                break;
            case 3:
                iv_profile.setImageResource(R.drawable.ic_profile_female_blue);
                break;
            case 4:
                iv_profile.setImageResource(R.drawable.ic_profile_male_blue);
                break;
            case 5:
                iv_profile.setImageResource(R.drawable.ic_profile_female_purple);
                break;
            case 6:
                iv_profile.setImageResource(R.drawable.ic_profile_male_purple);
                break;
            default:
                iv_profile.setImageResource(R.drawable.ic_profile_female_green);
                break;
        }
    }

    public void update(String u_name, String u_number, String u_email, String u_sos) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ChangeService.CHANGE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ChangeService changeAPI = retrofit.create(ChangeService.class);

        changeAPI.updateUser(id,userImage,u_name,u_number,u_email,u_sos).enqueue(new Callback<CheckSuccess>() {
            @Override
            public void onResponse(Call<CheckSuccess> call, Response<CheckSuccess> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) { // 성공적으로 받아왔을 때

                    editor.putInt("image",userImage);
                    editor.putString("name",u_name);
                    editor.putString("number",u_number);
                    editor.putString("email",u_email);
                    editor.putString("sos",u_sos);
                    editor.commit(); // 세션 영역에 해당 유저의 정보를 넣음

                    editor.commit();
                    Intent intent = new Intent(UserChangeActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CheckSuccess> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(getApplicationContext(), "서버 네트워크가 닫혀있습니다.", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    public void quit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ChangeService.CHANGE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ChangeService changeAPI = retrofit.create(ChangeService.class);

        changeAPI.deleteUser(id).enqueue(new Callback<CheckSuccess>() {
            @Override
            public void onResponse(Call<CheckSuccess> call, Response<CheckSuccess> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) { // 성공적으로 받아왔을 때
                    editor.clear();
                    editor.commit();
                    Intent intent = new Intent(UserChangeActivity.this, LoginActivity.class); // 로그인 화면으로
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "탈퇴되었습니다.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CheckSuccess> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(getApplicationContext(), "서버 네트워크가 닫혀있습니다.", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }
}