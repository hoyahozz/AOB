package com.dongyang.android.aob.Riding.Map;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dongyang.android.aob.BuildConfig;
import com.dongyang.android.aob.Introduction.Model.CheckSuccess;
import com.dongyang.android.aob.LoadingDialog;
import com.dongyang.android.aob.Riding.Map.Marker.BikeRenderer;
import com.dongyang.android.aob.Riding.Map.Model.Bike.BikeItem;
import com.dongyang.android.aob.Riding.Map.Model.Bike.RentBikeStatus;
import com.dongyang.android.aob.Riding.Map.Model.Bike.Row;
import com.dongyang.android.aob.Riding.Map.Model.Bike.SeoulBike;
import com.dongyang.android.aob.User.FavoriteActivity;
import com.dongyang.android.aob.User.Model.Favorite;
import com.dongyang.android.aob.Riding.Map.Model.Measurement.CalDistance;
import com.dongyang.android.aob.R;
import com.dongyang.android.aob.Riding.Map.Service.BikeService;
import com.dongyang.android.aob.User.Service.FavoriteService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment
        implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private String SEOUL_API_KEY = BuildConfig.SEOUL_API_KEY; // 서울 공공데이터 API 키

    private MapView mapView = null;
    private GoogleMap mMap;
    private Marker currentMarker = null; // 현재 위치를 나타낼 마커, 초기값은 NULL로 지정
    private Marker clickMarker = null; // 클릭 마커
    private Marker favoriteMarker = null; // 즐겨찾기 마커
    private Marker ridingStartMarker = null;
    private Marker ridingPauseMarker = null;
    private Marker ridingEndMarker = null;
    private ClusterManager<BikeItem> mClusterManager; // 클러스터 매니저, 커스텀 마커

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 3000;  // 3초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 2500; // 2.5초

    private Handler mHandler; // 소켓 통신 핸들러
    private Socket socket;
    private int port = 9999;                          // port 번호

    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


    Location mCurrentLocation; // 로케이션 객체로 현 위치 저장
    LatLng currentPosition; // 좌표 객체로 현 위치 저장


    private FusedLocationProviderClient mFusedLocationClient; // 위치 서비스 이용자(본인) 생성
    private LocationRequest locationRequest; // 위치를 요청하는 객체
    private Location location;
    private Location mLastlocation = null;


    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)
    private View bikeBottomSheet;
    private View bikeMeasurementBottomSheet;

    // 따릉이 커스텀 마커 설정
    private View bikeMarkerLayout;
    private TextView bike_detail;
    private int seoulBike = 0;
    private Button seoulBike_controller;

    private TextView bottomRackTotCnt, bottomParkingBikeTotCnt, bottomStationName, bike_distance, bike_avg_speed;
    private Chronometer bike_timer;

    private ImageButton bikeMeasurement, bikeMyLocation, bikeRefresh;
    private ImageButton bike_measurement_start, bike_measurement_stop, bike_measurement_reset;
    private AlertDialog.Builder dialog;
    private boolean bikeON = false;

    private Dialog favoriteDialog;
    private LoadingDialog loadingDialog;


    MarkerManager.Collection normalMarkerCollection;


    // 스탑 워치
    private boolean timeRunning;
    private long pauseOffset;

    // 거리 및 속도 측정
    private double bef_lat; // 이전 위도/경도
    private double bef_long;
    private double cur_lat; // 현재 위도/경도
    private double cur_long;
    private int sum_dist = 0; // 거리 합계
    private double avg_speed = 0; // 평균 속도
    private long timer = 0; // 시간
    private double calroie = 0; // 칼로리
    CalDistance calDistance;

    // 측정 후 데이터베이스에 담을 것들
    // 시작점
    private String s_lat = "";
    private String s_long = "";
    private String s_time = "";
    // 종료점
    private String f_lat = "";
    private String f_long = "";
    private String f_time = "";

    // 즐겨찾기
    private ImageButton bikeFavorite;

    // 유저 아이디를 담을 변수
    private String userId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mapLayout = inflater.inflate(R.layout.fragment_map, container, false);

        // 커스텀 마커 설정
        bikeMarkerLayout = inflater.inflate(R.layout.marker_seoul_bike, null);
        bike_detail = bikeMarkerLayout.findViewById(R.id.map_seoulBike_detail);

        mapView = (MapView) mapLayout.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mLayout = mapLayout.findViewById(R.id.mapLayout);

        bikeBottomSheet = mapLayout.findViewById(R.id.map_seoulBike_bottomSheet);
        bikeMeasurementBottomSheet = mapLayout.findViewById(R.id.map_measurement_bottomSheet);
        bikeMeasurement = (ImageButton) mapLayout.findViewById(R.id.map_measurement_btn);
        bikeMyLocation = mapLayout.findViewById(R.id.map_my_location_btn);
        bikeRefresh = mapLayout.findViewById(R.id.map_refresh_btn);
        bike_avg_speed = mapLayout.findViewById(R.id.map_avg_speed);
        bike_distance = mapLayout.findViewById(R.id.map_distance);
        bike_timer = mapLayout.findViewById(R.id.map_timer);
        bike_measurement_start = mapLayout.findViewById(R.id.map_measurement_start);
        bike_measurement_stop = mapLayout.findViewById(R.id.map_measurement_stop);
        bike_measurement_reset = mapLayout.findViewById(R.id.map_measurement_reset);
        bikeFavorite = mapLayout.findViewById(R.id.map_my_favortie_btn);
        bikeBottomSheet.setVisibility(View.GONE);
        loadingDialog = new LoadingDialog(this.getActivity());
        seoulBike_controller = mapLayout.findViewById(R.id.map_seoulBike_controller);

        locationRequest = new LocationRequest() // 위치를 요청한다
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // 정확도 조절
                .setInterval(UPDATE_INTERVAL_MS) // UPDATE_INTERVAL_MS 초마다 요청한다는 의미
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS); // 더 빠르게 위치 업데이트를 요청한다.

        // 위치 요청을 추가하는 부분
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        // 위치 서비스 이용자 mFusedLocationClient


        mapView.getMapAsync(this);
        // 지도 객체를 사용할 수 있을 때 onMapReady() 함수가 자동 호출되며 매개변수로 GoogleMap 객체가 전달된다.


        // 메인 액티비티로부터 유저 아이디를 받아온다.
        userId = getArguments().getString("userId");

        return mapLayout;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        loadingDialog.show();

        dialog = new AlertDialog.Builder(this.getActivity());

        mMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)

            startLocationUpdates(); // 3. 위치 업데이트 시작
            bikeMyLocation.setVisibility(View.VISIBLE);
            bikeMeasurement.setVisibility(View.VISIBLE);
            bikeFavorite.setVisibility(View.VISIBLE);
            bikeRefresh.setVisibility(View.VISIBLE);
            if (currentPosition != null) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPosition, 13);
                mMap.moveCamera(cameraUpdate);
            }


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // 3-3. 사용자에게 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        requestPermissions(REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);

