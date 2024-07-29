package com.jingxi.lib.paging.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jingxi.lib.paging.R;

public class PagingLoadHolder extends BasePagingHolder {
    public TextView textView;

    public PagingLoadHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.textView);
    }

    @Override
    public void onLoad() {
        textView.setVisibility(View.VISIBLE);
        textView.setText(R.string.paging_loading);
    }

    @Override
    public void onLoadCompleted() {
        textView.setVisibility(View.GONE);
    }

    @Override
    public void onNoMore() {
        textView.setVisibility(View.VISIBLE);
        textView.setText(R.string.paging_no_more);
    }
}
