package com.jingxi.smartlife.pad.sdk.doorServer.base.observers;

import com.intercom.base.Log;
import com.intercom.sdk.SystemProvision;
import com.jingxi.smartlife.pad.sdk.doorServer.base.AppSettings;
import com.jingxi.smartlife.pad.sdk.doorServer.base.DoorServerInitUtil;

public class ServerProvisionEventListener implements SystemProvision.SystemProvisionHandler {
    private static final String TAG = "ServerProvisionEventListener";

    @Override
    public void onSystemProvisionCompleted(int err,boolean reboot, String room_number) {
        Log.w(TAG,"onProvisioningCompleted err = " + err + " room_number = " + room_number + " reboot = " + reboot);
        if(err == SystemProvision.Result_Success){
            /**
             * 部署完成
             * called AppSettings.setEth0()
             */
            try {
                reboot |= AppSettings.setEth0();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(reboot){
                //reboot
                if(DoorServerInitUtil.getInstance().getServerDeployListener() != null){
                    DoorServerInitUtil.getInstance().getServerDeployListener().onNetReboot("");
                }
            }
        }
    }

}
