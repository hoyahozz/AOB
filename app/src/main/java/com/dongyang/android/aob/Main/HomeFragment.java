package com.dongyang.android.aob.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.dongyang.android.aob.R;


public class HomeFragment extends Fragment {

    private View homeLayout;

    private TextView home_user_name;
    private Button home_measurement;
    String userName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        homeLayout = inflater.inflate(R.layout.fragment_home, container, false);
        home_measurement = homeLayout.findViewById(R.id.home_measurement);
        home_user_name = homeLayout.findViewById(R.id.home_user_name);

        // 메인 액티비티에서 입력한 값 받아오기
        if(getArguments() != null) {
            userName = getArguments().getString("userName");
            home_user_name.setText(userName + " 님!");
        }

        home_measurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragment();
            }
        });


        return homeLayout;
    }
}