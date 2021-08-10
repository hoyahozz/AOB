package com.dongyang.android.boda.Introduction.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.dongyang.android.boda.Introduction.Model.User;
import com.dongyang.android.boda.Introduction.Service.IntroService;
import com.dongyang.android.boda.Main.MainActivity;
import com.dongyang.android.boda.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    Button login_register, login_find, login_button;
    TextView login_id, login_pw, login_id_check, login_pw_check, login_check;
    CheckBox login_stay_cb;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(); // 아이템 맞추기

        // 회원가입 버튼
        login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 아이디, 비밀번호 찾기 버튼
        login_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, FindActivity.class);
                startActivity(intent);
            }
        });

        // 로그인 버튼
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

//                pref = getSharedPreferences("mine",MODE_PRIVATE); // 세션 영역에 유지할 때 사용
//                editor = pref.edit();
//
//                String id = login_id.getText().toString();
//                String pw = login_pw.getText().toString();
//
//                login_id_check.setVisibility(View.GONE);
//                login_pw_check.setVisibility(View.GONE);
//                login_check.setVisibility(View.GONE);
//
//                if (id.equals("") && pw.equals("")) { // 아이디, 비밀번호 둘 다 빈칸일 때
//                    login_id_check.setVisibility(View.VISIBLE);
//                } else if (id.equals("")) { // 아이디만 빈칸일 때
//                    login_id_check.setVisibility(View.VISIBLE);
//                } else if (pw.equals("")) { // 비밀번호만 빈칸일 때
//                    login_pw_check.setVisibility(View.VISIBLE);
//                } else {
//
//                    // 데이터베이스 접속 및 확인
//                    Retrofit retrofit = new Retrofit.Builder()
//                            .baseUrl(IntroService.INTRO_URL)
//                            .addConverterFactory(GsonConverterFactory.create())
//                            .build();
//
//                    IntroService introAPI = retrofit.create(IntroService.class);
//
//                    introAPI.login(id, pw).enqueue(new Callback<User>() {
//                        @Override
//                        public void onResponse(Call<User> call, Response<User> response) {
//                            if (response.isSuccessful()) { // 성공적으로 받아왔을 때
//                                if (response.body().isSuccess() == true) { // 아이디, 비밀번호가 일치했을 때
//                                    User userdata = response.body(); // 유저 데이터를 호출한 데이터베이스로부터 받아와 변수에 저장
//
//                                    if (login_stay_cb.isChecked()) { // 로그인 상태 유지가 체크되어 있다면
//                                        editor.putString("id",userdata.getId());
//                                        editor.putString("pw",userdata.getPw());
//                                        editor.putString("name",userdata.getName());
//                                        editor.putString("number",userdata.getNumber());
//                                        editor.putString("email",userdata.getEmail());
//                                        editor.putString("sos",userdata.getSos());
//                                        editor.commit(); // 세션 영역에 해당 유저의 정보를 넣음
//
//                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                        startActivity(intent);
//                                        finish();
//                                    } else { // 로그인 상태 유지 체크가 안되어 있다면
//                                        // 세션 영역에 유저의 정보를 넣을 필요가 없기 때문에 그대로 메인으로 넘어감
//                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                        startActivity(intent);
//                                        finish();
//                                    }
//                                } else { // 아이디, 비밀번호가 일치하지 않을 때
//                                    login_check.setVisibility(View.VISIBLE); // 다시 확인하라는 텍스트뷰 출력
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<User> call, Throwable t) {
//                            Toast.makeText(getApplicationContext(), "로그인 실패!", Toast.LENGTH_LONG).show();
//                            t.printStackTrace();
//                        }
//                    });
//
//                }


            }
        });

    }


    public void findViewById() {
        login_register = findViewById(R.id.login_register);
        login_find = findViewById(R.id.login_find);
        login_button = findViewById(R.id.login_button);
        login_id = findViewById(R.id.login_id);
        login_pw = findViewById(R.id.login_pw);
        login_id_check = findViewById(R.id.login_id_check);
        login_pw_check = findViewById(R.id.login_pw_check);
        login_check = findViewById(R.id.login_check);
        login_stay_cb = findViewById(R.id.login_stay_cb);
    }
}