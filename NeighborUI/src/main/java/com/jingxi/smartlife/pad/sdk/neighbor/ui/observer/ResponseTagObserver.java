package com.jingxi.smartlife.pad.sdk.neighbor.ui.observer;

public abstract class ResponseTagObserver<T,K> extends ResponseObserver<T>{
    public K tag;

    public ResponseTagObserver(K k){
        this.tag = k;
    }
}
