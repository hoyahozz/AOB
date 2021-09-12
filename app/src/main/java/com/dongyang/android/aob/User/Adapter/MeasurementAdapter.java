package com.dongyang.android.aob.User.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.dongyang.android.aob.Introduction.Model.CheckSuccess;
import com.dongyang.android.aob.Main.MainActivity;
import com.dongyang.android.aob.Map.Model.Measurement.Measure;
import com.dongyang.android.aob.R;
import com.dongyang.android.aob.User.Model.Favorite;
import com.dongyang.android.aob.User.Service.FavoriteService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class MeasurementAdapter
        extends RecyclerView.Adapter<MeasurementAdapter.ViewHolder> {

    private Context context;
    private List<Measure> datas = new ArrayList<>();

    public MeasurementAdapter(Context context, List<Measure> datas) {
        this.context = context;
        this.datas = datas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_measure, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bind(context, datas, position);

//        holder.delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//                dialog.setMessage("즐겨찾기를 삭제하시겠습니까?");
//                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                });
//                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        // 데이터베이스에서도 데이터 삭제, 리사이클러뷰에서 보여지는 아이템 삭제
//                        datas.remove(position);
//                        notifyItemRemoved(position);
//                        deleteFavorite(context, fnum);
//                        notifyItemRangeChanged(position, datas.size());
//                    }
//                });
//                dialog.show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mapImg;
        TextView dist, speed, time;
        String mapString;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mapImg = itemView.findViewById(R.id.measure_image);
            dist = itemView.findViewById(R.id.measure_dist);
            speed = itemView.findViewById(R.id.measure_speed);
            time = itemView.findViewById(R.id.measure_time);


        }

        private void bind(Context context, List<Measure> datas, int position) {

            mapString = datas.get(position).getImage();
            Bitmap bitmap = StringToBitmaps(mapString);

            mapImg.setImageBitmap(bitmap);
            dist.setText(String.valueOf(datas.get(position).getDist()) + " km");
            speed.setText(String.valueOf(datas.get(position).getKcal()) + " km/h");
            time.setText(String.valueOf(datas.get(position).getTime()) + " 분");



//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(context, MainActivity.class);
//                    intent.putExtra("f_lat", latitude);
//                    intent.putExtra("f_long", longitude);
//                    intent.putExtra("value", 1);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    context.startActivity(intent);
//                    ((Activity)context).finish();
//
//                    // Toast.makeText(context, fnum + "(fnum) 클릭하였음.", Toast.LENGTH_SHORT).show();
//                }
//            });

        }
    }

    public static void deleteFavorite(Context context, int fnum) {
        SharedPreferences pref = context.getSharedPreferences("userInfo", MODE_PRIVATE);
        String userId =  pref.getString("id","");

        if(userId == "") {
            Toast.makeText(context, "오류가 발생하였습니다. 어플을 재실행 해주세요.", Toast.LENGTH_SHORT).show();
        } else {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(FavoriteService.FAVORITE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            FavoriteService retrofitAPI = retrofit.create(FavoriteService.class);

            retrofitAPI.deleteFavorite(userId, fnum).enqueue(new Callback<CheckSuccess>() {
                @Override
                public void onResponse(Call<CheckSuccess> call, retrofit2.Response<CheckSuccess> response) {
                    if (response.isSuccessful()) {
                        Log.d("Favorite", "Response");
                        Toast.makeText(context, "삭제가 완료되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CheckSuccess> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(context, "알 수 없는 이유로 실패했어요.", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public static Bitmap StringToBitmaps(String image) {
        Log.e("StringToBitMap", "StringToBitMap");
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            Log.e("StringToBitMap", "good");
            // mapImage.setImageBitmap(bitmap);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
