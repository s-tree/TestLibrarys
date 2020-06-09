package com.jingxi.smartlife.pad.sdk.doorServer.base.observers;

import com.alibaba.fastjson.JSON;
import com.intercom.sdk.IntercomObserver;
import com.intercom.sdk.NetClient;
import com.jingxi.smartlife.pad.sdk.doorServer.base.utils.LogUtil;

/**
 * @author bjj
 */
public class ServerIntercomEventListener implements IntercomObserver.OnIntercomEventListener {
    private static final String TAG = ServerIntercomEventListener.class.getSimpleName();

    @Override
    public void onSipStateChanged(String identity, int state) {
        LogUtil.v(TAG,"[onSipStateChanged] identity = " + identity + " state = " + state);
    }

    @Override
    public void onIntercomSessionCreated(int dir, String deviceId, String sessionId, String username) {
        LogUtil.v(TAG,"[onIntercomSessionCreated] dir = " + dir + " device_id = " + deviceId + " sessionID = " + sessionId + " username = " + username);
    }

    @Override
    public void onIntercomSessionStateChanged(String sessionId, int status, String command, int err) {
        LogUtil.v(TAG,"[onIntercomSessionStateChanged] session_id = " + sessionId + " status = " + status + " command = " + command + " err = " + err);
    }

    @Override
    public void onIntercomSessionClientStateChanged(String sessionId, int router, NetClient netClient, int status) {
        LogUtil.v(TAG,"[onIntercomSessionClientStateChanged] session_id = " + sessionId + " router = " + router + " status = " + status + " netClient = " + JSON.toJSONString(netClient));
    }

    @Override
    public void onIntercomClientStreamStateChanged(int type, String sessionId, String userId, int state) {
        LogUtil.v(TAG,"[onIntercomClientStreamStateChanged] type = " + type + " session_id = " + sessionId + " user_id = " + userId + " state = " + state);
    }

    @Override
    public void onIntercomTransportStarted(int type, String sessionId, String userId) {
        LogUtil.v(TAG,"[onIntercomTransportStarted] type = " + type + " session_id = " + sessionId + " user_id = " + userId);
    }

    @Override
    public void onIntercomTransportIceNegotiationResult(int type, String sessionId, String userId, int result) {
        LogUtil.v(TAG,"[onIntercomTransportIceNegotiationResult] type = " + type + " session_id = " + sessionId + " user_id = " + userId);

    }
}
