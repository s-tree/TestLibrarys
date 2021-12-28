package com.jingxi.smartlife.pad.sdk.demo.dooraccess;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;

import com.intercom.service.RecordPlayer;
import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.demo.R;
import com.jingxi.smartlife.pad.sdk.doorAccess.DoorAccessManager;

/**
 * 回放主页
 */
public class DoorAccess2PlayBackActivity extends AppCompatActivity implements
        SurfaceHolder.Callback,SeekBar.OnSeekBarChangeListener,RecordPlayer.RecordPlayerHandler{
    private SeekBar seekBar;
    private SurfaceView surfaceView;
    private String sessionId;
    private String videoPath;
    private DoorAccessManager doorAccessManager;
    private int seek;
    private int max;
    /**
     * listener 中返回的value 单位是 微秒
     */
    private final long unit = 1000 * 1000;

    /**
     * 是否在滑动进度条
     */
    private boolean isTouchSeek = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doorAccessManager = JXPadSdk.getDoorAccessManager();

        setContentView(R.layout.layout_activity_playback);
        sessionId = getIntent().getStringExtra("sessionId");
        videoPath = getIntent().getStringExtra("videoPath");
        initView();

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                0);
        audioManager.setSpeakerphoneOn(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doorAccessManager.removePlayBackListener(this);
    }

    private void initView(){
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        surfaceView = (SurfaceView) findViewById(R.id.surface);
    }

    public void start(View v){
//        doorAccessManager.startPlayBack(this,sessionId,"",1);
        doorAccessManager.startPlayBack(this,sessionId,videoPath,1);
        doorAccessManager.switchPlaybackAudio(this,true);
        doorAccessManager.enablePlaybackAudioTrack(this,true,true);
        doorAccessManager.enablePlaybackAudioTrack(this,false,true);
    }

    public void resume(View v){
        int tempSeek = seek;
        doorAccessManager.startPlayBack(this,sessionId,videoPath,1);
        doorAccessManager.seekPlayBack(sessionId,tempSeek * 100 / max);
        doorAccessManager.switchPlaybackAudio(this,true);
    }

    public void stop(View v){
        doorAccessManager.pausePlayBack(sessionId);
    }

    public void updateSurface(View v){
        doorAccessManager.updatePlayBackWindow(sessionId,surfaceView);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        seek = i;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isTouchSeek = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isTouchSeek = false;
        doorAccessManager.seekPlayBack(sessionId,seek * 100 / max);
    }

    @Override
    public void onMediaDuration(String session_id, long duration) {
        max = (int) (duration / unit);
        seekBar.setMax(max);
    }

    @Override
    public void onMediaProgress(String session_id, long progress) {
        seek = (int) (progress / unit);
        if(isTouchSeek){
            return;
        }
        seekBar.setOnSeekBarChangeListener(null);
        seekBar.setProgress(seek);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onMediaCompleted(String session_id, int reason) {
        seek = max;
        seekBar.setOnSeekBarChangeListener(null);
        seekBar.setProgress(max);
        seekBar.setOnSeekBarChangeListener(this);
        doorAccessManager.pausePlayBack(sessionId);
    }

    @Override
    public void onMediaVideoDimensionChanged(String session_id, int width, int height) {

    }
}
