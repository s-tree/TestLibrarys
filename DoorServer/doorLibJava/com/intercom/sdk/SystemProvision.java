package com.intercom.sdk;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.intercom.base.annotations.CalledByNative;

import java.lang.ref.WeakReference;

/**
 * 系统部署实现，我们支持两种部署方式：
 * 1）使用deploy.json部署，需要上层实现服务器激活，然后获得deploy.json，传入底座，开始部署，直到部署完成
 * 2）将deploy.json, deploy.lua,conf.tar.gz放在同一个目录，然后执行本地部署
 */

public class SystemProvision {
    /**
     * Accessed by native methods: provides access to C++ SystemProvision object
     */
    @SuppressWarnings("unused")
    private long mNativeJavaObj;

    private final SystemProvisionHandler handler;

    private final CallbackHandler callback;

    public static final int Result_Success = 0;
    public static final int Result_Deploy_Json_Error = 1;
    public static final int Result_Script_Error = 2;
    public static final int Result_Unknown_Error = 3;

    public interface SystemProvisionHandler {
        void onSystemProvisionCompleted(int err, boolean reboot, String room_number);
    }

    public SystemProvision(SystemProvisionHandler handler) {
        this.handler = handler;
        this.mNativeJavaObj = 0;
        this.callback = new CallbackHandler(this);
    }

    /**
     * 启动部署
     *
     * @param conf:conf == null,表示本地部署，否则是网络部署，
     *                  如本地部署，必须将deploy.lua,deploy.json,conf.tar.gz放在/sdcard/data/doorkeeper/server/update目录下
     * @return
     */
    public boolean start(String conf) {
        return nativeStart(new WeakReference<SystemProvision>(this), conf);
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

    public static boolean isSystemProvisioning(String work_dir) {
        return nativeIsSystemProvisioning(work_dir);
    }

    public static void resetDeviceToUnProvisioning(String work_dir) {
        nativeResetDeviceToUnProvisioning(work_dir);
    }

    private static class CallbackHandler extends Handler {
        private final WeakReference<SystemProvision> manager;

        CallbackHandler(SystemProvision manager) {
            this.manager = new WeakReference<>(manager);
        }

        @Override
        public void handleMessage(Message message) {
            SystemProvision manager = this.manager.get();
            if (manager != null) {
                if (message.what == 0) {
                    int err = message.getData().getInt("err");
                    boolean reboot = message.getData().getBoolean("reboot");
                    String number = message.getData().getString("number");
                    manager.handler.onSystemProvisionCompleted(err, reboot, number);
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
    private static void onSystemProvisionCompleted(Object weak_thiz,
                                                   int err,
                                                   boolean reboot,
                                                   String number) {
        SystemProvision provision = (SystemProvision) ((WeakReference) weak_thiz).get();
        if (provision == null || provision.callback == null) {
            return;
        }
        Message m = new Message();
        m.what = 0;
        Bundle bundle = new Bundle();
        bundle.putInt("err", err);
        bundle.putBoolean("reboot", reboot);
        bundle.putString("number", number);
        m.setData(bundle);
        provision.callback.sendMessage(m);
    }

    private native final boolean nativeStart(Object weak_this, String conf);

    private native final void nativeStop();

    private native final void nativeRelease();

    private native final void nativeFinalize();

    private native static final boolean nativeIsSystemProvisioning(String work_dir);

    private native static final void nativeResetDeviceToUnProvisioning(String work_dir);
}