//                        ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
//                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                requestPermissions(REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
//                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
//                        PERMISSIONS_REQUEST_CODE);
            }

        }


        mMap.getUiSettings().setMyLocationButtonEnabled(false); // 로케이션 버튼 활성화

        mClusterManager = new ClusterManager<BikeItem>(getActivity(), mMap); // 클러스터링 마커 설정
        mClusterManager.setRenderer(new BikeRenderer(getActivity(), mMap, mClusterManager));

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<BikeItem>() {
            @Override
            public boolean onClusterItemClick(BikeItem bikeItem) { // 클러스터 마커 클릭했을 때 이벤트
                bottomStationName = getView().findViewById(R.id.map_detail_stationName);

                bottomStationName.setText(bikeItem.getStationName());
                bikeBottomSheet.setVisibility(View.VISIBLE);
                if (clickMarker != null) {
                    clickMarker.remove();
                }
                return false;
            }
        });

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager.getMarkerManager());

        bikeMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bikeBottomSheet.setVisibility(View.GONE);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentPosition); // 현재 좌표 지정
                mMap.moveCamera(cameraUpdate); // 현재 좌표로 이동
            }
        });

        bikeFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FavoriteActivity.class);
                startActivity(intent);
            }
        });

        bikeRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                onMapReady(mMap);
            }
        });



        seoulBike_controller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (seoulBike == 0) {
                    seoulBike_controller.setTextColor(0xFF46b95b);
                    getBikeAPI();
                    setDefaultLocation();
                    seoulBike = 1;
                } else {
                    mClusterManager.clearItems();
                    seoulBike = 0;
                    seoulBike_controller.setTextColor(Color.BLACK);
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                }
            }
        });

        if (bikeON == false) { // ★ 라이딩 측정 모드가 꺼져있을 때만 다른 마커를 활성화시킨다. ★
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng point) {
                    bikeBottomSheet.setVisibility(View.GONE);
                    if (clickMarker != null) clickMarker.remove(); // 지난번 위치의 마커가 있다면 지운다.

//                    String markerTitle = getCurrentAddress(point);
//                    String markerSnippet = "위도:" + point.latitude
//                            + " 경도:" + String.valueOf(point.longitude);

                    MarkerOptions click = new MarkerOptions();
                    click.position(point);
                    click.title("클릭하면 즐겨찾는 장소로 저장이 가능해요!");
                    click.draggable(true);
                    click.icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.ic_map_click_marker)));
                    // clickMarker = mMap.addMarker(click);
                    clickMarker = normalMarkerCollection.addMarker(click);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(point); // 현재 좌표 지정
                    mMap.moveCamera(cameraUpdate); // 현재 좌표로 이동
                    Log.d(TAG, "onMapClick :");
                }
            });

            // getBikeAPI();
            getFavoriteLocation();


            normalMarkerCollection = mClusterManager.getMarkerManager().newCollection();
            normalMarkerCollection.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    if (marker.getTitle().equals("클릭하면 즐겨찾는 장소로 저장이 가능해요!")) {

                        // 즐겨찾기 커스텀 다이얼로그 설정
                        favoriteDialog = new Dialog(getActivity());
                        favoriteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
                        favoriteDialog.setContentView(R.layout.dialog_map_favorite); // 레이아웃 파일과 연결

                        favoriteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        favoriteDialog.show();
                        showFavoriteDialog();
                    }
                    return false;
                }
            });

        } else {
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng point) {
                    Log.d(TAG, "onMapClick :");
                }
            });
        }


        // 자전거 속도 측정 모드

        bikeMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bikeON == false) { // 측정 모드가 켜져있지 않을 때
                    dialog.setMessage("측정 모드를 실행하시겠습니까?");

                    dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // 기존 맵에 남아있는 마커 지우는 작업 실시
                            mMap.clear();
                            mClusterManager.clearItems();
                            bikeBottomSheet.setVisibility(View.GONE);

                            Animation animation = new AlphaAnimation(0, 1);
                            animation.setDuration(1000);

                            bikeON = true;
                            onMapReady(mMap);
                            bef_lat = mCurrentLocation.getLatitude();
                            bef_long = mCurrentLocation.getLongitude();
                            // 아이콘 변경
                            bikeMeasurement.setImageResource(R.drawable.ic_riding_on);
                            bikeMeasurementBottomSheet.setVisibility(View.VISIBLE);
                            bikeMeasurementBottomSheet.setAnimation(animation);
                            // 현 위치로 이동
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPosition, 15);
                            mMap.moveCamera(cameraUpdate);
                        }
                    });
                    dialog.show();
                } else { // 측정 모드가 켜져있을 때
                    dialog.setMessage("측정 모드를 종료하시겠습니까?");

                    dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mMap.clear();
                            bikeON = false;
                            onMapReady(mMap);
                            bikeMeasurement.setImageResource(R.drawable.ic_riding_off);
                            bikeMeasurementBottomSheet.setVisibility(View.GONE);
                        }
                    });
                    dialog.show();
                }
            }
        });

        // 측정 시작을 눌렀을 때
        bike_measurement_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!timeRunning) {
                    // 시간 설정
                    bike_timer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    bike_timer.start(); // 스탑 워치 시작
                    timeRunning = true; // 스탑 워치 작동 중
                    bike_measurement_start.setVisibility(View.GONE);
                    bike_measurement_stop.setVisibility(View.VISIBLE);
                    bike_measurement_reset.setEnabled(false);


                    // 거리 설정
                    MarkerOptions ridingStart = new MarkerOptions();
                    ridingStart.position(currentPosition);
                    ridingStart.title("기록 측정 시작지점");
                    ridingStart.icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.ic_map_riding_start_marker)));
                    ridingStartMarker = mMap.addMarker(ridingStart);


                    // 시작 지점 경도, 위도 설정
                    s_lat = String.valueOf(location.getLatitude());
                    s_long = String.valueOf(location.getLongitude());
                    // 기록 측정 시작 시간
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    s_time = sdfNow.format(date);

                    // 기록 측정은 locationCallback 참조

                }
            }
        });

        bike_measurement_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bike_timer.stop();

