package com.dongyang.android.aob.Main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

;

import com.dongyang.android.aob.R;
import com.dongyang.android.aob.Repair.RepairFragment;
import com.dongyang.android.aob.User.InfoActivity;
import com.dongyang.android.aob.VoiceChat.VoiceChatFragment;
import com.dongyang.android.aob.Riding.Map.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {


    private final int FRAGMENT_HOME = 1;
    private final int FRAGMENT_MAP = 2;
    private final int FRAGMENT_CHAT = 3;
    private final int FRAGMENT_REPAIR = 4;
    private BottomNavigationView main_bnv;
    private ImageView userImage;

//    Fragment homeFragment, mapFragment, voiceChatFragment, repairFragment;

    private SharedPreferences pref;
    private String userName, userId;
    private Bundle bundle;
    private CardView userInfo;
    private int value; // (0이면 메인으로 초기화면, 1이면 맵으로 초기화면 지정)
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        value = 0;

        main_bnv = findViewById(R.id.main_bnv);
        userInfo = findViewById(R.id.main_user_info);

        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });

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

        intent = getIntent();
        value = intent.getIntExtra("value", 0);

        FragmentView(value);
    }

    private void FragmentView(int value) {

        HomeFragment homeFragment = new HomeFragment();
        MapFragment ridingFragment = new MapFragment();
        VoiceChatFragment voiceChatFragment = new VoiceChatFragment();
        RepairFragment repairFragment = new RepairFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();


        // 초기 화면 설정
        if(value == 0) {
            fragmentManager.beginTransaction().replace(R.id.main_container, homeFragment, "home").commitAllowingStateLoss();
        } else {

            double f_lat = intent.getDoubleExtra("f_lat", 0);
            double f_long = intent.getDoubleExtra("f_long", 0);

            bundle.putDouble("favorite_lat", f_lat);
            bundle.putDouble("favorite_long", f_long);
            fragmentManager.beginTransaction().replace(R.id.main_container, ridingFragment, "riding").commitAllowingStateLoss();
            main_bnv.getMenu().findItem(R.id.navigation_ride).setChecked(true);
        }

        // 유저 정보 프래그먼트로 보내기
        homeFragment.setArguments(bundle);
        ridingFragment.setArguments(bundle);
        voiceChatFragment.setArguments(bundle);
        repairFragment.setArguments(bundle);

        main_bnv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                // 아이템을 생성할 때마다 transaction 을 따로 만들어주어 관리해야 함.

                switch (item.getItemId()) {
                    case R.id.navigation_home:

                        if(fragmentManager.findFragmentByTag("home") != null) {
                            // 프래그먼트가 존재한다면 보여주기
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("home")).commit();
                        } else { // 프래그먼트가 존재하지 않는다면 추가
                            fragmentManager.beginTransaction().add(R.id.main_container, homeFragment, "home").commit();
                        }

                        // 다른 프래그먼트가 보일땐 가리기
                        if(fragmentManager.findFragmentByTag("riding") != null) {
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("riding")).commit();
                        }

                        if(fragmentManager.findFragmentByTag("voiceChat") != null) {
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("voiceChat")).commit();
                        }

                        if(fragmentManager.findFragmentByTag("repair") != null) {
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("repair")).commit();
                        }
                        
                        return true;

                    case R.id.navigation_ride:

                        if(fragmentManager.findFragmentByTag("riding") != null) {
                            // 프래그먼트가 존재한다면 보여주기
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("riding")).commit();
                        } else {
                            fragmentManager.beginTransaction().add(R.id.main_container, ridingFragment, "riding").commit();
                        }

                        if(fragmentManager.findFragmentByTag("home") != null) {
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("home")).commit();
                        }

                        if(fragmentManager.findFragmentByTag("voiceChat") != null) {
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("voiceChat")).commit();
                        }

                        if(fragmentManager.findFragmentByTag("repair") != null) {
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("repair")).commit();
                        }

                        return true;
                    case R.id.navigation_voice_chat:

                        if(fragmentManager.findFragmentByTag("voiceChat") != null) {
                            // 프래그먼트가 존재한다면 보여주기
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("voiceChat")).commit();
                        } else {
                            fragmentManager.beginTransaction().add(R.id.main_container, voiceChatFragment, "voiceChat").commit();
                        }

                        if(fragmentManager.findFragmentByTag("riding") != null) {
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("riding")).commit();
                        }

                        if(fragmentManager.findFragmentByTag("home") != null) {
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("home")).commit();
                        }

                        if(fragmentManager.findFragmentByTag("repair") != null) {
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("repair")).commit();
                        }

                        return true;
                    case R.id.navigation_repair:

                        if(fragmentManager.findFragmentByTag("repair") != null) {
                            // 프래그먼트가 존재한다면 보여주기
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("repair")).commit();
                        } else {
                            fragmentManager.beginTransaction().add(R.id.main_container, repairFragment, "repair").commit();
                        }

                        if(fragmentManager.findFragmentByTag("riding") != null) {
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("riding")).commit();
                        }

                        if(fragmentManager.findFragmentByTag("voiceChat") != null) {
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("voiceChat")).commit();
                        }

                        if(fragmentManager.findFragmentByTag("home") != null) {
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("home")).commit();
                        }

                        return true;
                    default: return false;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_profile:
                Intent intent = new Intent(this, InfoActivity.class);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void replaceFragment() { // 홈 프래그먼트에서 버튼을 눌렀을 때 맵으로 이동할 수 있게끔 설정
        main_bnv.getMenu().findItem(R.id.navigation_ride).setChecked(true);
        MapFragment ridingFragment = new MapFragment();
        bundle.putBoolean("bikeON", true);
        ridingFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, ridingFragment, "riding").commit();
    }
}