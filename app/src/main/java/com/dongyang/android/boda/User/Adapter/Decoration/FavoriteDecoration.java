package com.dongyang.android.boda.User.Adapter.Decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FavoriteDecoration
        extends RecyclerView.ItemDecoration {

    private int divHeight = 0;
//    private Paint paint = new Paint();

    public FavoriteDecoration(int divHeight) {
        this.divHeight = divHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = divHeight;
        outRect.bottom = divHeight;
    }

//    @Override
//    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//        paint.setColor(Color.GRAY);
//
//        for (int i = 0; i < parent.getChildCount(); i++) {
//            View child = parent.getChildAt(i);
//            RecyclerView.LayoutParams params =
//
//        }
//    }
}
