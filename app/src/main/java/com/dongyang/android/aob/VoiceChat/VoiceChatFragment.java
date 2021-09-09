package com.dongyang.android.aob.VoiceChat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.dongyang.android.aob.R;


public class VoiceChatFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Handler hand = new Handler();

        hand.postDelayed(new Runnable() {

            @Override
            public void run() {
                try{
                    // TODO Auto-generated method stub
                    Intent i = new Intent(VoiceChatFragment.this.getActivity(), ChatStartActivity.class);
                    startActivity(i);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, 200); //1초 후 ChatStartActivity로 화면 전환

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_loading, container, false);
    }
}
