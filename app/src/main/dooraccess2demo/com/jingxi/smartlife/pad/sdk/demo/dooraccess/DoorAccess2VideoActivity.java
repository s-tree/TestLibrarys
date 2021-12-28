package com.jingxi.smartlife.pad.sdk.demo.dooraccess;

import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.intercom.client.IntercomConstants;
import com.intercom.client.IntercomManager;
import com.jingxi.smartlife.pad.sdk.demo.R;
import com.jingxi.smartlife.pad.sdk.doorAccess.DoorAccessManager;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.bean.DoorEvent;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.ui.DoorAccessConversationUI;

import java.io.File;

/**
 * 门禁主页面
 */
public class DoorAccess2VideoActivity extends AppCompatActivity implements
        SurfaceHolder.Callback,DoorAccessConversationUI ,View.OnClickListener{
    SurfaceView surfaceView,localSurface;
    DoorAccessManager manager;
    String sessionId;
    Button bt_record;
    private String cameraName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dooraccess_video);
        surfaceView = (SurfaceView) findViewById(R.id.surface);
        surfaceView.getHolder().addCallback(this);

        localSurface = findViewById(R.id.localSurface);
        ViewGroup group = (ViewGroup)localSurface.getParent();
        group.removeView(localSurface);
//        localSurface.setZOrderOnTop(true);
        localSurface.setZOrderMediaOverlay(true);
        group.addView(localSurface);
        localSurface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        localSurface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                DoorAccessManager.getInstance().updatePreviewWindow(localSurface);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                DoorAccessManager.getInstance().updatePreviewWindow(null);
            }
        });

        manager = DoorAccessManager.getInstance();
        manager.addConversationUIListener(this);

        sessionId = getIntent().getStringExtra("sessionId");

        cameraName = IntercomConstants.kFrontCameraName;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DoorAccessManager.getInstance().removeConversationUIListener(this);
        if(!TextUtils.isEmpty(recordSession)){
            DoorAccessManager.getInstance().stopRecord(DoorAccess2MainActivity.familyID,sessionId,recordSession);
        }
        surfaceView.getHolder().removeCallback(this);
        DoorAccessManager.getInstance().updatePreviewWindow(null);
        surfaceView = null;
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
        IntercomManager.Intercom intercom = IntercomManager.getInstance().getIntercom(DoorAccess2MainActivity.familyID);
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

    public void unlock(View v){
        DoorAccessManager.getInstance().openDoor(sessionId);
    }

    public void SwitchCamera(View v){
        cameraName = TextUtils.equals(cameraName,IntercomConstants.kFrontCameraName)
                ? IntercomConstants.kBackCameraName : IntercomConstants.kFrontCameraName;
        DoorAccessManager.getInstance().switchPreCamera(cameraName);
    }

    public void Record(View v){
        if(TextUtils.isEmpty(recordSession)){
            startRecord();
        }else{
            stopRecord();
            recordSession = "";
        }
    }

    public void SendCustomMessage(View v){
        DoorAccessManager.getInstance().sendMessage(DoorAccess2MainActivity.familyID,sessionId,"Hello,nice to meet you");
    }

    public void toPhone(View view){
        /**
         * 免提
         */
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(true);
    }

    public void toReceiver(View view){
        /**
         * 听筒/耳机
         */
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(false);
    }

    @Override
    public void startTransPort(String sessionID, int mediaType) {
        Toast.makeText(this,"开始传输视频",Toast.LENGTH_SHORT).show();
        DoorAccessManager.getInstance().updateCallWindow(sessionId,surfaceView);
        DoorAccessManager.getInstance().enableLocalPlayer(DoorAccess2MainActivity.familyID,sessionID,false);
        DoorAccessManager.getInstance().enableLocalMic(DoorAccess2MainActivity.familyID,sessionID,false);
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
        else if(TextUtils.equals(event.getCmd(),IntercomConstants.kIntercomCommandMessage)){
            if(!TextUtils.equals(sessionId,event.sessionId)){
                Toast.makeText(this,"[收到非本人消息]" + event.message.getContent(),Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"[收到消息]" + event.message.getContent(),Toast.LENGTH_SHORT).show();
            }
        }
        else if(TextUtils.equals(event.getCmd(),IntercomConstants.kIntercomCommandPickup)){
            DoorAccessManager.getInstance().updateCallWindow(sessionId,surfaceView);
            DoorAccessManager.getInstance().enableLocalPlayer(DoorAccess2MainActivity.familyID,sessionId,true);
        }

    }

    /**
     * 单次会话中，可以通过调用 startRecord 和 stopRecord 来实现录制、停止录制视频
     */
    public String recordSession = "";
    public void startRecord(){
        recordSession = DoorAccessManager.getInstance().startRecord(DoorAccess2MainActivity.familyID,sessionId);
    }

    public void stopRecord(){
        DoorAccessManager.getInstance().stopRecord(DoorAccess2MainActivity.familyID,sessionId,recordSession);
    }

    @Override
    public int inviteIntercept(DoorEvent inviteEvent) {
        return 0;
    }

    @Override
    public void onVideoDimen(String familyId, String sessionId, int width, int height) {
        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
        float max = getResources().getDisplayMetrics().widthPixels;
        float bili = (float)width / (float)height;
        if(bili > 1){
            params.width = (int) max;
            params.height = (int) (max / bili);
        }else{
            params.width = (int) (max * bili);
            params.height = (int) bili;
        }
//        params.width = width;
//        params.height = height;
        surfaceView.setLayoutParams(params);
    }

    @Override
    public void onCaptureDimensionChanged(int width, int height) {
        ViewGroup.LayoutParams params = localSurface.getLayoutParams();
        float scale = (float)getResources().getDisplayMetrics().density / 240f;
        float max = 150 * scale;
        float bili = (float)width / (float)height;
        if(bili > 1){
            params.width = (int) max;
            params.height = (int) (max / bili);
        }else{
            params.width = (int) (max * bili);
            params.height = (int) bili;
        }

//        params.width = width;
//        params.height = height;
        localSurface.setLayoutParams(params);
    }

    @Override
    public void onCaptureDeviceError(int err, String from_here, String reason) {

    }

    @Override
    public void onCaptureDeviceStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
    }
}
