package com.jingxi.smartlife.pad.sdk.neighbor.ui.observer;

import com.alibaba.fastjson.JSON;
import com.jingxi.smartlife.pad.sdk.network.BaseEntry;

import java.util.List;

import io.reactivex.disposables.Disposable;

public abstract class ResponseListObserver<T> extends BaseResponseObserver<T>{
    boolean isDispose = false;

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(BaseEntry baseEntry) {
        isDispose = true;
        if(baseEntry.result){
            List<T> ts = JSON.parseArray(baseEntry.content,getContentClass());
            onResponse(ts);
            onResponse(ts,baseEntry);
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

    public void onResponse(List<T> t){

    }

    public void onResponse(List<T> t,BaseEntry baseEntry){

    }

    public void onFaild(String message){

    }

    public void onFaild(int code,String message){

    }

}
