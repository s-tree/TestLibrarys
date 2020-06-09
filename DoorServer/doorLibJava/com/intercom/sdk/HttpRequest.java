package com.intercom.sdk;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.intercom.base.annotations.CalledByNative;

import java.lang.ref.WeakReference;

/**
 * http request/post/upload
 * support SSL connection
 * 支持多次请求，但是底层是串行处理的
 */
public class HttpRequest {
    /**
     * Accessed by native methods: provides access to C++ HttpRequest object
     */
    @SuppressWarnings("unused")
    private long mNativeJavaObj;

    private final HttpRequestHandler handler;

    private final CallbackHandler callback;

    public interface HttpRequestHandler {
        /**
         * 请求完成回调
         * @param url:原始的请求url
         * @param eff_url：实际请求的url
         * @param err:0：成功，其他表示失败，具体可参考 https://curl.haxx.se/libcurl/c/libcurl-errors.html
         * @param response:服务器返回
         */
        void onHttpRequestCompleted(String url, String eff_url, int err, String response);
    }

    public HttpRequest(HttpRequestHandler handler) {
        this.mNativeJavaObj = 0;
        this.handler = handler;
        this.callback = new CallbackHandler(this);
    }

    public boolean post(String url, String data) {
        return nativeStart(new WeakReference<HttpRequest>(this), url, data, null);
    }

    public boolean upload(String url, String name, String file) {
        return nativeStart(new WeakReference<HttpRequest>(this), url, name, file);
    }

    public void stop() {
        nativeStop();
    }

    public void release() {
        nativeRelease();
    }

    @Override
    protected void finalize() {
        nativeFinalize();
    }

    private static class CallbackHandler extends Handler {
        private final WeakReference<HttpRequest> manager;

        CallbackHandler(HttpRequest manager) {
            this.manager = new WeakReference<>(manager);
        }

        @Override
        public void handleMessage(Message message) {
            HttpRequest manager = this.manager.get();
            if (manager != null) {
                if (message.what == 0) {
                    String url = message.getData().getString("url");
                    String eff_url = message.getData().getString("eff_url");
                    int err = message.getData().getInt("err");
                    String response = message.getData().getString("response");
                    manager.handler.onHttpRequestCompleted(url, eff_url, err, response);
                }
            } else {
                super.handleMessage(message);
            }
        }
    }

    //---------------------------------------------------------
    // Java methods called from the native side
    //--------------------
    @CalledByNative
    @SuppressWarnings("unused")
    private static void onHttpRequestCompleted(Object weak_thiz,
                                               String url,
                                               String eff_url,
                                               int err,
                                               String response) {
        HttpRequest request = (HttpRequest) ((WeakReference) weak_thiz).get();
        if (request == null || request.callback == null) {
            return;
        }
        Message m = new Message();
        m.what = 0;
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("eff_url", eff_url);
        bundle.putInt("err", err);
        bundle.putString("response", response);
        m.setData(bundle);
        request.callback.sendMessage(m);
    }

    private native final boolean nativeStart(Object weak_this,
                                             String url,
                                             String arg1,
                                             String arg2);

    private native final void nativeStop();

    private native final void nativeRelease();

    private native final void nativeFinalize();
}
