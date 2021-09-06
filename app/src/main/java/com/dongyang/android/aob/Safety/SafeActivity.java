package com.dongyang.android.aob.Safety;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.dongyang.android.aob.R;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class SafeActivity extends AppCompatActivity {
    int flag=0;//사용자가 버튼을 눌렀는지 확인하는 플래그변수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe);
        Button btnY = (Button)findViewById(R.id.btnY);
        Button btnN = (Button)findViewById(R.id.btnN);
        btnY.setOnClickListener(new View.OnClickListener(){//넘어졌을 경우
            @Override
            public void onClick(View v) {
                flag=1;//버튼 누름
                Intent intent = new Intent(getApplicationContext(), tcp.class);
                intent.putExtra("button", "btnY");

                startService(intent);//tcp 서비스에 전달
                finish();
            }
        });
        btnN.setOnClickListener(new View.OnClickListener(){//넘어지지 않았을경우
            @Override
            public void onClick(View v) {
                flag=1;//버튼 누름
                //Toast.makeText(getApplicationContext(),"안넘어짐",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), tcp.class);
                intent.putExtra("button", "btnN");
                startService(intent);
                finish();
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (flag == 0) {//눌린 버튼이 없을때(사용자가 버튼을 안눌렀을때)
                    Intent intent = new Intent(getApplicationContext(), tcp.class);
                    intent.putExtra("button", "btnY");
                    startService(intent);
                    finish();
                }
            }
        }, 10000);//10초 기다렸다가 꺼짐
        flag=0;
        Log.d("flag", String.valueOf(flag));
    }

    @Override
    public void onBackPressed() {//메시지 화면에서 뒤로가기 눌렀을때
        flag=1;//버튼 누름으로 판단
        Intent intent = new Intent(getApplicationContext(), tcp.class);
        intent.putExtra("button", "btnN");
        startService(intent);
        super.onBackPressed();
    }
}