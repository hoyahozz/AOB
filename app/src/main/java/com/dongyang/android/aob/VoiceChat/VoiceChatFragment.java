package com.dongyang.android.aob.VoiceChat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dongyang.android.aob.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class VoiceChatFragment extends Fragment {

//    private Toolbar toolbar;
//    private BottomNavigationView bnv;
//    private String TAG = "VoiceChat";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        toolbar = getActivity().findViewById(R.id.main_toolbar);
//        bnv = getActivity().findViewById(R.id.main_bnv);
//
//        toolbar.setVisibility(View.GONE);
//        bnv.setVisibility(View.GONE);



        Handler hand = new Handler();

        hand.postDelayed(new Runnable() {

            @Override
            public void run() {
                try{
                    // TODO Auto-generated method stub
                    Intent i = new Intent( VoiceChatFragment.this.getActivity(), ChatStartActivity.class);
                    startActivity(i);

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.beginTransaction().remove(VoiceChatFragment.this).commit();
                    fm.popBackStack();

                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000); //0.01초 후 ChatStartActivity로 화면 전환

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_loading, container, false);

        /*@Override
        protected void onRestart(){
            super.onRestart();
            main_bnv.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
        }*/
    }
}
