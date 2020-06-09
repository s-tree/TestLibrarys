package com.jingxi.smartlife.pad.sdk.demo.doorserver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.intercom.base.Log;
import com.jingxi.smartlife.pad.sdk.doorServer.DoorServerManager;
import com.jingxi.smartlife.pad.sdk.doorServer.base.DoorServerKit;
import com.jingxi.smartlife.pad.sdk.doorServer.base.observers.DoorServerDeployListener;

public class MainActivity extends AppCompatActivity implements DoorServerDeployListener{
    private static final String TAG = "test_bug_doorserver";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.hello).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoorServerManager.getInstance().setServerDeployListener(MainActivity.this);
            }
        });
        findViewById(R.id.hell2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoorServerManager.getInstance().saveAppVersions("1.0.1","1.0.1","1.0.1");
            }
        });
        findViewById(R.id.hell3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoorServerManager.getInstance().initServer(MainActivity.this);
            }
        });
        findViewById(R.id.hell4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoorServerManager.getInstance().enableDeploy(true);
            }
        });
        findViewById(R.id.hell5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoorServerKit.Options options = DoorServerKit.getOptions();
                options.sipUrl = "test.sip.com";
                options.transitUrl = "test.transit.com";
                options.deployUrl = "test.deploy.com";
                options.deployKey = "12345678";
                options.isDebug = true;
                DoorServerKit.init(options);
            }
        });
    }

    @Override
    public void onNetConnectStateChanged(String url, boolean connected) {
        Log.w(TAG,"onNetConnectStateChanged");
    }

    @Override
    public void onNetLoginCompleted(String url, boolean success, int code, String errmsg) {
        Log.w(TAG,"onNetLoginCompleted");
    }

    @Override
    public void onNetReboot(String reason) {
        Log.w(TAG,"onNetReboot");
    }

    @Override
    public void onNetUpdateBegin(String url) {
        Log.w(TAG,"onNetUpdateBegin");
    }

    @Override
    public void onNetUpdateCompleted(boolean result, boolean reboot, String room_number) {
        Log.w(TAG,"onNetUpdateCompleted");
    }
}
