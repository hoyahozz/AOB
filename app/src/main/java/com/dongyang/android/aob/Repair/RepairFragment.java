package com.dongyang.android.aob.Repair;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dongyang.android.aob.LoadingDialog;
import com.dongyang.android.aob.Main.MainActivity;
import com.dongyang.android.aob.R;
import com.dongyang.android.aob.Repair.Model.Result;
import com.dongyang.android.aob.Repair.Service.DeepRunningService;
import com.dongyang.android.aob.SplashActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission_group.CAMERA;
import static android.app.Activity.RESULT_OK;


public class RepairFragment extends Fragment {
    //private View view;

    private static final String TAG = "RepairFragment";
    //private static final int PERMISSIONS_REQUEST_CODE = 100;
    public static final int REQUEST_TAKE_PHOTO = 10;
    //public static final int REQUEST_PERMISSION = 11;

    private Button btnCamera, btnSave;
    private ImageView ivCapture;
    private String mCurrentPhotoPath;
    private LoadingDialog loadingDialog;
    BottomNavigationView main_bnv;
    FragmentManager fragmentManager;
    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.

    String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View repairLayout = inflater.inflate(R.layout.fragment_repair, container, false);
        mLayout = repairLayout.findViewById(R.id.repairLayout);
        ivCapture = repairLayout.findViewById(R.id.ivCapture); //imageView 선언
        btnCamera = repairLayout.findViewById(R.id.btnCapture); // button 선언
        btnSave = repairLayout.findViewById(R.id.btnSave); //button 선언


        main_bnv = getActivity().findViewById(R.id.main_bnv);
        fragmentManager = getActivity().getSupportFragmentManager();




        loadImgArr();

        //촬영
        btnCamera.setOnClickListener(v -> captureCamera());

        //저장
        btnSave.setOnClickListener(view -> {
            try {
                BitmapDrawable drawable = (BitmapDrawable) ivCapture.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                //찍은 사진이 없으면
                if (bitmap == null) {
                    Toast.makeText(this.getActivity(), "저장할 사진이 없습니다", Toast.LENGTH_SHORT).show();
                } else {
                    //저장
                    saveImg();
                    mCurrentPhotoPath = ""; //initialize
                }
            } catch (Exception e) {
                Toast.makeText(this.getActivity(), "저장할 사진이 없습니다", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "SAVE ERROR", e);
            }
        });

