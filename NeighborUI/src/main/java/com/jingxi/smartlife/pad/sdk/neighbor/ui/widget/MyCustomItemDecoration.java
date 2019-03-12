package com.jingxi.smartlife.pad.sdk.neighbor.ui.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Android RecyclerView设置item间距
 */
public class MyCustomItemDecoration extends RecyclerView.ItemDecoration {

    private int right, left, bottom, top;

    public MyCustomItemDecoration(int right, int left, int bottom, int top) {
        this.right = right;
        this.left = left;
        this.bottom = bottom;
        this.top = top;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right = right;
        outRect.left = left;
        outRect.bottom = bottom;
        outRect.top = top;
    }
}
