package com.dongyang.android.boda.Introduction.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dongyang.android.boda.Introduction.Model.CheckSuccess;
import com.dongyang.android.boda.Introduction.Model.User;
import com.dongyang.android.boda.Introduction.Service.IntroService;
import com.dongyang.android.boda.Main.MainActivity;
import com.dongyang.android.boda.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private String TAG = "Register";

    private Button register_button;
    private ImageButton register_id_validate, register_email_validate;
    private EditText register_id, register_pw, register_pw2, register_name,
            register_number, register_email, register_sos;
    private Toolbar toolbar;
    private TextView register_check, register_id_check, register_pw_check, register_validate_check,
            register_id_length_check, register_pw_length_check;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private int validate = 2; // 아이디 확인값, 확인을 아예 안했을 때 2, 확인은 했지만 맞지 않았을 때 1, 확인이 완료됐을 때 0
    private boolean idLengthCheck = false;
    private boolean pwLengthCheck = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById(); // 아이템 맞추기

        // 툴바 생성
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        // 아이디 중복 확인 버튼을 눌렀을 때
        register_id_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register_validate_check.setVisibility(View.GONE);
                register_id_check.setVisibility(View.GONE);

                String id = register_id.getText().toString(); // 아이디값을 받아옴

                if (id.equals("")) { // 아이디 입력값이 없을 때
                    register_id_check.setVisibility(View.VISIBLE);
                    register_id_check.setTextColor(Color.RED);
                    register_id_check.setText("아이디를 입력해주세요.");
                    validate = 1;
                } else {

                    Log.d(TAG, id);

                    // 데이터베이스 접속 및 확인
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(IntroService.INTRO_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    IntroService validateAPI = retrofit.create(IntroService.class);

                    validateAPI.idValidate(id).enqueue(new Callback<CheckSuccess>() {
                        @Override
                        public void onResponse(Call<CheckSuccess> call, Response<CheckSuccess> response) {
                            if (response.isSuccessful()) { // 성공적으로 받아왔을 때
                                if (response.body().isSuccess() == false) { // 중복되는 아이디가 없을 때
                                    register_id_validate.setImageResource(R.drawable.ic_register_check);
                                    validate = 0;
                                } else { // 중복되는 아이디가 있을 때
                                    register_id_validate.setImageResource(R.drawable.ic_register_not_check);
                                    register_id_check.setVisibility(View.VISIBLE);
                                    register_id_check.setTextColor(Color.RED);
                                    register_id_check.setText("존재하는 아이디입니다! 다른 아이디를 입력해주세요.");
                                    validate = 1;
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CheckSuccess> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "!", Toast.LENGTH_LONG).show();
                            t.printStackTrace();
                        }
                    });
                }
            }
        });

        // 회원가입 버튼을 눌렀을 때
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                register_check.setVisibility(View.GONE);
                register_pw_check.setVisibility(View.GONE);

                // 값을 모두 받아온다
                String id = register_id.getText().toString();
                String pw = register_pw.getText().toString();
                String pw2 = register_pw2.getText().toString();
                String name = register_name.getText().toString();
                String number = register_number.getText().toString();
                String email = register_email.getText().toString();
                String sos = register_sos.getText().toString();


                if (validate == 2) { // 아이디 체크를 완전히 하지 않았을 때
                    register_validate_check.setVisibility(View.VISIBLE);
                }

                // 빈 값이 존재할 경우
                if (id.equals("") || pw.equals("") || pw2.equals("") || name.equals("") || number.equals("") || email.equals("") || sos.equals("")) {
                    register_check.setVisibility(View.VISIBLE); // 값을 채워넣으라는 안내문 출력
                    return;
                } else { // 값을 모두 넣었을 때

                    // 비밀번호는 재확인 비밀번호와 일치하는지, 아이디 중복확인은 완전히 완료하였는지,
                    // 아이디, 비밀번호는 조건에 맞게끔 입력하였는지 검사 후 일치하면 데이터베이스를 받아오는 단계로 넘어감
                    if (pw.equals(pw2) && validate == 0) {

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(IntroService.INTRO_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        IntroService introAPI = retrofit.create(IntroService.class);

                        introAPI.signUp(id, pw, name, number, email, sos).enqueue(new Callback<CheckSuccess>() {
                            @Override
                            public void onResponse(Call<CheckSuccess> call, Response<CheckSuccess> response) {
                                Log.d("Register", "Success");
                                if (response.isSuccessful() && response.body() != null) { // 성공적으로 받아왔을 때
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class); // 로그인 화면으로
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onFailure(Call<CheckSuccess> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "알 수 없는 이유로 오류가 떴어요.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                t.printStackTrace();
                            }
                        });
                    }
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

    public void findViewById() {
        toolbar = findViewById(R.id.register_toolbar);
        register_button = findViewById(R.id.register_button);

        register_id_validate = findViewById(R.id.register_id_validate);

        register_id = findViewById(R.id.register_id);
        register_pw = findViewById(R.id.register_pw);
        register_pw2 = findViewById(R.id.register_pw2);
        register_name = findViewById(R.id.register_name);
        register_number = findViewById(R.id.register_number);
        register_email = findViewById(R.id.register_email);
        register_sos = findViewById(R.id.register_sos);
        register_check = findViewById(R.id.register_check);
        register_id_check = findViewById(R.id.register_id_check);
        register_pw_check = findViewById(R.id.register_pw_check);
        register_validate_check = findViewById(R.id.register_validate_check);
        register_id_length_check = findViewById(R.id.register_id_length_check);
        register_pw_length_check = findViewById(R.id.register_pw_length_check);

        register_name.setFilters(new InputFilter[]{textSetFilter("kor")}); // 한글만 나오게 설정

        builder = new AlertDialog.Builder(RegisterActivity.this);

        // 아이디 글자 수가 6자가 넘게끔 설정
        register_id.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (register_id.length() < 6) {
                        register_id_length_check.setTextColor(Color.RED);
                        idLengthCheck = false;
                    } else {
                        register_id_length_check.setTextColor(0xFF46b95b);
                        idLengthCheck = true;
                    }
                }
            }
        });

        // 비밀번호 영문 / 숫자 조합 설정
        Pattern VALID_PASSWOLD_REGEX_ALPHA_NUM = Pattern.compile("^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#$%^&*])(?=.*[0-9!@#$%^&*]).{8,16}$");
        // 8자리 ~ 16자리까지 가능

        register_pw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String pw = register_pw.getText().toString();
                Matcher matcher = VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(pw);
                boolean pwLengthCheck = matcher.matches();
                if (!hasFocus) {
                    if (pwLengthCheck == false) {
                        register_pw_length_check.setTextColor(Color.RED);
                    } else {
                        register_pw_length_check.setTextColor(0xFF46b95b);
                    }
                }
            }
        });

        register_pw2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String pw = register_pw.getText().toString();
                String pw2 = register_pw2.getText().toString();
                if (!hasFocus) {
                    if (!pw.equals(pw2)) {
                        register_pw_check.setTextColor(Color.RED);
                        register_pw_check.setText("비밀번호가 일치하지 않습니다! 다시 입력해주세요.");
                        register_pw_check.setVisibility(View.VISIBLE);
                    } else {
                        register_pw_check.setTextColor(0xFF46b95b);
                        register_pw_check.setText("비밀번호가 일치해요.");
                        register_pw_check.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


    }

    public InputFilter textSetFilter(String lang) {
        Pattern ps;
        if (lang.equals("kor")) {
            ps = Pattern.compile("^[ㄱ-ㅣ가-힣\\s]*$"); //한글 및 공백문자만 허용
        } else {
            ps = Pattern.compile("[a-zA-Z\\s-]*$"); //영어 및 하이픈 문자만 허용
        }
        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        };
        return filter;
    }
}

