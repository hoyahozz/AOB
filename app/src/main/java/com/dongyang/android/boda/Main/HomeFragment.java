package com.dongyang.android.boda.Main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.dongyang.android.boda.R;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {

    private View homeLayout;

    private TextView home_user_name;
    String userName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        homeLayout = inflater.inflate(R.layout.fragment_home, container, false);

        home_user_name = homeLayout.findViewById(R.id.home_user_name);

        // 메인 액티비티에서 입력한 값 받아오기
        if(getArguments() != null) {
            userName = getArguments().getString("userName");
            home_user_name.setText(userName + " 님!");
        }




        return homeLayout;
    }
}