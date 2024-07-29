package com.jingxi.lib.paging.adapter;

import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import kotlinx.coroutines.CoroutineDispatcher;

public abstract class BasePagingDataAdapter<T,K extends RecyclerView.ViewHolder> extends PagingDataAdapter<T,K> {
    List<T> delSet = new ArrayList<>();

    public BasePagingDataAdapter(@NotNull DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
    }

    public BasePagingDataAdapter(@NotNull DiffUtil.ItemCallback<T> diffCallback, @NotNull CoroutineDispatcher mainDispatcher) {
        super(diffCallback, mainDispatcher);
    }

    public BasePagingDataAdapter(@NotNull DiffUtil.ItemCallback<T> diffCallback, @NotNull CoroutineDispatcher mainDispatcher, @NotNull CoroutineDispatcher workerDispatcher) {
        super(diffCallback, mainDispatcher, workerDispatcher);
    }

    public void pagingInsert(int index,T newItem){
        int size = snapshot().getSize();
        if(index >= size){
            return;
        }
        for(int i = size - 1;i > index; i--){
            T next = snapshot().get(i);
            T source = snapshot().get(i - 1);
            cloneTo(source,next);
        }
        T tag = snapshot().get(index);
        cloneTo(newItem,tag);
        notifyItemRangeChanged(index,size - index);
    }

    public void pagingUpdate(T newItem){
        int index = snapshot().getItems().indexOf(newItem);
        if(index == -1){
            return;
        }
        T cache = snapshot().get(index);
        cloneTo(newItem,cache);
        notifyItemChanged(index);
    }

    public void pagingRemove(T delItem){
        int index = snapshot().indexOf(delItem);
        if(index == -1){
            return;
        }
        delSet.add(delItem);
        notifyItemChanged(index);
    }

    private void cloneTo(T newItem,T cache){
        try{
            Field[] fieldList = newItem.getClass().getDeclaredFields();
            for(Field field : fieldList){
                field.setAccessible(true);
                field.set(cache,field.get(newItem));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getAliveItemCount(){
        return getItemCount() - delSet.size();
    }

    public boolean hasDeleted(T obj){
        for(T data : delSet){
            if(data.equals(obj)){
                return true;
            }
        }
        return false;
    }
}
