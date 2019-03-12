package com.jingxi.smartlife.pad.sdk.demo;

import android.app.Application;

public class DemoApplication extends Application {
    public static DemoApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }
}
