package com.dongyang.android.aob.Introduction.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.dongyang.android.aob.Introduction.Fragment.IdFindFragment;
import com.dongyang.android.aob.Introduction.Fragment.PwFindFragment;
import com.dongyang.android.aob.R;

public class FindActivity extends AppCompatActivity {

    private final int FRAGMENT_ID_FIND = 1;
    private final int FRAGMENT_PW_FIND = 2;
    private Button find_id , find_pw;
    private View find_id_line , find_pw_line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        Toolbar toolbar = findViewById(R.id.find_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        find_id = findViewById(R.id.find_id);
        find_id_line = findViewById(R.id.find_id_line);
        find_pw = findViewById(R.id.find_pw);
        find_pw_line = findViewById(R.id.find_pw_line);


        find_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                find_id.setTextColor(0xFF46b95b);
                find_id.setTypeface(find_pw.getTypeface(), Typeface.BOLD);
                find_id_line.setBackgroundColor(0xFF46b95b);
                find_pw.setTextColor(0xB35A6A72);
                find_pw.setTypeface(find_pw.getTypeface(), Typeface.NORMAL);
                find_pw_line.setBackgroundColor(0xB35A6A72);
                FragmentView(FRAGMENT_ID_FIND);
            }
        });

        find_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                find_id.setTextColor(0xB35A6A72);
                find_id.setTypeface(find_pw.getTypeface(), Typeface.NORMAL);
                find_id_line.setBackgroundColor(0xB35A6A72);
                find_pw.setTextColor(0xFF46b95b);
                find_pw.setTypeface(find_pw.getTypeface(), Typeface.BOLD);
                find_pw_line.setBackgroundColor(0xFF46b95b);
                FragmentView(FRAGMENT_PW_FIND);
            }
        });

        FragmentView(FRAGMENT_ID_FIND);

    }

    private void FragmentView(int fragment){

        // FragmentTransactiom를 이용해 프래그먼트를 사용
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment){
            case 1:
                // 아이디 찾기 프래그먼트 호출
                IdFindFragment idFindFragment = new IdFindFragment();
                transaction.replace(R.id.find_container, idFindFragment);
                transaction.commit();
                break;

            case 2:
                // 비밀번호 찾기 프래그먼트 호출
                PwFindFragment pwFindFragment = new PwFindFragment();
                transaction.replace(R.id.find_container, pwFindFragment);
                transaction.commit();
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}