//                MarkerOptions ridingPause = new MarkerOptions();
//                ridingPause.position(currentPosition);
//                ridingPause.title("기록 측정 정지지점");
//                ridingPause.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                ridingPauseMarker = mMap.addMarker(ridingPause);

                pauseOffset = SystemClock.elapsedRealtime() - bike_timer.getBase();
                timer = SystemClock.elapsedRealtime() - bike_timer.getBase();
                timeRunning = false; // 정지
                bike_measurement_start.setVisibility(View.VISIBLE);
                bike_measurement_stop.setVisibility(View.GONE);
                bike_measurement_reset.setEnabled(true); // reset 버튼 활성화
            }
        });

        bike_measurement_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.setMessage("측정을 종료하고 리셋 하시겠습니까?");
                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // ridingPauseMarker.remove(); // 중지 지점 삭제
                        MarkerOptions ridingEnd = new MarkerOptions();
                        ridingEnd.position(currentPosition);
                        ridingEnd.title("기록 측정 종료 지점");
                        ridingEnd.icon((BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.ic_map_riding_stop_marker))));
                        ridingEndMarker = mMap.addMarker(ridingEnd);

                        // 종료 지점 경도, 위도 설정
                        f_lat = String.valueOf(location.getLatitude());
                        f_long = String.valueOf(location.getLongitude());
                        // 기록 측정 종료 시간
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        f_time = sdfNow.format(date);


                        Log.d("최종 라이딩 정보", "총 라이딩 시간 : " + timer + " 총 라이딩 거리 :" + sum_dist);
                        Log.d("최종 라이딩 정보", "시작시간 : " + s_time + " 시작지점 경도 :" + s_lat + " 시작지점 위도 : " + s_long);
                        Log.d("최종 라이딩 정보", "종료시간 : " + f_time + " 종료지점 경도 :" + f_lat + " 종료지점 위도 : " + f_long);

                        // 초기화

                        bike_timer.setBase(SystemClock.elapsedRealtime());
                        pauseOffset = 0;
                        sum_dist = 0;
                        avg_speed = 0;
                        s_lat = "";
                        s_long = "";
                        s_time = ""; // 시작 지점 GPS 정보 초기화
                        f_lat = "";
                        f_long = "";
                        f_time = ""; // 종료 지점 GPS 정보 초기화

                        bike_avg_speed.setText("0.0 m/s");
                        bike_distance.setText("0 m");


                    }
                });
                dialog.show();
            }
        });


        // 자전거 속도 측정 모드 끝

        loadingDialog.dismiss();

    }


    // 위치 업데이트 콜백 정의, 즉 위치 업데이트 요청시마다 실행함
    final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) { // 장치 위치 정보를 사용할 수 있을때 호출된다.
            super.onLocationResult(locationResult); // 부모 객체의 메소드 사용


            List<Location> locationList = locationResult.getLocations(); // 현재 위치값을 받아온다.

            if (locationList.size() > 0) { // 받아온 위치값이 있다면,
                location = locationList.get(locationList.size() - 1); // location에 현재 위치값을 넣는다.
                //location = locationList.get(0);
                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                // 좌표 객체(LatLng)로 변환

                // 마커 타이틀 및 스니펫 설정
                String markerTitle = "현재 내 위치";
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //현재 위치에 마커 생성하고 이동
                // setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocation = location;

                if (timeRunning == true && bikeON == true) { // 측정 모드 ON
                    Log.d("측정 모드", "START");

                    Log.d("Before", String.valueOf(bef_lat));
                    Log.d("Before", String.valueOf(bef_long));
                    LatLng bef_latLng = new LatLng(bef_lat, bef_long);
                    // 이전 위치 정보 저장
                    timer = (int) (SystemClock.elapsedRealtime() - bike_timer.getBase()) / 1000;
                    Log.d("Timer", String.valueOf(timer));

                    // 현재 위치 정보 저장
                    bike_distance.setText(sum_dist + " m");
                    bike_avg_speed.setText(avg_speed + " m/s");
                    cur_lat = location.getLatitude();
                    cur_long = location.getLongitude();
                    LatLng cur_latLng = new LatLng(cur_lat, cur_long);


                    calDistance = new CalDistance(bef_lat, bef_long, cur_lat, cur_long);
                    double dist = calDistance.getDistance();
                    dist = (int) (dist * 100) / 100.0;
                    Log.d("dist", String.valueOf(dist));
                    sum_dist += dist;
                    Log.d("sum_dist", String.valueOf(sum_dist));
                    // 평균 속도 계산
                    avg_speed = sum_dist / timer;
                    avg_speed = (int) (avg_speed * 100) / 100.0; // 소수점 둘째 자리 계산
                    Log.d("avg_speed", String.valueOf(avg_speed));
                    bef_lat = cur_lat;
                    bef_long = cur_long;

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentPosition); // 현재 좌표 지정
                    mMap.moveCamera(cameraUpdate); // 현재 좌표로 이동
                    // 폴리라인 생성
                    mMap.addPolyline(new PolylineOptions().color(0xFF6BC77C).width(30.0f).geodesic(true).add(cur_latLng).add(bef_latLng));
                    bef_latLng = cur_latLng;


                }
            }
        }
    };


    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) { // 위치 갱신 서비스 활성화가 되어있지 않다면

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting(); // 활성화하게 도와줌
        } else { // 갱신 서비스가 활성화 되어있다면

            // 퍼미션 허용 확인
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            // 퍼미션 허용이 되어있지 않다면 리턴처리
            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            // 사용자의 현 위치 업데이트를 요청한다.

            if (checkPermission()) // 퍼미션 확인도 됐다면
                mMap.setMyLocationEnabled(true);  // 본인 위치 레이어를 활성화시킨다.

        }

    }


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap != null)
                mMap.setMyLocationEnabled(true);

        }


    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

        Log.d(TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        Log.d(TAG, "onResume");
    }

    @Override
    public void onStop() {

        super.onStop();
        mapView.onStop();

        if (mFusedLocationClient != null && bikeON == false) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }


    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(getActivity(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getActivity(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getActivity(), "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() { // 로케이션 매니저 설정 및 위치값 갱신 허가
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        // 로케이션 매니저 설정 및 선언

        // 위치값 갱신 호출
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) // GPS로 호출하거나
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); // 네트워크로 호출할 수 있음
    }


    // 현 위치에 마커를 넣는 함수
    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) currentMarker.remove(); // 지난번 위치의 마커가 있다면 지운다.
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        // 현재 좌표

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng); // 현 좌표로 포지션 설정
        markerOptions.title(markerTitle); // 타이틀 지정
        // markerOptions.snippet(markerSnippet); // 부가설명 지정
        markerOptions.draggable(true); // 마커 드래그 가능하게 설정

        currentMarker = mMap.addMarker(markerOptions); // 마커 추가
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        // mMap.moveCamera(cameraUpdate);

    }


    public void setDefaultLocation() {
        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "서울";
        String markerSnippet = "대한민국의 수도";

        // 디폴트 위치 마커 넣는 과정
//        if (currentMarker != null) currentMarker.remove();
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(DEFAULT_LOCATION);
//        markerOptions.title(markerTitle);
//        markerOptions.snippet(markerSnippet);
//        markerOptions.draggable(true);
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);
    }


    public void getFavoriteLocation() {
        Log.d("Favorite", "START");

        //  통신 시 json 사용과 해당 객체로의 파싱을 위해 생성,
        //  이 부분이 없을 시 IllegalArgumentException 발생 함
        Gson gson = new GsonBuilder().setLenient().create();


        // Retrofit2 시작
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FavoriteService.FAVORITE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        FavoriteService retrofitAPI = retrofit.create(FavoriteService.class);

        retrofitAPI.getFavorite(userId).enqueue(new Callback<List<Favorite>>() {
            @Override
            public void onResponse(Call<List<Favorite>> call, retrofit2.Response<List<Favorite>> response) {
                if (response.isSuccessful()) {
                    Log.d("Favorite", "Response");
                    List<Favorite> data = response.body();

                    for (int i = 0; i < data.size(); i++) {

                        double latitude = data.get(i).getLatitude();
                        double longitude = data.get(i).getLongitude();
                        String title = data.get(i).getTitle();
                        String content = data.get(i).getContent();


                        LatLng FAVORITE_LOCATION = new LatLng(latitude, longitude);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(FAVORITE_LOCATION);
                        markerOptions.title(title);
                        markerOptions.snippet(content);
                        markerOptions.draggable(true);
                        markerOptions.icon((BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.ic_map_favorite_marker))));
                        favoriteMarker = mMap.addMarker(markerOptions);

                    }
                }

            }

            @Override
            public void onFailure(Call<List<Favorite>> call, Throwable t) {
                Toast.makeText(getActivity(), "서버 네트워크가 닫혀있습니다.", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }


    public void getBikeAPI() { // 따릉이 API 받아오는 구간

        Log.d("getBikeAPI", "START");

//        mClusterManager = new ClusterManager<BikeItem>(getActivity(), mMap); // 클러스터링 마커 설정
//        mClusterManager.setRenderer(new BikeRenderer(getActivity(), mMap, mClusterManager));
//
//        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<BikeItem>() {
//            @Override
//            public boolean onClusterItemClick(BikeItem bikeItem) { // 클러스터 마커 클릭했을 때 이벤트
//                bottomStationName = getView().findViewById(R.id.map_detail_stationName);
//
//                bottomStationName.setText(bikeItem.getStationName());
//                bikeBottomSheet.setVisibility(View.VISIBLE);
//                if (clickMarker != null) {
//                    clickMarker.remove();
//                }
//                return false;
//            }
//        });

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager.getMarkerManager());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BikeService.SEOUL_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BikeService retrofitAPI = retrofit.create(BikeService.class);

        retrofitAPI.getBike(SEOUL_API_KEY).enqueue(new Callback<SeoulBike>() {
            @Override
            public void onResponse(Call<SeoulBike> call, retrofit2.Response<SeoulBike> response) {
                if (response.isSuccessful()) {
                    SeoulBike data = response.body();
                    RentBikeStatus rentBikeStatus = data.getRentBikeStatus();
                    List<Row> row = rentBikeStatus.getRow();

                    for (int i = 0; i < row.size(); i++) {
                        String stationName = row.get(i).getStationName();
                        int rackTotCnt = row.get(i).getRackTotCnt();
                        int parkingBikeTotCnt = row.get(i).getParkingBikeTotCnt();
                        int shared = row.get(i).getShared();
                        String stationId = row.get(i).getStationId();
                        double stationLatitude = row.get(i).getStationLatitude();
                        double stationLongitude = row.get(i).getStationLongitude();

                        bike_detail.setText(String.valueOf(parkingBikeTotCnt));
                        Bitmap icon = createDrawableFromView(getActivity(), bikeMarkerLayout);

                        LatLng SEOUL_BIKE_LOCATION = new LatLng(stationLatitude, stationLongitude);

                        // Row BikeItem = new Row(rackTotCnt, stationName, parkingBikeTotCnt, shared, stationLatitude, stationLongitude, stationId, SEOUL_BIKE_LOCATION);
                        BikeItem bikeItem = new BikeItem(stationLatitude, stationLongitude, rackTotCnt, stationName, parkingBikeTotCnt, icon);
                        mClusterManager.addItem(bikeItem);

                    }
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                }
            }

            @Override
            public void onFailure(Call<SeoulBike> call, Throwable t) {
                t.printStackTrace();
            }
        });


    }

//    @Override
//    public boolean onClusterItemClick(BikeItem bikeItem) {
//        Log.d("Cluster Click", "START");
//
//
//
//        bottomStationName = getView().findViewById(R.id.map_detail_stationName);
//
//        bottomStationName.setText(bikeItem.getStationName());
//        bikeBottomSheet.setVisibility(View.VISIBLE);
//        if (clickMarker != null) {
//            clickMarker.remove();
//        }
////        bikeBottomSheetDialog modalbikeBottomSheet = new bikeBottomSheetDialog(this);
////        modalbikeBottomSheet.setContentView(view);
////        modalbikeBottomSheet.show();
//        return false;
//    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        return false;

    }


    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
                bikeMyLocation.setVisibility(View.VISIBLE);
                bikeMeasurement.setVisibility(View.VISIBLE);
                bikeFavorite.setVisibility(View.VISIBLE);
                bikeRefresh.setVisibility(View.VISIBLE);
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), REQUIRED_PERMISSIONS[1])) {

                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            getActivity().finish();
                        }
                    }).show();

                } else {
                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            getActivity().finish();
                        }
                    }).show();
                }
            }

        }
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");
                        needRequest = true;
                        return;
                    }
                }
                break;
        }
    }


    void connect() { // 소켓 연결하는 부분
        mHandler = new Handler();
        Log.w("connect", "연결 하는중");
        final String location = "location " + String.valueOf(currentPosition); // 현재 위치

        // 받아오는거
        Thread checkUpdate = new Thread() {
            public void run() {
                // ip 입력
                String newip = "172.20.10.2";
                // String newip = "192.168.0.4";

                // 서버 접속
                try {
                    socket = new Socket(newip, port);
                    Log.w("서버 접속됨", "서버 접속됨");
                } catch (IOException e1) {
                    Log.w("서버접속못함", "서버접속못함");
                    e1.printStackTrace();
                }

                Log.w("edit 넘어가야 할 값 : ", "안드로이드에서 서버로 연결요청");

                try {
                    byte[] data = location.getBytes(); // string 형식을 byte 배열 형식으로 변환
                    ByteBuffer b = ByteBuffer.allocate(4); // ByteBuffer 를 통해 데이터 길이를 byte 형식으로 변환
                    b.order(ByteOrder.LITTLE_ENDIAN);  // byte 포맷은 Little Endian 임
                    b.putInt(data.length);
                    OutputStream sender = socket.getOutputStream();
                    sender.write(b.array(), 0, 4); // 데이터 길이 전송
                    sender.write(data); // 데이터 전송
//                  dos = new DataOutputStream(socket.getOutputStream());   // output에 보낼꺼 넣음
//                  dis = new DataInputStream(socket.getInputStream());     // input에 받을꺼 넣어짐
//                  dos.writeUTF(location);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w("버퍼", "버퍼생성 잘못됨");
                }
                Log.w("버퍼", "버퍼생성 잘됨");

            }
        };
        checkUpdate.start();
    }

    // vector -> bitmap
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    // xml -> bitmap
    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    // 즐겨찾기 등록 다이얼로그 기능 부여
    public void showFavoriteDialog() {


        EditText dialog_favorite_title, dialog_favorite_content;
        Button dialog_favorite_cancel, dialog_favorite_insert;
        TextView dialog_favorite_length_check;

        dialog_favorite_title = favoriteDialog.findViewById(R.id.dialog_favorite_title);
        dialog_favorite_content = favoriteDialog.findViewById(R.id.dialog_favorite_content);
        dialog_favorite_cancel = favoriteDialog.findViewById(R.id.dialog_favorite_cancel);
        dialog_favorite_insert = favoriteDialog.findViewById(R.id.dialog_favorite_insert);
        dialog_favorite_length_check = favoriteDialog.findViewById(R.id.dialog_favorite_length_check);

        dialog_favorite_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                double setFavoriteLat = clickMarker.getPosition().latitude;
                double setFavoriteLong = clickMarker.getPosition().longitude;
                String setFavoriteTitle = dialog_favorite_title.getText().toString();
                String setFavoriteContent = dialog_favorite_content.getText().toString();


                if (setFavoriteTitle.length() < 3 || setFavoriteContent.length() < 3) {

                    dialog_favorite_length_check.setTextColor(Color.RED);
                } else {

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(FavoriteService.FAVORITE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    FavoriteService retrofitAPI = retrofit.create(FavoriteService.class);

                    retrofitAPI.insertFavorite(userId, setFavoriteLat, setFavoriteLong, setFavoriteTitle, setFavoriteContent).enqueue(new Callback<CheckSuccess>() {
                        @Override
                        public void onResponse(Call<CheckSuccess> call, retrofit2.Response<CheckSuccess> response) {
                            if (response.isSuccessful()) {
                                Log.d("Favorite", "Response");
                                Toast.makeText(getActivity(), "성공적으로 추가됐어요! 좌측 상단의 새로고침을 눌러보세요.", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<CheckSuccess> call, Throwable t) {
                            t.printStackTrace();
                            Toast.makeText(getActivity(), "알 수 없는 이유로 실패했어요.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    favoriteDialog.dismiss();
                }
            }
        });

        dialog_favorite_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoriteDialog.dismiss();
            }
        });


    }
}