package com.jingxi.smartlife.pad.sdk.demo.dooraccess;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.intercom.sdk.SecurityMessage;
import com.intercom.sdk.SmartHomeManager;
import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.demo.R;
import com.jingxi.smartlife.pad.sdk.doorAccess.DoorAccessManager;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.DoorSecurityUtil;

import java.util.List;

public class DoorAccessSecurityActivity extends AppCompatActivity implements DoorSecurityUtil.OnSecurityChangedListener{
    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dooraccess_list);
        textView = (TextView) findViewById(R.id.textView);
        JXPadSdk.getDoorAccessManager().addSecurityListener(this);
        DoorAccessManager doorAccessManager = JXPadSdk.getDoorAccessManager();
        /**
         * 查询安防状态
         */
        doorAccessManager.querySecurityStatus(DoorAccessMainActivity.familyID);
        /**
         * 取消报警
         */
        doorAccessManager.cancelSecurityWarning(DoorAccessMainActivity.familyID);
    }

    public void toPlayBack(View v){
    }

    public void toSecurity(View v){
    }

    public void callP2P(View v){
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        JXPadSdk.getDoorAccessManager().removeSecurityListener(this);
    }

    /**
     * 安防状态变更
     * @param familyDockSn
     * @param state
     * @param isFromQuery
     */
    @Override
    public void onStateChanged(String familyDockSn, int state, boolean isFromQuery) {
        textView.setText("安防状态变更： " + familyDockSn + " 状态 ： " + state + " isFromQuery = " + isFromQuery);
    }

    /**
     * 安防设备报警
     * @param familyDockSn
     * @param stateBeans
     * @param device
     */
    @Override
    public void onAlarm(String familyDockSn, List<SecurityMessage.StateBean> stateBeans, SmartHomeManager.SecurityDevice device) {
        textView.setText("安防设备报警 ： " + familyDockSn + " 设备 " + stateBeans.get(0).getAlias());
    }

    /**
     * 安防取消报警回调
     * @param familyDockSn
     * @param device
     */
    @Override
    public void onCancelAlarm(String familyDockSn, SmartHomeManager.SecurityDevice device) {
        textView.setText("防区解除报警 ： " + familyDockSn);
    }
}
