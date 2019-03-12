package com.jingxi.smartlife.pad.sdk.neighbor.ui.observer;

import com.alibaba.fastjson.JSON;
import com.jingxi.smartlife.pad.sdk.network.BaseEntry;

import io.reactivex.disposables.Disposable;

public abstract class ResponseObserver<T> extends BaseResponseObserver<T>{
    boolean isDispose = false;

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(BaseEntry baseEntry) {
        isDispose = true;
        if(baseEntry.result){
            T t = JSON.parseObject(baseEntry.content,getContentClass());
            onResponse(t);
            onResponse(t,baseEntry);
            return;
        }
        onFaild(baseEntry.msg);
        onFaild(baseEntry.code,baseEntry.msg);
    }

    @Override
    public void onError(Throwable e) {
        isDispose = true;
        onFaild(e.getMessage());
    }

    @Override
    public void onComplete() {
        if (!isDispose){
            onFaild("http request faild");
        }
    }

    public void onResponse(T t){

    }

    public void onFaild(String message){

    }

    public void onResponse(T data,BaseEntry entry){

    }

    public void onFaild(int code,String message){

    }

}
