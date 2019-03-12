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
        IACCSManager manager = ACCSManager.getAccsInstance(this, "23815601", "test_tag");
        manager.sendData(this,buildAccsRequest());


    }

    @Override
    public void onReceiverMessage(String content) {
        // todo Received Message
    }

    private ACCSManager.AccsRequest buildAccsRequest(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dataType","pingreq");
        jsonObject.put("timeInterval",3600*1000);
        ACCSManager.AccsRequest accsRequest = new ACCSManager.AccsRequest(null,null,jsonObject.toJSONString().getBytes(), UUID.randomUUID().toString());
        accsRequest.setTarget("accs-iot");
        accsRequest.setTargetServiceName("sal");
        return accsRequest;
        //see NotifManager
//        HashMap var3;
//        (var3 = new HashMap()).put("api", "agooAck");
//        var3.put("id", var1.a + "@" + var1.e);
//        if (!TextUtils.isEmpty(var1.c)) {
//            var3.put("del_pack", var1.c);
//        }
//
//        if (!TextUtils.isEmpty(var1.d)) {
//            var3.put("ec", var1.d);
//        }
//
//        if (!TextUtils.isEmpty(var1.f)) {
//            var3.put("type", var1.f);
//        }
//
//        if (!TextUtils.isEmpty(var1.b)) {
//            var3.put("ext", var1.b);
//        }
//
//        var3.put("appkey", "23815601");
//        var3.put("utdid", a.b(this));
//        byte[] var6 = (new JSONObject(var3)).toString().getBytes("UTF-8");
    }
}
