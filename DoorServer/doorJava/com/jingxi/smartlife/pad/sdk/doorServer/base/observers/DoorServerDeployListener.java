package com.jingxi.smartlife.pad.sdk.doorServer.base.observers;

public interface DoorServerDeployListener {

    /**
     * 登录部署服务器
     * @param url
     * @param connected
     */
    void onNetConnectStateChanged(String url, boolean connected);

    /**
     * 登录成功部署服务器
     * @param url
     * @param success
     * @param code
     * @param errmsg
     */
    void onNetLoginCompleted(String url, boolean success, int code, String errmsg);

    /**
     * 代理更新了配置，此时需要重启
     * @param reason
     */
    void onNetReboot(String reason);

    /**
     * 代理开始部署
     * @param url
     */
    void onNetUpdateBegin(String url);

    /**
     * 代理部署/配置更新成功，reboot 为true 时需要重启
     * @param result
     * @param reboot
     * @param room_number
     */
    void onNetUpdateCompleted(boolean result, boolean reboot, String room_number);
}
