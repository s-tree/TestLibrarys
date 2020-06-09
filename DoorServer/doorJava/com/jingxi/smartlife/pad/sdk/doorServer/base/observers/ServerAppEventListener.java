package com.jingxi.smartlife.pad.sdk.doorServer.base.observers;

import com.intercom.sdk.IntercomObserver;
import com.jingxi.smartlife.pad.sdk.doorServer.base.utils.LogUtil;

/**
 * @author bjj
 */
public class ServerAppEventListener implements IntercomObserver.OnIntercomAppEventListener {
    private static final String TAG = ServerAppEventListener.class.getSimpleName();

    @Override
    public void onAppInitialized(int result) {
        if(result == 0){
            /**
             * 已经部署完成
             */
        }
        LogUtil.v(TAG,"[onAppInitialized] result = " + result);
    }
}
