package com.intercom.sdk;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.intercom.base.Log;
import com.intercom.base.annotations.CalledByNative;

import java.lang.ref.WeakReference;

/**
 * 代理需要自行启动DeployServer
 * 程序启动，需要使用SystemProvision判断是否已经部署，如果未部署，则进行部署
 * 部署完成之后，启动intercomserver and deployserver
 * 另外，DeployServer也可以实现全自动部署
 */
public class DeployServer {
    /**
     * Accessed by native methods: provides access to C++ DeployServer object
     */
    @SuppressWarnings("unused")
    private long mNativeJavaObj;

    private final DeployServerHandler handler;

    private final CallbackHandler callback;

    public interface DeployServerHandler {
        //某个服务器的连接状态改变
        void onNetConnectStateChanged(String url, boolean connected);

        //某个服务器登录完成，如果失败，会有错误代码
        void onNetLoginCompleted(String url, boolean success, int code, String errmsg);

        //管理平台可能执行了什么操作，需要我们重启设备？还是重启APP？
        void onNetReboot(String reason);

        //底层开始执行升级、或者即将执行某个任务，此刻最好不要重启
        void onNetUpdateBegin(String url);

        /**
         * 部署完成、或者升级完成，我们看看reboot，是不是需要重启代理
         * 重启设备应该不需要
         *
         * @param result
         * @param reboot
         * @param room_number
         */
        void onNetUpdateCompleted(boolean result, boolean reboot, String room_number);
    }

    public DeployServer(DeployServerHandler handler) {
        this.handler = handler;
        this.mNativeJavaObj = 0;
        this.callback = new CallbackHandler(this);
    }

    /**
     * 启动 DeployServer
     *
     * @param server_conf:可以为空，则自动从.../conf/server.json中读取 如果不为空，则覆盖.../conf/server.json
     *                                                     如果我们系统尚未部署，可以在这里指定部署服务器配置，可以实现自动部署
     *                                                     如果不指定的话，底层会使用组播去发现服务器，根据场景不同而定
     * @return
     */
    public boolean start(String server_conf) {
        return nativeStart(new WeakReference<DeployServer>(this), server_conf);
    }

    public void enableUpdate(boolean enable) {
        Log.w("test_bug","enableUpdate = " + enable);
        nativeEnableUpdate(enable);
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
        private final WeakReference<DeployServer> server;

        CallbackHandler(DeployServer manager) {
            this.server = new WeakReference<>(manager);
        }

        @Override
        public void handleMessage(Message message) {
            DeployServer server = this.server.get();
            if (server != null) {
                if (message.what == 0) {
                    String reason = message.getData().getString("reason");
                    server.handler.onNetReboot(reason);
                } else if (message.what == 1) {
                    String url = message.getData().getString("url");
                    server.handler.onNetUpdateBegin(url);
                } else if (message.what == 2) {
                    boolean result = message.getData().getBoolean("result");
                    boolean reboot = message.getData().getBoolean("reboot");
                    String number = message.getData().getString("number");
                    server.handler.onNetUpdateCompleted(result, reboot, number);
                } else if (message.what == 3) {
                    String url = message.getData().getString("url");
                    boolean connected = message.getData().getBoolean("connected");
                    server.handler.onNetConnectStateChanged(url, connected);
                } else if (message.what == 4) {
                    String url = message.getData().getString("url");
                    boolean success = message.getData().getBoolean("success");
                    int code = message.getData().getInt("code");
                    String errmsg = message.getData().getString("errmsg");
                    server.handler.onNetLoginCompleted(url, success, code, errmsg);
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
    private static void onReboot(Object weak_thiz, String reason) {
        DeployServer server = (DeployServer) ((WeakReference) weak_thiz).get();
        if (server == null || server.callback == null) {
            return;
        }
        Message m = new Message();
        m.what = 0;
        Bundle bundle = new Bundle();
        bundle.putString("reason", reason);
        m.setData(bundle);
        server.callback.sendMessage(m);
    }

    @CalledByNative
    @SuppressWarnings("unused")
    private static void onNetUpdateBegin(Object weak_thiz, String url) {
        DeployServer server = (DeployServer) ((WeakReference) weak_thiz).get();
        if (server == null || server.callback == null) {
            return;
        }
        Message m = new Message();
        m.what = 1;
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        m.setData(bundle);
        server.callback.sendMessage(m);
    }

    @CalledByNative
    @SuppressWarnings("unused")
    private static void onNetUpdateCompleted(Object weak_thiz,
                                             boolean result,
                                             boolean reboot,
                                             String number) {
        DeployServer server = (DeployServer) ((WeakReference) weak_thiz).get();
        if (server == null || server.callback == null) {
            return;
        }
        Message m = new Message();
        m.what = 2;
        Bundle bundle = new Bundle();
        bundle.putBoolean("result", result);
        bundle.putBoolean("reboot", reboot);
        bundle.putString("number", number);
        m.setData(bundle);
        server.callback.sendMessage(m);
    }

    @CalledByNative
    @SuppressWarnings("unused")
    private static void onNetConnectStateChanged(Object weak_thiz,
                                                 String url,
                                                 boolean connected) {
        DeployServer server = (DeployServer) ((WeakReference) weak_thiz).get();
        if (server == null || server.callback == null) {
            return;
        }
        Message m = new Message();
        m.what = 3;
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putBoolean("connected", connected);
        m.setData(bundle);
        server.callback.sendMessage(m);
    }

    @CalledByNative
    @SuppressWarnings("unused")
    private static void onNetLoginCompleted(Object weak_thiz,
                                            String url,
                                            boolean success,
                                            int code,
                                            String errmsg) {
        DeployServer server = (DeployServer) ((WeakReference) weak_thiz).get();
        if (server == null || server.callback == null) {
            return;
        }
        Message m = new Message();
        m.what = 4;
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putBoolean("success", success);
        bundle.putInt("code", code);
        bundle.putString("errmsg", errmsg);
        m.setData(bundle);
        server.callback.sendMessage(m);
    }

    private native final boolean nativeStart(Object weak_this, String server_conf);

    private native final void nativeEnableUpdate(boolean enable);

    private native final void nativeStop();

    private native final void nativeRelease();

    private native final void nativeFinalize();
}
