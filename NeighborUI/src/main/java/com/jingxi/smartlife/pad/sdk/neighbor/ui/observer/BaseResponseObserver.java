package com.jingxi.smartlife.pad.sdk.neighbor.ui.observer;

import com.jingxi.smartlife.pad.sdk.network.BaseEntry;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Observer;

public abstract class BaseResponseObserver<T> implements Observer<BaseEntry> {

    public Class<T> getContentClass(){
        Type superClass = this.getClass().getGenericSuperclass();
        Type type = ((ParameterizedType)superClass).getActualTypeArguments()[0];
        Class<T> clazz = null;
        try {
            return (Class<T>) Class.forName(((Class) type).getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("class fromat err");
    }
}