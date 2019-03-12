package com.jingxi.smartlife.pad.sdk.demo.dooraccess;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;

import com.intercom.sdk.IntercomConstants;
import com.intercom.sdk.IntercomObserver;
import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.demo.R;
import com.jingxi.smartlife.pad.sdk.doorAccess.DoorAccessManager;
import com.jingxi.smartlife.pad.sdk.utils.JXLogUtil;

/**
 * 回放主页
 */
public class DoorAccessPlayBackActivity extends AppCompatActivity implements
        SurfaceHolder.Callback,SeekBar.OnSeekBarChangeListener ,IntercomObserver.OnPlaybackListener {
    private SeekBar seekBar;
    private SurfaceView surfaceView;
    private String sessionId;
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
        doorAccessManager.addPlayBackListener(this);

        setContentView(R.layout.layout_activity_playback);
        sessionId = getIntent().getStringExtra("sessionId");
        initView();
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
        doorAccessManager.startPlayBack(sessionId);
    }

    public void resume(View v){
        int tempSeek = seek;
        doorAccessManager.startPlayBack(sessionId);
        doorAccessManager.seekPlayBack(sessionId,tempSeek * 100 / max);
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
    public void onMediaPlayEvent(String session_id, int event, long value) {
        if(event == IntercomConstants.MediaPlayEvent.MediaPlayEventDuration){
            max = (int) (value / unit);
            seekBar.setMax(max);
        }
        else if(event == IntercomConstants.MediaPlayEvent.MediaPlayEventProgress){
            seek = (int) (value / unit);
            if(isTouchSeek){
                return;
            }
            seekBar.setOnSeekBarChangeListener(null);
            seekBar.setProgress(seek);
            seekBar.setOnSeekBarChangeListener(this);
        }
        else if(event == IntercomConstants.MediaPlayEvent.MediaPlayEventCompleted){
            seek = max;
            seekBar.setOnSeekBarChangeListener(null);
            seekBar.setProgress(max);
            seekBar.setOnSeekBarChangeListener(this);
            doorAccessManager.pausePlayBack(sessionId);
        }
    }
}
