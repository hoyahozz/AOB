package com.dongyang.android.aob.Main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dongyang.android.aob.R;
import com.dongyang.android.aob.Safety.GpsTracker;
import com.dongyang.android.aob.Weather.Model.Items;
import com.dongyang.android.aob.Weather.Model.WeatherAPI;
import com.dongyang.android.aob.Weather.Service.WeatherService;
import com.dongyang.android.aob.Weather.Weather;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeFragment extends Fragment {

    private View homeLayout;

    private TextView home_user_name;
    private Button home_measurement;
    String userName;

    // weather
    // 위치 관련
    private TransLocalPoint transLocalPoint;
    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    // 위도, 경도를 api에서 사용할 수 있는 격자로 변환 관련
    public static int TO_GRID = 0;
    public static int TO_GPS = 1;

    // 기상청 api 관련
    private String MY_KEY = "rsUhkN5/FhLY6KC2adYn9Cs35Bd8GVlQJ3UCUIyi8XS6UIwpqgqUrsI/fPNicSjsSVm7h37bGhYjIiazKzVFog==";
    private Integer PAGE_NO = 1;
    private Integer NUM_OF_ROWS = 33;
    private String TMP;
    private String SKY;
    private String PTY;
    private String POP;

    // 기상청 격자 좌표
    private int grid_x;
    private int grid_y;

    private List<Weather> w_data = new ArrayList<>();

    // 기상청 api 관련
    private String w_tmp; // 기온
    private String w_sky; // 하늘 상태
    private String w_pty; // 강수 형태
    private String w_pop; // 강수 확률
    private String address; // 현재 위치

    private TextView home_weatherText; // 흐림, 비
    private TextView home_weatherTextEx; // 흐림 : 달리기에 좋은 날씨네요, 비 : 오늘은 달리기 힘들것 같아요
    private TextView home_weatherLocation; // 서울 강서구
    private TextView home_weatherTemperature; // 기온
    private TextView home_weatherRain; // 강수확률
    private ImageView home_weatherImage; // ic_weather_cloudy, ic_weather_rainy
    private Button home_locationButton; // 현재 좌표 새로고침 버튼

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
        home_weatherText = homeLayout.findViewById(R.id.home_weatherText);
        home_weatherTextEx = homeLayout.findViewById(R.id.home_weatherTextEx);
        home_weatherLocation = homeLayout.findViewById(R.id.home_location);
        home_weatherTemperature = homeLayout.findViewById(R.id.home_temperature);
        home_weatherRain = homeLayout.findViewById(R.id.home_rain);
        home_weatherImage = homeLayout.findViewById(R.id.home_weatherImage);
        home_locationButton = homeLayout.findViewById(R.id.home_location_button);

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) { // 권한 부여가 완료됐을 때
            gpsCheckTask();
            weatherTask(grid_x, grid_y);
        } else {
            // 사용자가 위치를 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {
                Toast.makeText(getActivity(), "위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                //사용자게에 위치 요청
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else {
                // 위치기능 끈 경우
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        // 메인 액티비티에서 입력한 값 받아오기
        if (getArguments() != null) {
            userName = getArguments().getString("userName");
            home_user_name.setText(userName + " 님!");
        }


        home_measurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).replaceFragment();
            }
        });

        home_locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                w_data.clear();
                gpsCheckTask();
                weatherTask(grid_x, grid_y);
            }
        });


        return homeLayout;
    }

    public void weatherSet(String TMP, String SKY, String PTY, String POP, String ADDRESS) {
        if (PTY.equals("0")) { // 강수형태 0일 때 비X
            switch (SKY) {
                case "3": // 구름많음
                    home_weatherText.setText("구름많음");
                    home_weatherTextEx.setText("햇빛이 강하지 않아서 달리기 좋은 날씨예요!");
                    home_weatherImage.setImageResource(R.drawable.ic_weather_cloudy);
                    break;
                case "5": // 흐림
                    home_weatherText.setText("흐림");
                    home_weatherTextEx.setText("햇빛이 강하지 않아 달리기 좋은 날씨예요!");
                    home_weatherImage.setImageResource(R.drawable.ic_weather_cloudy);
                    break;
                default:
                    home_weatherText.setText("맑음");
                    home_weatherTextEx.setText("걱정없이 달려도 되겠어요!");
                    home_weatherImage.setImageResource(R.drawable.ic_weather_sunny);
                    break;
            }
        } else { // 비, 눈 등등
            home_weatherText.setText("비");
            home_weatherTextEx.setText("비가 오는 날씨라 달리기 힘들겠어요!");
            home_weatherImage.setImageResource(R.drawable.ic_weather_rainy);
        }
        home_weatherTemperature.setText(TMP); // 기온
        home_weatherRain.setText(POP); // 강수확률
        home_weatherLocation.setText(ADDRESS); // 주소
    }

    public void weatherTask(int GRID_X, int GRID_Y) {
        Date date = new Date();
        SimpleDateFormat today = new SimpleDateFormat("yyyyMMdd"); // 오늘 날짜
        SimpleDateFormat time = new SimpleDateFormat("HH"); // 현재 시간 분 단위는 상관X

        int w_date; // 오늘 날짜
        String w_time; // 현재 시간
        int i_date = Integer.parseInt(today.format(date)); // 00일 때 -1
        int i_time = Integer.parseInt(time.format(date)); // 0, 1, ..., 22, 23
        Log.d("now time", "현재 시간 : " + i_time + ", 오늘 날짜 : " + i_date);

        // 24시간 중 8개 시간만 저장, 1일 8회 2시, 5시, 8시, 11시, 14시, 17시, 20시, 23시 이외 시간대 NO_DATA
        // 23 -> 00, 01, 02 / 02 -> 03, 04, 05 / 05 -> 06, 07, 08 / 08 -> 09, 10, 11 / 11 -> 12, 13, 14 / 14 -> 15, 16, 17 / 17 -> 18, 19, 20 / 20 -> 21, 22, 23
        if (i_time == 0 || i_time == 1 || i_time == 2) {
            w_time = "2300"; // 23
            w_date = i_date - 1; // 하루 전
        } else if (i_time == 3 || i_time == 4 || i_time == 5) {
            w_time = "0200";
            w_date = i_date;
        } else if (i_time == 6 || i_time == 7 || i_time == 8) {
            w_time = "0500";
            w_date = i_date;
        } else if (i_time == 9 || i_time == 10 || i_time == 11) {
            w_time = "0800";
            w_date = i_date;
        } else if (i_time == 12 || i_time == 13 || i_time == 14) {
            w_time = "1100";
            w_date = i_date;
        } else if (i_time == 15 || i_time == 16 || i_time == 17) {
            w_time = "1400";
            w_date = i_date;
        } else if (i_time == 18 || i_time == 19 || i_time == 20) {
            w_time = "1700";
            w_date = i_date;
        } else {
            w_time = "2000";
            w_date = i_date;
        }
        Log.d("now", "time = " + w_time + ", date = " + w_date);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String s) {
                Log.e("", s);
            }
        });

        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherService.WEATHER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        WeatherService retrofitAPI = retrofit.create(WeatherService.class);
        retrofitAPI.getList(MY_KEY, PAGE_NO, NUM_OF_ROWS, "JSON", w_date, w_time, GRID_X, GRID_Y).enqueue(new Callback<WeatherAPI>() {
            @Override
            public void onResponse(Call<WeatherAPI> call, Response<WeatherAPI> response) {
                if (response.isSuccessful()) {
                    WeatherAPI data = response.body();

                    List<Items.Item> weatherList = data.getResponse().getBody().getItems().getItem();

                    for (int i = 0; i < 33; i++) {
                        if (i_time == 3 * i) { // 0, 3, 6, 9, ..., 21
                            for (int j = 0; j < 11; j++) {
                                String category = weatherList.get(j).getCategory();
                                switch (category) {
                                    case "TMP": // 1시간 기온
                                        TMP = weatherList.get(j).getFcstValue();
                                        break;
                                    case "SKY": // 하늘 상태
                                        SKY = weatherList.get(j).getFcstValue();
                                        break;
                                    case "PTY": // 강수 형태
                                        PTY = weatherList.get(j).getFcstValue();
                                        break;
                                    case "POP": // 강수 확률
                                        POP = weatherList.get(j).getFcstValue();
                                        break;
                                }
                            }
                        } else if (i_time == 3 * i - 2) { // 1, 4, 7, 10, ..., 22
                            for (int j = 11; j < 22; j++) {
                                String category = weatherList.get(j).getCategory();
                                switch (category) {
                                    case "TMP":
                                        TMP = weatherList.get(j).getFcstValue();
                                        break;
                                    case "SKY":
                                        SKY = weatherList.get(j).getFcstValue();
                                        break;
                                    case "PTY":
                                        PTY = weatherList.get(j).getFcstValue();
                                        break;
                                    case "POP":
                                        POP = weatherList.get(j).getFcstValue();
                                        break;
                                }
                            }
                        } else { // 2, 5, 8, ..., 23
                            for (int j = 22; j < 33; j++) {
                                String category = weatherList.get(j).getCategory();
                                switch (category) {
                                    case "TMP":
                                        TMP = weatherList.get(j).getFcstValue();
                                        break;
                                    case "SKY":
                                        SKY = weatherList.get(j).getFcstValue();
                                        break;
                                    case "PTY":
                                        PTY = weatherList.get(j).getFcstValue();
                                        break;
                                    case "POP":
                                        POP = weatherList.get(j).getFcstValue();
                                        break;
                                }
                            }
                        }
                        w_data.add(new Weather(TMP, SKY, PTY, POP));
                    }
                    w_tmp = w_data.get(0).getTMP();
                    w_sky = w_data.get(0).getSKY();
                    w_pty = w_data.get(0).getPTY();
                    w_pop = w_data.get(0).getPOP();

                    Log.d(">>", "tmp = " + w_tmp + ", sky = " + w_sky + ", pty = " + w_pty + ", pop = " + w_pop);
                    weatherSet(w_tmp, w_sky, w_pty, w_pop, address);
                } else {
                    Log.d("WEATHER", "Resoponse Fail.");
                }
            }

            @Override
            public void onFailure(Call<WeatherAPI> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    public void gpsCheckTask() {
        gpsTracker = new GpsTracker(getActivity());

        double latitude = gpsTracker.getLatitude(); //위도
        double longitude = gpsTracker.getLongitude(); //경도
        Log.e(">>", "latitude = " + latitude + ", longitude = " + longitude);

        // 변환 관련
        transLocalPoint = new TransLocalPoint();
        TransLocalPoint.LatXLngY tmp = transLocalPoint.convertGRID_GPS(TO_GRID, latitude, longitude);
        Log.e(">>", "x = " + tmp.x + ", y = " + tmp.y);

        grid_x = (int) tmp.x;
        grid_y = (int) tmp.y;
        Log.e(">>", "x = " + grid_x + ", y = " + grid_y);

        address = getCurrentAddress(latitude, longitude);
    }

    //지오코더 GPS를 주소로 변환
    private String getCurrentAddress(double latitude, double longitude) {
        String getAddress = null;

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        //주소목록 담기위한 List
        List<Address> list = null;

        try { // 주소불러오기 성공이면
            list = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException ioException) { //실패면
            Toast.makeText(getContext(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();

            return "지오코더 서비스 사용불가";
        }
        //
        //catch (IllegalAccessException illegalAccessException){
        // Toast.makeText(this,"잘못된 GPS 좌표",Toast.LENGTH_LONG).show();

        // return "잘못된 GPS 좌표";
        //}

        if (list == null) {
            Toast.makeText(getContext(), "주소오류", Toast.LENGTH_LONG).show();
            return "주소오류";
        }
        if (list.size() > 0) {
            Address addr = list.get(0);
            if (addr.getSubLocality() == null) {
                getAddress = addr.getAdminArea() + " " + addr.getLocality() + " " + addr.getThoroughfare(); //시(도), 구(시), 동 보여주기
            } else if (addr.getLocality() == null) {
                getAddress = addr.getAdminArea() + " " + addr.getSubLocality() + " " + addr.getThoroughfare(); //시(도), 구(시), 동 보여주기
            }
        }
        return getAddress;
    }
}