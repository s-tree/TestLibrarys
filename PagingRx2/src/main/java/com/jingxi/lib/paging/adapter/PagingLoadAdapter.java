package com.jingxi.lib.paging.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.LoadState;
import androidx.paging.LoadStateAdapter;

import com.jingxi.lib.paging.PagingPlugin;
import com.jingxi.lib.paging.holder.BasePagingHolder;

import java.lang.reflect.Constructor;

public class PagingLoadAdapter extends LoadStateAdapter<BasePagingHolder> {
    int tempLoadRes = -1;

    public PagingLoadAdapter(){

    }

    public PagingLoadAdapter(int tempLoadRes){
        this.tempLoadRes = tempLoadRes;
    }

    @NonNull
    @Override
    public BasePagingHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, @NonNull LoadState loadState) {
        Class<? extends BasePagingHolder> pagingClass = PagingPlugin.holderConfig.loadStateHolder;
        int layoutRes = tempLoadRes == -1 ? PagingPlugin.holderConfig.loadStateLayoutRes : tempLoadRes;
        try {
            Constructor<? extends BasePagingHolder> constructor = pagingClass.getConstructor(View.class);
            return constructor.newInstance(LayoutInflater.from(viewGroup.getContext()).inflate(layoutRes,viewGroup,false));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BasePagingHolder holder, @NonNull LoadState loadState) {
        holder.bind(loadState);
    }
}
