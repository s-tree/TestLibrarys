package com.jingxi.smartlife.pad.sdk.demo;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;

public class DemoApplication extends Application {
    public static DemoApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        enableMulticast();
    }


    private void enableMulticast() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock multicastLock = wifiManager.createMulticastLock("multicast.test");
        multicastLock.acquire();
    }
}
