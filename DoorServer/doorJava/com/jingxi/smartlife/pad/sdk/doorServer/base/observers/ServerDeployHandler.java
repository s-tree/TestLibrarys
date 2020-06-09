package com.jingxi.smartlife.pad.sdk.doorServer.base.observers;

import com.intercom.base.Log;
import com.intercom.sdk.DeployServer;
import com.jingxi.smartlife.pad.sdk.doorServer.base.DoorServerInitUtil;

public class ServerDeployHandler implements DeployServer.DeployServerHandler {
    private static final String TAG = "ServerDeployHandler";

    @Override
    public void onNetConnectStateChanged(String url, boolean connected) {
        Log.w(TAG,"onNetConnectStateChanged url = " + url + " connected = " + connected) ;
        if(DoorServerInitUtil.getInstance().getServerDeployListener() != null){
            DoorServerInitUtil.getInstance().getServerDeployListener().onNetConnectStateChanged(url,connected);
        }
    }

    @Override
    public void onNetLoginCompleted(String url, boolean success, int code, String errmsg) {
        Log.w(TAG,"onNetLoginCompleted url = " + url + " success = " + success + " code = " + code + " errmsg = " + errmsg) ;
        if(DoorServerInitUtil.getInstance().getServerDeployListener() != null){
            DoorServerInitUtil.getInstance().getServerDeployListener().onNetLoginCompleted(url,success,code,errmsg);
        }
    }

    @Override
    public void onNetReboot(String reason) {
        Log.w(TAG,"onNetReboot reason = " + reason) ;
        /**
         * needReboot
         */
        if(DoorServerInitUtil.getInstance().getServerDeployListener() != null){
            DoorServerInitUtil.getInstance().getServerDeployListener().onNetReboot(reason);
        }
    }

    @Override
    public void onNetUpdateBegin(String url) {
        Log.w(TAG,"onNetUpdateBegin url = " + url) ;
        if(DoorServerInitUtil.getInstance().getServerDeployListener() != null){
            DoorServerInitUtil.getInstance().getServerDeployListener().onNetUpdateBegin(url);
        }
    }

    @Override
    public void onNetUpdateCompleted(boolean result, boolean reboot, String room_number) {
        Log.w(TAG,"onNetUpdateCompleted result = " + result + " reboot = " + reboot + " room_number = " + room_number) ;
        if(DoorServerInitUtil.getInstance().getServerDeployListener() != null){
            DoorServerInitUtil.getInstance().getServerDeployListener().onNetUpdateCompleted(result,reboot,room_number);
        }
    }
}
