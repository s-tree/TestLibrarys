package com.jingxi.smartlife.pad.sdk.doorServer;

import android.content.Context;

import com.jingxi.smartlife.pad.sdk.doorServer.base.observers.DoorServerDeployListener;

public abstract class DoorServerManager {
    private static DoorServerManager instance;

    public static DoorServerManager getInstance(){
        if(instance == null){
            synchronized (DoorServerManager.class){
                if(instance == null){
                    instance = new DoorServerManagerImpl();
                }
            }
        }
        return instance;
    }

    public abstract void initServer(Context context);

    public abstract void uninitServer();

    public abstract void saveAppVersions(String mainAppVersion,
                                         String intercomAppVersion,
                                         String serverAppVersion);

    public abstract void setServerDeployListener(DoorServerDeployListener serverDeployListener);

    public abstract void enableDeploy(boolean isEnabled);

    public abstract boolean isSystemProvisioning();
}
