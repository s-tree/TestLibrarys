package com.jingxi.smartlife.pad.sdk.doorServer.base.observers;

import com.alibaba.fastjson.JSON;
import com.intercom.sdk.IntercomObserver;
import com.intercom.sdk.NetClient;
import com.jingxi.smartlife.pad.sdk.doorServer.base.utils.LogUtil;

/**
 * @author bjj
 */
public class ServerProxyEventListener implements IntercomObserver.OnProxyEventListener {
    private static final String TAG = ServerProxyEventListener.class.getSimpleName();

    @Override
    public void onInternetStateChanged(boolean online) {
        LogUtil.v(TAG,"[onInternetStateChanged] online = " + online);
    }

    @Override
    public void onClientStateChanged(int router, boolean online, NetClient netClient) {
        LogUtil.v(TAG,"[onClientStateChanged] router = " + router + " online = " + online + " net_client = " + JSON.toJSONString(netClient));
    }
}
