package com.dongyang.android.boda.Main;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

;

import com.dongyang.android.boda.R;
import com.dongyang.android.boda.Repair.RepairFragment;
import com.dongyang.android.boda.VoiceChat.VoiceChatFragment;
import com.dongyang.android.boda.Map.MapFragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_bnv = findViewById(R.id.main_bnv);


        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FragmentView();
    }

    private void FragmentView() {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.main_container, new HomeFragment());
        transaction.commit();

        main_bnv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        HomeFragment homeFragment = new HomeFragment();
                        transaction.replace(R.id.main_container, homeFragment);
                        transaction.commit();
                        break;
                    case R.id.navigation_ride:
                        MapFragment mapFragment = new MapFragment();
                        transaction.replace(R.id.main_container, mapFragment);
                        transaction.commit();
                        break;
                    case R.id.navigation_voice_chat:
                        VoiceChatFragment voiceChatFragment = new VoiceChatFragment();
                        transaction.replace(R.id.main_container, voiceChatFragment);
                        transaction.commit();
                        break;
                    case R.id.navigation_repair :
                        RepairFragment repairFragment = new RepairFragment();
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