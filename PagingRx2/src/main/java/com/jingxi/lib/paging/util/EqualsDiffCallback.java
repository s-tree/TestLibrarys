package com.jingxi.lib.paging.util;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class EqualsDiffCallback<T> extends DiffUtil.ItemCallback<T> {

    @Override
    public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
        return oldItem.equals(newItem);
    }
}
