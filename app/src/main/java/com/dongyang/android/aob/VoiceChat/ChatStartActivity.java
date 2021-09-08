package com.dongyang.android.aob.VoiceChat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.dongyang.android.aob.R;
import com.dongyang.android.aob.VoiceChat.Model.ConstantApp;

public class ChatStartActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_chat); //레이아웃 설정
    }


    @Override
    protected void initUIandEvent() { //ui와 event 초기화
        EditText v_channel = (EditText) findViewById(R.id.channel_name); //채널이름 입력받는 곳
        v_channel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //채널 이름이 입력되기 전
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //채널 이름 입력되는 중
            }

            @Override
            public void afterTextChanged(Editable s) {  //채널 이름이 입력된 후
                boolean isEmpty = TextUtils.isEmpty(s.toString()); //채널이름이 비어있는지를 확인
                findViewById(R.id.button_join).setEnabled(!isEmpty); //안비어있으면 조인버튼 활성화
            }
        });

        String lastChannelName = vSettings().mChannelName; //최종적으로 정해진 채널이름인가보다
        if (!TextUtils.isEmpty(lastChannelName)) {
            v_channel.setText(lastChannelName);
            v_channel.setSelection(lastChannelName.length());
        }
    }

    @Override
    protected void deInitUIandEvent() { //ui와 event를 deInit 하는곳
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onClickJoin(View view) {
        forwardToRoom();
    }

    public void forwardToRoom() { //방에 입장이잖아;
        EditText v_channel = (EditText) findViewById(R.id.channel_name);
        String channel = v_channel.getText().toString();
        vSettings().mChannelName = channel;

        Intent i = new Intent(ChatStartActivity.this, ChatActivity.class);
        i.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, channel);

        startActivity(i);
    }
}
