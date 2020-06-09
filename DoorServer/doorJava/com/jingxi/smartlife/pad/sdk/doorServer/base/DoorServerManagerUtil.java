package com.jingxi.smartlife.pad.sdk.doorServer.base;

import com.intercom.sdk.SystemProvision;

public class DoorServerManagerUtil {

    public static void enableDeploy(boolean isEnabled){
        DoorServerInitUtil.getInstance().getDeployServer().enableUpdate(isEnabled);
    }

    public static boolean isSystemProvisioning(){
        return SystemProvision.isSystemProvisioning(AppSettings.WORK_DIR);
    }
}
