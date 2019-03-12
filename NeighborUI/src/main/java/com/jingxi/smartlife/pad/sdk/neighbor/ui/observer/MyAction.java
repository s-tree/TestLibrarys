package com.jingxi.smartlife.pad.sdk.neighbor.ui.observer;

/**
 * http请求回调
 */
public interface MyAction<T> {

    void call(T t);
    /**
     * @MyHttpStatus
     */
    void faild(int errorNo);
}