        return repairLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        checkPermission(); // 스낵바는 뷰를 변경하는 개념인데, OnCreateView 부근에서는 뷰가 완전히 완성되지 않음
        // 그렇기 때문에 완전히 뷰가 생성된 후에 퍼미션을 진행해 스낵바를 사용할 수 있게끔 하였음
    }

    //카메라 기능 실행
    private void captureCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //인텐트를 처리 할 카메라 액티비티가 있는지 확인
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            //촬영한 사진을 저장할 파일 생성
            File photoFile = null;
            Log.d(TAG, "captureCamera ON");
            try {
                File tempDir = getActivity().getCacheDir();

                String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
                String imageFileName = "Capture_" + timeStamp + "_";

                File tempImage = File.createTempFile(
                        imageFileName, //파일이름
                        ".jpg", //파일형식
                        tempDir //경로
                );

                //ACTION_VIEW 인텐트를 사용할 경로
                mCurrentPhotoPath = tempImage.getAbsolutePath();
                photoFile = tempImage;
            } catch (IOException e) {
                Log.w(TAG, "파일 생성 에러", e);
            }
            //파일이 정상적으로 생성되면 계속 진행
            if (photoFile != null) {
                //Uri 가져오기
                Uri photoURI = FileProvider.getUriForFile(this.getActivity(),
                        getActivity().getPackageName() + ".fileprovider",
                        photoFile);

                //인텐트에 uri담기
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                //인텐트 실행
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    //이미지저장 메소드
    private void saveImg() {
        try {
            // 로딩 다이얼로그 활성화
            loadingDialog = new LoadingDialog(this.getActivity());
            loadingDialog.show();

            //저장할 파일 경로
            Log.d(TAG, getActivity().getFilesDir().toString());
            File storageDir = new File(getActivity().getFilesDir() + "/capture");
            if (!storageDir.exists()) //폴더가 없으면 생성
                storageDir.mkdirs();

            String filename = "file" + ".jpg";

            //기존에 있다면 삭제
            File file = new File(storageDir, filename);
            boolean deleted = file.delete();
            Log.w(TAG, "Delete Dup Check: " + deleted);
            FileOutputStream output = null;

            try {
                output = new FileOutputStream(file);
                BitmapDrawable drawable = (BitmapDrawable) ivCapture.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    assert output != null;
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.e(TAG, "Captured Saved");
            sendImg(file);

        } catch (Exception e) {
            Log.w(TAG, "Capture Saving Error!", e);
            Toast.makeText(this.getActivity(), "Save failed", Toast.LENGTH_SHORT).show();
        }
    }

    // 파일을 라떼판다 서버로 보낸다.
    private void sendImg(File file) {

        Log.d(TAG, file.toString());
        Log.d(TAG, file.getName());

        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file); // 파일을 보낼 때 리퀘스트바디를 사용함.
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
        // 파일 변수명, 파일의 이름(여기서는 file.jpg), 파일 경로 설정

        // 통신 시간을 최대 15초씩으로 설정
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.MINUTES)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        // Retrofit 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DeepRunningService.SEND_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DeepRunningService sendAPI = retrofit.create(DeepRunningService.class);

        // filepart를 매개변수로 딥 러닝 서버로 전송
        sendAPI.sendImage(filePart).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) { // 성공적으로 받아왔을 때

                    Result result = response.body(); // 결과값을 result에 저장
                    Log.d(TAG, result.getResult());

                    // 결과값이 성공적으로 받아와졌으므로, 유튜브 액티비티로 이동한다.
                    Toast.makeText(getActivity(), "서버 연결 성공!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), YoutubeActivity.class);
                    intent.putExtra("result", result.getResult()); // 결과값에 따라 유튜브 검색값을 입력해야 하기 때문에, 유튜브 액티비티로 데이터를 전송한다.
                    Log.d("Register", result.getResult());
                    startActivity(intent);
                } else { // 사진을 서버에서 인식하지 못했을 때
                    Toast.makeText(getActivity(), "사진을 인식하지 못했어요! 다시 시도해주세요!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(getActivity(), "알 수 없는 이유로 오류가 떴어요.", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }

        });
    }

    private void loadImgArr() {
        try {
            File storageDir = new File(getActivity().getFilesDir() + "/capture");
            String filename = "file" + ".jpg";

            File file = new File(storageDir, filename);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            ivCapture.setImageBitmap(bitmap);

        } catch (Exception e) {
            Log.w(TAG, "Capture loading Error", e);
            //Toast.makeText(this, "load failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            //after capture
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: {
                    if (resultCode == RESULT_OK) {
                        File file = new File(mCurrentPhotoPath);
                        Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));

                        if (bitmap != null) {
                            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);

                            Bitmap rotatedBitmap = null;
                            switch (orientation) {
                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotatedBitmap = rotateImage(bitmap, 90);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotatedBitmap = rotateImage(bitmap, 180);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotatedBitmap = rotateImage(bitmap, 270);
                                    break;

                                case ExifInterface.ORIENTATION_NORMAL:
                                default:
                                    rotatedBitmap = bitmap;
                            }

                            ivCapture.setImageBitmap(rotatedBitmap);
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "onActivityResult Error", e);
        }
    }

    //카메라에 맞게 이미지 로테이션
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    final int MULTI_PERMISSION = 999;

    //권한 확인
    public void checkPermission() {
        try {
            ArrayList<String> permissionOK = new ArrayList();
            ArrayList<String> permissionNO = new ArrayList();

            if (REQUIRED_PERMISSIONS.length > 0) {
                int check = 0;
                for (String data : REQUIRED_PERMISSIONS) {
                    check = ContextCompat.checkSelfPermission(this.getActivity(), data);
                    if (check != PackageManager.PERMISSION_GRANTED) {
                        permissionNO.add(data);
                    } else {
                        permissionOK.add(data);
                    }
                }
                if (permissionNO.size() > 0) { // 퍼미션 거부된 값이 있을 경우

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), REQUIRED_PERMISSIONS[0])
                            || ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), REQUIRED_PERMISSIONS[1])) {
                        Snackbar.make(mLayout, "자가수리 기능을 실행하려면 접근 권한이 필요해요!",
                                Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                // 3-3. 사용자에게 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                                requestPermissions(permissionNO.toArray(new String[permissionNO.size()]), MULTI_PERMISSION);
                            }
                        }).show();
                    } else {
                        requestPermissions(permissionNO.toArray(new String[permissionNO.size()]), MULTI_PERMISSION);
                    }

                } else {
                    getPermissionOK();
                }
            } else {
                Toast.makeText(getActivity(), "퍼미션 허용을 확인할 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPermissionOK() {
        try {
            btnSave.setEnabled(true);
            btnCamera.setEnabled(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ArrayList<String> permissionNoRealTime = new ArrayList();

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MULTI_PERMISSION: {
                if (grantResults.length > 0) { // 퍼미션 권한이 부여되지 않는 배열 길이가 0보다 클경우
                    if (permissionNoRealTime.size() > 0) {
                        permissionNoRealTime.clear();
                    }
                    for (int i = 0; i < permissions.length; i++) { //배열을 순회하면서
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {  //권한이 부여되어 있지 않을 경우 확인
                            permissionNoRealTime.add(permissions[i]);
                        }
                    }
                    Log.d("---", "---");
                    Log.e("//===========//", "============");
                    Log.d("", "\n" + "[실시간 퍼미션 거부된 리스트 : " + permissionNoRealTime.toString() + "]");
                    Log.e("//===========//", "============");
                    Log.d("---", "---");
                    if (permissionNoRealTime.size() > 0) { //TODO 실시간으로 권한 허용이 거부된 값이 있을 경우
                        // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.

                        // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), REQUIRED_PERMISSIONS[0])
                                || ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), REQUIRED_PERMISSIONS[1])) {

                            // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                            Snackbar.make(mLayout, "퍼미션을 거부하시면 해당 기능을 이용할 수 없어요! 퍼미션을 허용해주세요.",
                                    Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("home")).commit();
                                    fragmentManager.beginTransaction().remove(RepairFragment.this).commit();
                                    main_bnv.getMenu().findItem(R.id.navigation_home).setChecked(true);
                                }
                            }).show();

                        } else {
                            // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                            Snackbar.make(mLayout, "퍼미션이 완전히 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다.",
                                    Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("home")).commit();
                                    fragmentManager.beginTransaction().remove(RepairFragment.this).commit();
                                    main_bnv.getMenu().findItem(R.id.navigation_home).setChecked(true);
                                }
                            }).show();
                        }
                    } else { //TODO 실시간으로 모든 권한이 허용된 경우 [메소드 호출]
                        getPermissionOK(); //메소드 호출
                    }
                }
                return;
            }
        }
    }
}