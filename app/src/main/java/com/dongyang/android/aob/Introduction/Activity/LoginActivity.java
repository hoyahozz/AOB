package com.dongyang.android.aob.Introduction.Activity;

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

import com.dongyang.android.aob.Introduction.Model.User;
import com.dongyang.android.aob.Introduction.Service.IntroService;
import com.dongyang.android.aob.LoadingDialog;
import com.dongyang.android.aob.Main.MainActivity;
import com.dongyang.android.aob.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private Button login_register, login_find, login_button;
    private TextView login_id, login_pw, login_id_check, login_pw_check, login_check;
    private CheckBox login_stay_cb;
    private SharedPreferences user_info_pref;
    private SharedPreferences.Editor user_info_editor;
    private LoadingDialog loadingDialog;
    private long backKeyPressedTime = 0;

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

//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);

                user_info_pref = getSharedPreferences("userInfo",MODE_PRIVATE); // 세션 영역에 저장할 유저 정보
                user_info_editor = user_info_pref.edit();


                String id = login_id.getText().toString();
                String pw = login_pw.getText().toString();

                login_id_check.setVisibility(View.GONE);
                login_pw_check.setVisibility(View.GONE);
                login_check.setVisibility(View.GONE);

                if (id.equals("") && pw.equals("")) { // 아이디, 비밀번호 둘 다 빈칸일 때
                    login_id_check.setVisibility(View.VISIBLE);
                } else if (id.equals("")) { // 아이디만 빈칸일 때
                    login_id_check.setVisibility(View.VISIBLE);
                } else if (pw.equals("")) { // 비밀번호만 빈칸일 때
                    login_pw_check.setVisibility(View.VISIBLE);
                } else {

                    loadingDialog.show();

                    // 데이터베이스 접속 및 확인
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(IntroService.INTRO_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    IntroService introAPI = retrofit.create(IntroService.class);

                    introAPI.login(id, pw).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            loadingDialog.dismiss();
                            if (response.isSuccessful()) { // 성공적으로 받아왔을 때
                                if (response.body().isSuccess() == true) { // 아이디, 비밀번호가 일치했을 때
                                    User userdata = response.body(); // 유저 데이터를 호출한 데이터베이스로부터 받아와 변수에 저장

                                    user_info_editor.putString("id",userdata.getId());
                                    user_info_editor.putString("pw",userdata.getPw());
                                    user_info_editor.putInt("image",userdata.getImage());
                                    user_info_editor.putString("name",userdata.getName());
                                    user_info_editor.putString("number",userdata.getNumber());
                                    user_info_editor.putString("email",userdata.getEmail());
                                    user_info_editor.putString("sos",userdata.getSos());
                                    user_info_editor.commit(); // 세션 영역에 해당 유저의 정보를 넣음


                                    if (login_stay_cb.isChecked()) { // 로그인 상태 유지가 체크되어 있다면
                                        user_info_editor.putBoolean("autoLogin",true); // 자동 로그인 여부를 true
                                        user_info_editor.commit();

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else { // 로그인 상태 유지 체크가 안되어 있다면
                                        user_info_editor.putBoolean("autoLogin",false); // 자동 로그인 여부를 false
                                        user_info_editor.commit();

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else { // 아이디, 비밀번호가 일치하지 않을 때
                                    login_check.setVisibility(View.VISIBLE); // 다시 확인하라는 텍스트뷰 출력
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            loadingDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "서버 네트워크가 닫혀있습니다.", Toast.LENGTH_LONG).show();
                            t.printStackTrace();
                        }
                    });

                }


            }
        });



    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현 시간과 비교
        // 마지막으로 뒤로가기 버튼을 누른 시간이 2초가 지났으면 안내메시지 출력

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로가기\' 버튼을 한번 더 누르면 종료돼요!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 마지막으로 뒤로가기 버튼 누른 시간이 2초가 지나지 않았으면 바로 종료
        // 즉, 뒤로가기 버튼 두 번을 눌러야 종료되는 메커니즘임

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
        }
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
        loadingDialog = new LoadingDialog(this);
    }
}