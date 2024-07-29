package com.jingxi.lib.paging.holder;

import android.view.View;

import androidx.paging.LoadState;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BasePagingHolder extends RecyclerView.ViewHolder {

    public BasePagingHolder(View rootView) {
        super(rootView);
    }

    public void bind(LoadState loadState) {
        if(loadState instanceof LoadState.Loading){
            onLoad();
        }
        else if(loadState instanceof LoadState.Error){
            onNoMore();
        }
        else{
            onLoadCompleted();
        }
    }

    public abstract void onLoad();

    public abstract void onLoadCompleted();

    public abstract void onNoMore();
}