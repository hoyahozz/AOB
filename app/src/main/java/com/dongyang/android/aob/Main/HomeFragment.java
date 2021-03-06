package com.dongyang.android.aob.Main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dongyang.android.aob.BuildConfig;
import com.dongyang.android.aob.Map.MapFragment;
import com.dongyang.android.aob.R;
import com.dongyang.android.aob.Safety.GpsTracker;
import com.dongyang.android.aob.Weather.Model.Items;
import com.dongyang.android.aob.Weather.Model.WeatherAPI;
import com.dongyang.android.aob.Weather.Service.WeatherService;
import com.dongyang.android.aob.Weather.Weather;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
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

import static android.content.Context.LOCATION_SERVICE;


public class HomeFragment extends Fragment {

    private View homeLayout;

    private TextView home_user_name;
    private Button home_measurement;
    String userName;

    // permission
    private int hasFineLocationPermission;
    private int hasCoarseLocationPermission;
    private FragmentManager fragmentManager;
    private BottomNavigationView main_bnv;

    // weather
    // ?????? ??????
    private TransLocalPoint transLocalPoint;
    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    // ??????, ????????? api?????? ????????? ??? ?????? ????????? ?????? ??????
    public static int TO_GRID = 0;
    public static int TO_GPS = 1;

    // ????????? api ??????
    private String MY_KEY = BuildConfig.WEAHTER_API_KEY;
    private Integer PAGE_NO = 1;
    private Integer NUM_OF_ROWS = 33;
    private String TMP;
    private String SKY;
    private String PTY;
    private String POP;


    // ????????? ?????? ??????
    private int grid_x;
    private int grid_y;

    private List<Weather> w_data = new ArrayList<>();

    // ????????? api ??????
    private String w_tmp; // ??????
    private String w_sky; // ?????? ??????
    private String w_pty; // ?????? ??????
    private String w_pop; // ?????? ??????
    private String address; // ?????? ??????

    private TextView home_weatherText; // ??????, ???
    private TextView home_weatherTextEx; // ?????? : ???????????? ?????? ????????????, ??? : ????????? ????????? ????????? ?????????
    private TextView home_weatherLocation; // ?????? ?????????
    private TextView home_weatherTemperature; // ??????
    private TextView home_weatherRain; // ????????????
    private ImageView home_weatherImage; // ic_weather_cloudy, ic_weather_rainy
    private Button home_locationButton; // ?????? ?????? ???????????? ??????

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

        hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);


        if (!checkLocationServicesStatus()) { // ?????? ????????? ????????? ??????
            showDialogForLocationServiceSetting();
        } else {

            if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                    hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) { // ?????? ????????? ???????????? ???
                gpsCheckTask();
                weatherTask(grid_x, grid_y);
            } else {
                // ???????????? ????????? ????????? ??? ?????? ?????? ????????????
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {
                    Toast.makeText(getActivity(), "?????? ?????? ????????? ???????????????.", Toast.LENGTH_LONG).show();
                    //??????????????? ?????? ??????
                    ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                } else {
                    // ???????????? ??? ??????
                    ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                }
            }
        }

        // ?????? ?????????????????? ????????? ??? ????????????
        if (getArguments() != null) {
            userName = getArguments().getString("userName");
            home_user_name.setText(userName + " ???!");
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

                if (!checkLocationServicesStatus()) { // ?????? ????????? ????????? ??????
                    showDialogForLocationServiceSetting();
                } else {

                    w_data.clear();
                    // ?????? ????????? ??????
                    hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
                    hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);

                    if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) { // ?????? ????????? ???????????? ???
                        gpsCheckTask();
                        weatherTask(grid_x, grid_y);
                    } else {
                        // ???????????? ????????? ????????? ??? ?????? ?????? ??????
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {
                            Snackbar.make(homeLayout, "?????? ????????? ??????????????? ?????? ????????? ????????????!", Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                                }
                            }).show();
                            // ?????? ????????? ????????? ?????? ?????? ??????
                        } else {
                            // ?????? ????????? ????????? ?????? ?????? ??????
                            Snackbar.make(homeLayout, "?????? ????????? ??????????????? ?????? ????????? ????????????!", Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                                }
                            }).show();
                        }
                    }
                }
            }
        });


        return homeLayout;
    }

    public void weatherSet(String TMP, String SKY, String PTY, String POP, String ADDRESS) {
        if (PTY.equals("0")) { // ???????????? 0??? ??? ???X
            switch (SKY) {
                case "3": // ????????????
                    home_weatherText.setText("????????????");
                    home_weatherTextEx.setText("????????? ????????? ????????? ????????? ?????? ????????????!");
                    home_weatherImage.setImageResource(R.drawable.ic_weather_cloudy);
                    break;
                case "5": // ??????
                    home_weatherText.setText("??????");
                    home_weatherTextEx.setText("????????? ????????? ?????? ????????? ?????? ????????????!");
                    home_weatherImage.setImageResource(R.drawable.ic_weather_cloudy);
                    break;
                default:
                    home_weatherText.setText("??????");
                    home_weatherTextEx.setText("???????????? ????????? ????????????!");
                    home_weatherImage.setImageResource(R.drawable.ic_weather_sunny);
                    break;
            }
        } else { // ???, ??? ??????
            home_weatherText.setText("???");
            home_weatherTextEx.setText("?????? ?????? ????????? ????????? ???????????????!");
            home_weatherImage.setImageResource(R.drawable.ic_weather_rainy);
        }
        home_weatherTemperature.setText(TMP); // ??????
        home_weatherRain.setText(POP); // ????????????
        home_weatherLocation.setText(ADDRESS); // ??????
    }

    public void weatherTask(int GRID_X, int GRID_Y) {
        Date date = new Date();
        SimpleDateFormat today = new SimpleDateFormat("yyyyMMdd"); // ?????? ??????
        SimpleDateFormat time = new SimpleDateFormat("HH"); // ?????? ?????? ??? ????????? ??????X

        int w_date; // ?????? ??????
        String w_time; // ?????? ??????
        int i_date = Integer.parseInt(today.format(date)); // 00??? ??? -1
        int i_time = Integer.parseInt(time.format(date)); // 0, 1, ..., 22, 23
        Log.d("now time", "?????? ?????? : " + i_time + ", ?????? ?????? : " + i_date);

        // 24?????? ??? 8??? ????????? ??????, 1??? 8??? 2???, 5???, 8???, 11???, 14???, 17???, 20???, 23??? ?????? ????????? NO_DATA
        // 23 -> 00, 01, 02 / 02 -> 03, 04, 05 / 05 -> 06, 07, 08 / 08 -> 09, 10, 11 / 11 -> 12, 13, 14 / 14 -> 15, 16, 17 / 17 -> 18, 19, 20 / 20 -> 21, 22, 23
        if (i_time == 0 || i_time == 1 || i_time == 2) {
            w_time = "2300"; // 23
            w_date = i_date - 1; // ?????? ???
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
                if (response.isSuccessful() && response.body() != null) {
                    WeatherAPI data = response.body();

                    List<Items.Item> weatherList;

                    if (data.getResponse().getBody().getItems().getItem() != null) {
                        weatherList = data.getResponse().getBody().getItems().getItem();

                        for (int i = 0; i < 33; i++) {
                            if (i_time == 3 * i) { // 0, 3, 6, 9, ..., 21
                                for (int j = 0; j < 11; j++) {
                                    String category = weatherList.get(j).getCategory();
                                    switch (category) {
                                        case "TMP": // 1?????? ??????
                                            TMP = weatherList.get(j).getFcstValue();
                                            break;
                                        case "SKY": // ?????? ??????
                                            SKY = weatherList.get(j).getFcstValue();
                                            break;
                                        case "PTY": // ?????? ??????
                                            PTY = weatherList.get(j).getFcstValue();
                                            break;
                                        case "POP": // ?????? ??????
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
                        Toast.makeText(getActivity(), "????????? ???????????? ?????? ????????? ?????????????????????. ?????? ??????????????????!", Toast.LENGTH_LONG).show();
                    }

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

        double latitude = gpsTracker.getLatitude(); //??????
        double longitude = gpsTracker.getLongitude(); //??????
        Log.e(">>", "latitude = " + latitude + ", longitude = " + longitude);

        // ?????? ??????
        transLocalPoint = new TransLocalPoint();
        TransLocalPoint.LatXLngY tmp = transLocalPoint.convertGRID_GPS(TO_GRID, latitude, longitude);
        Log.e(">>", "x = " + tmp.x + ", y = " + tmp.y);

        grid_x = (int) tmp.x;
        grid_y = (int) tmp.y;
        Log.e(">>", "x = " + grid_x + ", y = " + grid_y);

        address = getCurrentAddress(latitude, longitude);
    }

    //???????????? GPS??? ????????? ??????
    private String getCurrentAddress(double latitude, double longitude) {
        String getAddress = null;

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        //???????????? ???????????? List
        List<Address> list = null;

        try { // ?????????????????? ????????????
            list = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException ioException) { //?????????
            Toast.makeText(getContext(), "???????????? ????????? ????????????", Toast.LENGTH_LONG).show();

            return "???????????? ????????? ????????????";
        }
        //
        //catch (IllegalAccessException illegalAccessException){
        // Toast.makeText(this,"????????? GPS ??????",Toast.LENGTH_LONG).show();

        // return "????????? GPS ??????";
        //}

        if (list == null) {
            Toast.makeText(getContext(), "????????????", Toast.LENGTH_LONG).show();
            return "????????????";
        }
        if (list.size() > 0) {
            Address addr = list.get(0);
            if (addr.getSubLocality() == null) {
                getAddress = addr.getAdminArea() + " " + addr.getLocality() + " " + addr.getThoroughfare(); //???(???), ???(???), ??? ????????????
            } else if (addr.getLocality() == null) {
                getAddress = addr.getAdminArea() + " " + addr.getSubLocality() + " " + addr.getThoroughfare(); //???(???), ???(???), ??? ????????????
            }
        }
        return getAddress;
    }


    /*
        10.03
        ??? ??????????????? ?????? ????????? ????????? ??????
     */

    public boolean checkLocationServicesStatus() { // ???????????? ????????? ?????? ??? ????????? ?????? ??????
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        // ???????????? ????????? ?????? ??? ??????

        // ????????? ?????? ??????
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) // GPS??? ???????????????
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); // ??????????????? ????????? ??? ??????
    }


    private void showDialogForLocationServiceSetting() {
        AlertDialog dial = new AlertDialog.Builder(getActivity()).
                setMessage("?????? ???????????? ???????????? ????????? ?????? ???????????? ????????????! "
                        + "?????? ????????? ????????? ????????????????")
                .setCancelable(true)
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent callGPSSettingIntent
                                = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getActivity().startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
                    }
                }).setNegativeButton("??????", null).show();


        TextView dialTv = dial.findViewById(android.R.id.message);
        Button dialBtn = (Button) dial.getWindow().findViewById(android.R.id.button1);
        Button dialBtn2 = (Button) dial.getWindow().findViewById(android.R.id.button2);
        Typeface typeface = ResourcesCompat.getFont(getActivity(), R.font.nanum_square);
        dialTv.setTypeface(typeface);
        dialBtn.setTypeface(typeface);
        dialBtn2.setTypeface(typeface);
        dialBtn2.setTextColor(Color.BLACK);
    }

}