package com.jingxi.smartlife.pad.sdk.doorServer.base;

import android.os.Build;
import android.text.TextUtils;

import com.intercom.base.Log;
import com.intercom.sdk.IntercomConfigure;
import com.intercom.sdk.IntercomConstants;
import com.jingxi.smartlife.pad.sdk.doorServer.base.utils.EthernetUtilV25;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class AppSettings {
    public static final String TAG = "AppSettings";

    protected static final String DEFAULT_SIP_URL = "http://sip.house-keeper.cn/opensip/v2/register";
    protected static final String DEFAULT_DEPLOY_URL = "http://install.house-keeper.cn:42333";
    protected static final String DEFAULT_DEPLOY_KEY = "19880306";
    protected static final String DEFAULT_TRANSIT_URL = "http://transit.house-keeper.cn/keepalive";

    public static String clientId = "";
    public static String channel = "jx";
    public static String familyId = "";
    public static String buttonKey = AppFinals.DEFAULT_BUTTON_KEY;
    public static String alias = AppFinals.DEFAULT_ALIAS;
    public static String publishId = "";
    public static boolean logToFile = true;
    public static final String WORK_DIR = "sdcard/data/doorkeeper/server/";
    public static final String WORK_DIR_CONF = "sdcard/data/doorkeeper/server/conf";
    public static final String ANDOIRD_INI = "android.ini";
    public static final String WORK_DEBUG = WORK_DIR + "debug";
    public static final String CloudFile = WORK_DIR + "conf/enable_cloud.dat";
    public static final String ETH0_PATH = WORK_DIR + "update/temp/eth0.txt";

    public static final String SIP_PATH = WORK_DIR_CONF + "/sip_auth.ini";
    public static final String TRANSIT_PATH = WORK_DIR_CONF + "/inet.ini";
    public static final String DEPLOY_PATH = WORK_DIR_CONF + "/server.json";

    protected static DoorServerKit.Options options = DoorServerKit.Options.getDefault();

    public static IntercomConfigure.AppConf getAppConf(String appVersion){
        int logLevel = IntercomConstants.LogLevel.Fatal;
        if(new File(WORK_DEBUG).exists() || options.isDebug){
            logLevel = IntercomConstants.LogLevel.Info;
        }
        return new IntercomConfigure.AppConf(
                appVersion,
                options.channel,
                Build.SERIAL,
                AppSettings.WORK_DIR,
                AppSettings.logToFile,
                logLevel
        );
    }

    public static String getClientConf(){
        IntercomConfigure.ClientConf clientConf = new IntercomConfigure.ClientConf(
                IntercomConstants.NetClientType.NetClientTypeProxy,
                IntercomConstants.NetProxySubType.NetProxySubTypeIndoor,
                AppSettings.familyId,
                AppSettings.buttonKey,
                AppSettings.alias,
                AppSettings.publishId,
                Build.SERIAL);

        return clientConf.toJson();
    }

    public static boolean setEth0() throws Exception{
        File eth0File = new File(AppSettings.ETH0_PATH);
        if(!eth0File.exists()){
            return false;
        }
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream(eth0File);
        properties.load(inputStream);
        String ip = properties.getProperty(AppFinals.ETH0_IP);
        if (TextUtils.isEmpty(ip)) {
            Log.w(TAG,"eth0 ip isEmpty");
            return false;
        }
        String mask = properties.getProperty(AppFinals.ETH0_NETMASK);
        if (TextUtils.isEmpty(mask)) {
            Log.w(TAG,"eth0 mask isEmpty");
            return false;
        }
        String gateway = properties.getProperty(AppFinals.ETH0_GATEWAY);
        if (TextUtils.isEmpty(gateway)) {
            Log.w(TAG,"eth0 gateway isEmpty");
            return false;
        }
        String dns = properties.getProperty(AppFinals.ETH0_DNS);
        if (TextUtils.isEmpty(dns)) {
            Log.w(TAG,"eth0 dns isEmpty");
            return false;
        }
        EthernetUtilV25.setEthStaticIp(ip,EthernetUtilV25.getMaskLength(mask),gateway,dns);
        inputStream.close();
        return true;
    }
}
