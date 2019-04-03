package com.jingxi.smartlife.pad.sdk.demo.push;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.push.OnPushedListener;
import com.jingxi.smartlife.pad.sdk.push.PushManager;
import com.taobao.accs.ACCSManager;
import com.taobao.accs.IACCSManager;

import java.util.UUID;

public class PushMainActivity extends AppCompatActivity implements OnPushedListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JXPadSdk.init(getApplication());
        JXPadSdk.initPushManager();
        PushManager pushManager = JXPadSdk.getPushManager();
        pushManager.addCallback(this);
        pushManager.bindAccount("accid_111");
        pushManager.bindTags("accid_111","test_tag","tag2","tag3");
    }

    @Override
    public void onReceiverMessage(String content) {
        // todo Received Message
    }

    /**
     * @hide
     * 此方法不用
     * @return
     */
    private ACCSManager.AccsRequest buildAccsRequest(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dataType","pingreq");
        jsonObject.put("timeInterval",3600*1000);
        ACCSManager.AccsRequest accsRequest = new ACCSManager.AccsRequest(null,null,jsonObject.toJSONString().getBytes(), UUID.randomUUID().toString());
        accsRequest.setTarget("accs-iot");
        accsRequest.setTargetServiceName("sal");
        return accsRequest;
    }
}
