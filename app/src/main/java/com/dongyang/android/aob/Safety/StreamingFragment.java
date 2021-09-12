package com.dongyang.android.aob.Safety;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import com.dongyang.android.aob.Main.MainActivity;
import com.dongyang.android.aob.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StreamingFragment extends Fragment {
    Button date;
    Button btnmain;
    WebView web;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View streamingLayout = inflater.inflate(R.layout.fragment_streaming, container,false);


        btnmain = streamingLayout.findViewById(R.id.btnmain);
        web = (WebView) streamingLayout.findViewById(R.id.webView);
        web.setWebViewClient(new TCWebViewClient()); // TCWebViewClient 클래스를 생성하여 웹뷰에 대입

        // WebSettings 클래스를 이용하여 줌 버튼 컨트롤이 화면에 보이게 함
        WebSettings webSet = web.getSettings();
        webSet.setBuiltInZoomControls(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setSupportZoom(true);
        web.getSettings().setDisplayZoomControls(false);



//        btnmain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent5 = new Intent(getActivity(), MainActivity.class);
//                startActivity(intent5);
//            }
//            //main activity로 이동
//        });
        return streamingLayout;
    }

    // WebViewClient를 상속받아 자신의 WebViewClient인 TCWebViewClient 클래스를 정의
    class TCWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}

