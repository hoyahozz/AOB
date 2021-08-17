package com.dongyang.android.aob.Repair.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dongyang.android.aob.R;
import com.dongyang.android.aob.Repair.Model.Youtube;

import java.util.ArrayList;
import java.util.List;


public class YoutubeAdapter
        extends RecyclerView.Adapter<YoutubeAdapter.ViewHolder> {

    private Context context;
    private List<Youtube> datas = new ArrayList<>();

    public YoutubeAdapter(Context context, List<Youtube> datas) {
        this.context = context;
        this.datas = datas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_youtube, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(context, datas, position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView thumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.youtube_title);
            thumbnail = itemView.findViewById(R.id.youtube_thumbnail);
        }

        private void bind(Context context, List<Youtube> datas, int position) {
            title.setText(datas.get(position).getTitle()); // 해당 포지션에서 제목을 받아옴
            String url = datas.get(position).getUrl(); // 해당 포지션에서 썸네일 url을 받아옴
            // https://jizard.tistory.com/179
            Glide.with(context).load(url).into(thumbnail); // url -> 사진으로 변경 후 이미지뷰에 설정

            itemView.setOnClickListener(new View.OnClickListener() { // 리스트를 눌렀을 때 액션 설정(해당 유튜브 동영상이 나오게 설정해야 함)
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "클릭하였음.", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

}
