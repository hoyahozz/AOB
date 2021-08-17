package com.dongyang.android.aob.Repair;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dongyang.android.aob.LoadingDialog;
import com.dongyang.android.aob.R;
import com.dongyang.android.aob.Repair.Model.Result;
import com.dongyang.android.aob.Repair.Service.DeepRunningService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

import static android.app.Activity.RESULT_OK;


public class RepairFragment extends Fragment {

    private static final String TAG = "RepairFragment";

    public static final int REQUEST_TAKE_PHOTO = 10;
    public static final int REQUEST_PERMISSION = 11;

    private Button btnCamera, btnSave;
    private ImageView ivCapture;
    private String mCurrentPhotoPath;
    private LoadingDialog loadingDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View repairLayout = inflater.inflate(R.layout.fragment_repair, container, false);

        checkPermission(); //권한체크

        ivCapture = repairLayout.findViewById(R.id.ivCapture); //imageView 선언
        btnCamera = repairLayout.findViewById(R.id.btnCapture); // button 선언
        btnSave = repairLayout.findViewById(R.id.btnSave); //button 선언


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

//             Toast.makeText(this.getActivity(), "서버 연결 성공!", Toast.LENGTH_SHORT).show();
//             Intent intent = new Intent(this.getActivity(), YoutubeActivity.class);
//             startActivity(intent);


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

        // 통신 시간을 최대 30초씩으로 설정
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
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


            // 데이터베이스 (비트맵 형태로 데이터베이스 접속할 때)
            //         String image = bitmapToByteAray(bitmap);
            ivCapture.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.w(TAG, "Capture loading Error", e);
            //Toast.makeText(this, "load failed", Toast.LENGTH_SHORT).show();
        }
    }

//    public String bitmapToByteAray(Bitmap bitmap) {
//        String image = "";
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
//        image = "&image=" + byteArrayToBinaryString(byteArray);
//        return image;
//    }
//
//    public static String byteArrayToBinaryString(byte[] b) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < b.length; ++i) {
//            sb.append(byteToBinaryString(b[i]));
//        }
//        return sb.toString();
//    }
//
//    public static String byteToBinaryString(byte n) {
//        StringBuilder sb = new StringBuilder("0000000");
//        for (int bit = 0; bit < 8; bit++) {
//            if (((n >> bit) & 1) > 0) {
//                sb.setCharAt(7 - bit, '1');
//            }
//        }
//        return sb.toString();
//    }

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
        checkPermission(); //권한체크
    }

    //권한 확인
    public void checkPermission() {
        int permissionCamera = ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.CAMERA);
        int permissionRead = ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWrite = ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //권한이 없으면 권한 요청
        if (permissionCamera != PackageManager.PERMISSION_GRANTED
                || permissionRead != PackageManager.PERMISSION_GRANTED
                || permissionWrite != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.CAMERA)) {
                Toast.makeText(this.getActivity(), " 앱을 실행하기 위해 권한이 필요합니다", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this.getActivity(), "권한 확인", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this.getActivity(), "권한 없음", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}