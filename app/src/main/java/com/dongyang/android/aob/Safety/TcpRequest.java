package com.dongyang.android.aob.Safety;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class TcpRequest extends StringRequest {

    // URL 서버 설정 (PhP연결)
    final static private String URL = "http://122.32.165.55/Message2.php";
    private final Map<String, String> map;

    public TcpRequest(String id, Response.Listener<String> listener) {
        super(Method.POST,URL,listener,null);

        map = new HashMap<>();
        map.put("id",id);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Log.d("tcpRequest", String.valueOf(map));
        return map;
    }
}
