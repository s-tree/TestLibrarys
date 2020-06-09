package com.jingxi.smartlife.pad.sdk.doorServer;

import android.content.Context;

import com.jingxi.smartlife.pad.sdk.doorServer.base.DoorServerInitUtil;
import com.jingxi.smartlife.pad.sdk.doorServer.base.DoorServerManagerUtil;
import com.jingxi.smartlife.pad.sdk.doorServer.base.observers.DoorServerDeployListener;
import com.jingxi.smartlife.pad.sdk.doorServer.base.utils.ServerFileUtils;

public class DoorServerManagerImpl extends DoorServerManager {
    @Override
    public void initServer(Context context) {
        DoorServerInitUtil.getInstance().initServerManager(context);
    }

    @Override
    public void uninitServer() {
        DoorServerInitUtil.getInstance().uninitServer();
    }

    @Override
    public void saveAppVersions(String mainAppVersion, String intercomAppVersion, String serverAppVersion) {
        ServerFileUtils.writeAppVersions(mainAppVersion,intercomAppVersion,serverAppVersion);
    }

    @Override
    public void setServerDeployListener(DoorServerDeployListener serverDeployListener) {
        DoorServerInitUtil.getInstance().setServerDeployListener(serverDeployListener);
    }

    @Override
    public void enableDeploy(boolean isEnabled) {
        DoorServerManagerUtil.enableDeploy(isEnabled);
    }

    @Override
    public boolean isSystemProvisioning() {
        return DoorServerManagerUtil.isSystemProvisioning();
    }
}
