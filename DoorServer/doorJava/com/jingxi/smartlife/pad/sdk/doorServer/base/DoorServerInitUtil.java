package com.jingxi.smartlife.pad.sdk.doorServer.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.intercom.base.Log;
import com.intercom.sdk.DeployServer;
import com.intercom.sdk.IntercomServerManager;
import com.intercom.sdk.SystemProvision;
import com.jingxi.smartlife.pad.sdk.doorServer.base.observers.DoorServerDeployListener;
import com.jingxi.smartlife.pad.sdk.doorServer.base.observers.ServerAppEventListener;
import com.jingxi.smartlife.pad.sdk.doorServer.base.observers.ServerDeployHandler;
import com.jingxi.smartlife.pad.sdk.doorServer.base.observers.ServerIntercomEventListener;
import com.jingxi.smartlife.pad.sdk.doorServer.base.observers.ServerProvisionEventListener;
import com.jingxi.smartlife.pad.sdk.doorServer.base.observers.ServerProxyEventListener;
import com.jingxi.smartlife.pad.sdk.doorServer.base.utils.ServerFileUtils;

public class DoorServerInitUtil {
    private Context context;

    private static DoorServerInitUtil instance;
    private IntercomServerManager serverManager = null;
    private SystemProvision provisionManager = null;
    private DeployServer deployServer;

    private ServerAppEventListener appListener;
    private ServerIntercomEventListener intercomListener;
    private ServerProxyEventListener proxyListener;
    private ServerProvisionEventListener provisionEventListener;

    private DoorServerDeployListener serverDeployListener;

    public static DoorServerInitUtil getInstance() {
        if (instance == null) {
            synchronized (DoorServerInitUtil.class) {
                if (instance == null) {
                    instance = new DoorServerInitUtil();
                }
            }
        }
        return instance;
    }

    public void initServerManager(Context context){
        this.context = context;

        appListener = new ServerAppEventListener();
        intercomListener = new ServerIntercomEventListener();
        proxyListener = new ServerProxyEventListener();
        provisionEventListener = new ServerProvisionEventListener();

        ServerFileUtils.checkDefaultFile();

        PackageManager packageManager = context.getPackageManager();
        String version = "";
        try {
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(),0);
            version = TextUtils.concat(info.versionName,".",String.valueOf(info.versionCode)).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        IntercomServerManager.globalInitialize(context,AppSettings.getAppConf(version).toJson());
        provisionManager = new SystemProvision(provisionEventListener);
        try {
            enableMulticast(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getDeployServer().start("");
        getDeployServer().enableUpdate(true);
        initServer();
    }

    public void setServerDeployListener(DoorServerDeployListener serverDeployListener){
        this.serverDeployListener = serverDeployListener;
    }

    public DoorServerDeployListener getServerDeployListener() {
        return serverDeployListener;
    }

    public void uninitServer(){
        unInitServer();
        getDeployServer().enableUpdate(false);
        getDeployServer().stop();
        getDeployServer().release();
        getProvisionManager().stop();
        getProvisionManager().release();
    }

    private static void enableMulticast(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock multicastLock = wifiManager.createMulticastLock("multicast.test");
        multicastLock.acquire();
    }

    public Context getContext() {
        return context;
    }

    public strictfp DeployServer getDeployServer() {
        if(deployServer == null){
            deployServer = new DeployServer(new ServerDeployHandler());
        }
        return deployServer;
    }

    public void initServer() {
        Log.w("ServerApplication","isProvisioning = " + SystemProvision.isSystemProvisioning(AppSettings.WORK_DIR));
        if (!SystemProvision.isSystemProvisioning(AppSettings.WORK_DIR)) {
            return;
        }

        if (serverManager == null) {
            serverManager = new IntercomServerManager();
            serverManager.app_listener.add(appListener);
            serverManager.proxy_listener.add(proxyListener);
            serverManager.intercom_listener.add(intercomListener);
        } else {
            return;
        }

        serverManager.start(AppSettings.getClientConf());
    }

    public void unInitServer() {
        if (serverManager == null) {
            return;
        }
        serverManager.app_listener.remove(appListener);
        serverManager.proxy_listener.remove(proxyListener);
        serverManager.intercom_listener.remove(intercomListener);
        serverManager.stop();
        serverManager = null;
    }

    public IntercomServerManager getIntercomManager() {
        return serverManager;
    }

    public SystemProvision getProvisionManager() {
        return provisionManager;
    }
}
