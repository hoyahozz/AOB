package com.dongyang.android.aob.VoiceChat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.dongyang.android.aob.R;


public class VoiceChatFragment extends Fragment {
    /*
    Menu menu;
    MenuItem item;
    View view;
     */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        ((BaseActivity) requireActivity()).InitUIandEvent_VF();
        ((BaseActivity)requireActivity()).deInitUIandEvent_VF();

        requireActivity().onCreateOptionsMenu(menu);
        requireActivity().onOptionsItemSelected(item);
        ((BaseActivity)requireActivity()).onClickJoin(view);
        ((BaseActivity)requireActivity()).forwardToRoom();
        */

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voice_chat, container, false);
    }

}
