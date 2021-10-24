package com.dongyang.android.aob.Safety;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

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
    ServerThread thread;

    ServerSocket server = null;
    private static final String TAG = "in_tcp";
    Handler handler;
    @Override
    public void onCreate() {
        //생성되었을때 실행
        thread = new ServerThread();
        thread.start();
        Log.d("onCreate", "in onCreate");
        SharedPreferences setting = getSharedPreferences("userInfo", MODE_PRIVATE);
        id = setting.getString("id", "");
        Log.d("iddddd",id);

        handler = new Handler();
        super.onCreate();
    }
    private void _runOnUiThread(Runnable runnable) {
        handler.post(runnable);
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
                int messagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
                if(messagePermission == PackageManager.PERMISSION_DENIED){//메세지 권한 없을때
                    Log.d(TAG, "permission");
                    _runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "메세지 권한이 없습니다.",Toast.LENGTH_LONG).show();
                        }
                    });

                }
                else{
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
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    };
                    TcpRequest tRequest = new TcpRequest(id, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(com.dongyang.android.aob.Safety.tcp.this);
                    queue.add(tRequest);
                }




            }
            //라떼로 값 전달
            OutputPrint outputPrint = new OutputPrint();
            outputPrint.start();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return super.onStartCommand(intent, flags, startId);
        //return Service.START_NOT_STICKY;//서비스 종료시 재시작 안함
    }

    @Override
    public void onDestroy() {

        thread.interrupt();//스레드 종료를 위해 인터럽트 발생
        try {
            socket.close();//readline에 있을때 종료하기위해
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            server.close();//accept에 있을때 종료하기위해
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.d(TAG, "tcp onDestroy");
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
            int port = 9001;
            try {
                server = new ServerSocket(port);
                while (true) {
                    Log.d("waiting", "waiting accept");
                    try {
                        if(Thread.interrupted()){
                            Log.d(TAG, "thread interrupted");
                            break;
                        }
                        socket = server.accept();//onDestroy시 서버소켓을 닫아서 exception나게 하고 thread interrupt 확인으로 넘어감

                        Log.d(TAG, "socket = " + String.valueOf(socket));
                        InputStream input = null;
                        input = socket.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                        String readValue = reader.readLine();
                        Log.d(TAG, "socket read : " + readValue);
                        if (readValue != null) {
                            if (readValue.equals("fallen")) {//넘어졌을때
                                Intent intent = new Intent(getApplicationContext(), SafeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                //Thread.sleep(10000);//10초 기다림
                            }
                        }
                        Thread.sleep(1);//인터럽트 확인을 위해 잠깐 멈춰야됨


                    } catch (IOException ioException) {//SocketException 포함
                        ioException.printStackTrace();


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(TAG, "serverthraed interrupted");
                        break;
                    }
                }//while
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }


        }
    }



    class OutputPrint extends Thread {

        @Override
        public void run() {
            // OutputStream - 서버에서 클라이언트로 메세지 보내기
            OutputStream out = null;
            try {
                Log.d(TAG, "output thread wait");
                //넘어지고 자전거 일으킬시간동안 라떼에 보내지 않음
                Thread.sleep(15000);

                out = socket.getOutputStream();
                if(out != null){
                    Log.d(TAG, "outsocket is not null");
                    PrintWriter writer = new PrintWriter(out, true);
                    writer.println(button);
                }
                else{
                    Log.d(TAG, "outsocket is null");
                }
                socket.close();
                //Log.d("button",button);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }


}