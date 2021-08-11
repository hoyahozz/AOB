package com.dongyang.android.boda.Main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

;

import com.dongyang.android.boda.R;
import com.dongyang.android.boda.Repair.RepairFragment;
import com.dongyang.android.boda.VoiceChat.VoiceChatFragment;
import com.dongyang.android.boda.Riding.Map.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {


    private final int FRAGMENT_HOME = 1;
    private final int FRAGMENT_MAP = 2;
    private final int FRAGMENT_CHAT = 3;
    private final int FRAGMENT_REPAIR = 4;
    private BottomNavigationView main_bnv;
    private ImageView userImage;

    private SharedPreferences pref;
    private String userName, userId;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_bnv = findViewById(R.id.main_bnv);

        // 세션 영역에서 유저 이름 받아오기
        pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        userName = pref.getString("name", "김이엘").toString();
        userId = pref.getString("id","").toString();

        Log.d("login", userName);

        // 프래그먼트로 넘길 bundle 값 입력
        bundle = new Bundle();
        bundle.putString("userName", userName);
        bundle.putString("userId",userId);


        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FragmentView();
    }

    private void FragmentView() {

        HomeFragment homeFragment = new HomeFragment();
        MapFragment mapFragment = new MapFragment();
        VoiceChatFragment voiceChatFragment = new VoiceChatFragment();
        RepairFragment repairFragment = new RepairFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        homeFragment.setArguments(bundle);
        transaction.replace(R.id.main_container, homeFragment);
        transaction.commit();

        main_bnv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        homeFragment.setArguments(bundle);
                        transaction.replace(R.id.main_container, homeFragment);
                        transaction.commit();
                        break;
                    case R.id.navigation_ride:
                        mapFragment.setArguments(bundle);
                        transaction.replace(R.id.main_container, mapFragment);
                        transaction.commit();
                        break;
                    case R.id.navigation_voice_chat:
                        transaction.replace(R.id.main_container, voiceChatFragment);
                        transaction.commit();
                        break;
                    case R.id.navigation_repair:
                        repairFragment.setArguments(bundle);
                        transaction.replace(R.id.main_container, repairFragment);
                        transaction.commit();
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_profile:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}