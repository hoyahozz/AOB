package com.dongyang.android.aob.Safety;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Locale;

public class tcp extends Service {
    String button;
    Socket socket;
    private GpsTracker gpsTracker;
    String id;

    @Override
    public void onCreate() {
        //생성되었을때 실행
        ServerThread thread = new ServerThread();
        thread.start();
        Log.d("onCreate", "in onCreate");
        SharedPreferences setting = getSharedPreferences("userInfo", MODE_PRIVATE);
        id = setting.getString("id", "");
        Log.d("iddddd",id);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //호출될때마다 실행

        button = null;
//        try {
//            //loginActivity에서 id 받아오기
//            if (!intent.getStringExtra("id").isEmpty()) {//값이 있을때
//                id = intent.getStringExtra("id");
//                Log.d("tcp-id", id);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            button = intent.getStringExtra("button"); //message에서 버튼을 눌렀을 때
            Log.d("onButton",button);
            if (button.equals("btnY")) {
                //문자 보내기
                gpsTracker = new GpsTracker(getApplicationContext());

                double latitude = gpsTracker.getLatitude();//위도
                double longitude = gpsTracker.getLongitude();//경도
                String address = getCurrentAddress(latitude, longitude);//한글주소


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    String emCol;

                    @Override
                    public void onResponse(String response) {
                        Log.d("tcp", "in response");
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("Array", String.valueOf(jsonObject));
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                emCol = jsonObject.getString("sos");
                                String sendMessage = "https://www.google.com/maps/place/" + latitude + "," + longitude;
                                SmsManager sms = SmsManager.getDefault();
                                sms.sendTextMessage(emCol, null, sendMessage, null, null);

                                Log.d("address latitude", String.valueOf(latitude));
                                Log.d("address longitude", String.valueOf(longitude));
                                Log.d("address", address);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                TcpRequest tRequest = new TcpRequest(id, responseListener);
                RequestQueue queue = Volley.newRequestQueue(com.dongyang.android.aob.Safety.tcp.this);
                queue.add(tRequest);
            }
            OutputPrint thread = new OutputPrint();
            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    //GPS 도로명 주소로 변환
    public String getCurrentAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";

    }

    class ServerThread extends Thread {

        public void run() {
            try {
                int port = 9001;
                ServerSocket server = new ServerSocket(port);
                while (true) {

                    Log.d("waiting", "waiting accept");
                    socket = server.accept();
                    Log.d("socket.accept", String.valueOf(socket));
                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String readValue = reader.readLine();
                    if (readValue != null) {
                        Log.d("readValue", "in readValue()");
                        if (readValue.equals("fallen")) {//넘어졌을때
                            Intent intent = new Intent(getApplicationContext(), SafeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            //Thread.sleep(10000);//10초 기다림
                        }
                    }

                }
            } catch (BindException e) {
                e.printStackTrace();
                Log.d("BindException", "BindException");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class OutputPrint extends Thread {

        @Override
        public void run() {
            // OutputStream - 서버에서 클라이언트로 메세지 보내기
            OutputStream out = null;
            try {
                out = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(out, true);
                writer.println(button);
                socket.close();
                //Log.d("button",button);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}