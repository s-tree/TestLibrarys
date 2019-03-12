package com.jingxi.smartlife.pad.sdk.demo.dooraccess;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.intercom.base.Log;
import com.intercom.sdk.IntercomConstants;
import com.intercom.sdk.NetClient;
import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.demo.R;
import com.jingxi.smartlife.pad.sdk.doorAccess.DoorAccessManager;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.DoorSessionManager;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.bean.DoorEvent;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.bean.DoorRecordBean;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.orm.DoorAccessOrmUtil;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.ui.DoorAccessConversationUI;

/**
 * 门禁主页面
 */
public class DoorAccessVideoActivity extends AppCompatActivity implements
        SurfaceHolder.Callback,DoorAccessConversationUI {
    SurfaceView surfaceView;
    DoorAccessManager manager;
    String sessionId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dooraccess_video);
        surfaceView = (SurfaceView) findViewById(R.id.surface);

        manager = JXPadSdk.getDoorAccessManager();
        manager.addConversationUIListener(this);

        sessionId = getIntent().getStringExtra("sessionId");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DoorAccessManager.getInstance().removeConversationUIListener(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        manager.updateCallWindow(sessionId, surfaceView);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        manager.updateCallWindow(sessionId, null);
    }

    public void accept(View v){
        manager.acceptCall(sessionId);
    }

    public void hangup(View v){
        manager.hangupCall(sessionId);
        finish();
    }

    public void updateSurface(View v) {
        manager.updateCallWindow(sessionId, surfaceView);
    }

    @Override
    public void startTransPort(String sessionID) {
        Toast.makeText(this,"开始传输视频",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void refreshEvent(DoorEvent event) {
        if(TextUtils.equals(event.getCmd(), IntercomConstants.kIntercomCommandHangup)){
            manager.hangupCall(sessionId);
            finish();
        }
        else if(TextUtils.equals(event.getCmd(), IntercomConstants.kIntercomCommandSessionTimeout)){
            Toast.makeText(this,"超时",Toast.LENGTH_SHORT).show();
            manager.hangupCall(sessionId);
            finish();
        }
        else if(TextUtils.equals(event.getCmd(),IntercomConstants.kIntercomCommandPickupByOther)){
            Toast.makeText(this,"其他用户接听",Toast.LENGTH_SHORT).show();
            manager.hangupCall(sessionId);
            finish();
        }

    }

    @Override
    public void onBackPressed() {
    }
}
