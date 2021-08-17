package com.dongyang.android.aob.VoiceChat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voice_chat, container, false);
    }
//
////    @Override
////    protected void initUIandEvent() {
////        EditText v_channel = (EditText) findViewById(R.id.channel_name);
////        v_channel.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                boolean isEmpty = TextUtils.isEmpty(s.toString());
//                findViewById(R.id.button_join).setEnabled(!isEmpty);
//            }
//        });
//
//        String lastChannelName = vSettings().mChannelName;
//        if (!TextUtils.isEmpty(lastChannelName)) {
//            v_channel.setText(lastChannelName);
//            v_channel.setSelection(lastChannelName.length());
//        }
//    }
//
//    @Override
//    protected void deInitUIandEvent() {
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(final Menu menu) {
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }
//
//    public void onClickJoin(View view) {
//        forwardToRoom();
//    }
//
//    public void forwardToRoom() {
//        EditText v_channel = (EditText) findViewById(R.id.channel_name);
//        String channel = v_channel.getText().toString();
//        vSettings().mChannelName = channel;
//
//        Intent i = new Intent(MainActivity.this, ChatActivity.class);
//        i.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, channel);
//
//        startActivity(i);
//    }
}