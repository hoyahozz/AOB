package com.dongyang.android.boda.User.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dongyang.android.boda.R;
import com.dongyang.android.boda.User.Model.Favorite;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter
        extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private Context context;
    private List<Favorite> datas = new ArrayList<>();

    public FavoriteAdapter(Context context, List<Favorite> datas) {
        this.context = context;
        this.datas = datas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int fnum = datas.get(position).getFnum();
        holder.bind(context, datas, position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.favorite_title);
            content = itemView.findViewById(R.id.favorite_content);
        }

        private void bind(Context context, List<Favorite> datas, int position) {
            title.setText(datas.get(position).getTitle());
            content.setText(datas.get(position).getContent());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "클릭하였음.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
