package com.jingxi.smartlife.pad.sdk.demo.dooraccess;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.intercom.sdk.IntercomConstants;
import com.intercom.sdk.IntercomManager;
import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.demo.R;
import com.jingxi.smartlife.pad.sdk.doorAccess.DoorAccessManager;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.bean.DoorEvent;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.ui.DoorAccessConversationUI;

import java.io.File;

/**
 * 门禁主页面
 */
public class DoorAccessVideoActivity extends AppCompatActivity implements
        SurfaceHolder.Callback,DoorAccessConversationUI ,View.OnClickListener{
    SurfaceView surfaceView;
    DoorAccessManager manager;
    String sessionId;
    Button bt_record;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dooraccess_video);
        surfaceView = (SurfaceView) findViewById(R.id.surface);
        surfaceView.getHolder().addCallback(this);

        manager = JXPadSdk.getDoorAccessManager();
        manager.addConversationUIListener(this);

        sessionId = getIntent().getStringExtra("sessionId");
        bt_record = (Button) findViewById(R.id.bt_record);
        bt_record.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DoorAccessManager.getInstance().removeConversationUIListener(this);
        if(!TextUtils.isEmpty(recordSession)){
            DoorAccessManager.getInstance().stopRecord(DoorAccessMainActivity.familyID,sessionId,recordSession);
        }
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
//        sendAnswerMedia(sessionId,true);
//        DoorAccessManager.getInstance().enableLocalToRemoteVideo(DoorAccessMainActivity.familyID,sessionId,false);
//        DoorAccessManager.getInstance().enableLocalToRemoteAudio(DoorAccessMainActivity.familyID,sessionId,false);
    }


    /**
     * 准备访客留言时播放音频
     * @param session
     * @param isEnable
     */
    public static void sendAnswerMedia(String session,boolean isEnable){
        Bundle bundle = new Bundle();
        bundle.putString("scheme", IntercomConstants.kIntercomScheme);
        bundle.putBoolean("enable",isEnable);
        bundle.putString("command","autoanswer");
        bundle.putString("session_id",session);
        bundle.putString("message", "/sdcard/smartlife/mFile/plz_message.pcm");
        File file = new File("/sdcard/smartlife/mFile/plz_message.pcm");
        Log.w("test_bug","file = " + file.getAbsolutePath() + " isExist = " + file.exists());
        IntercomManager.Intercom intercom = IntercomManager.getInstance().getIntercom(DoorAccessMainActivity.familyID);
        if(intercom == null){
            return;
        }
        intercom.sendIntercomCommand(bundle);
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

    /**
     * 单次会话中，可以通过调用 startRecord 和 stopRecord 来实现录制、停止录制视频
     */
    public String recordSession = "";
    public void startRecord(){
        recordSession = DoorAccessManager.getInstance().startRecord(DoorAccessMainActivity.familyID,sessionId);
    }

    public void stopRecord(){
        DoorAccessManager.getInstance().stopRecord(DoorAccessMainActivity.familyID,sessionId,recordSession);
    }

    @Override
    public int inviteIntercept(DoorEvent inviteEvent) {
        return 0;
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.bt_record){
            if(TextUtils.isEmpty(recordSession)){
                startRecord();
            }else{
                stopRecord();
                recordSession = "";
            }
        }
    }
}
