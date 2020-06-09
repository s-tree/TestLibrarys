package com.jingxi.smartlife.pad.sdk.doorServer.base.utils;

import android.text.TextUtils;

import com.intercom.base.Log;
import com.jingxi.smartlife.pad.sdk.doorServer.base.DoorServerKit;

/**
 * @author bjj
 */
public class LogUtil {
    private static final String TAG = "DoorServer";
    private static boolean isDebug = DoorServerKit.getOptions().isDebug;
    private static final int LOG_V = 0;
    private static final int LOG_W = 1;
    private static final int LOG_E = 2;

    public static void w(String tag,String message){
        log(LOG_W,TAG, TextUtils.concat("[",tag,"] ",message).toString());
    }

    public static void v(String tag,String message){
        log(LOG_V,TAG, TextUtils.concat("[",tag,"] ",message).toString());
    }

    private static void log(int method,String tag,String message){
        if(!isDebug){
            return;
        }
        if(method == LOG_V){
            Log.v(tag,message);
        }
        else if(method == LOG_W){
            Log.w(tag,message);
        }
        else if(method == LOG_E){
            Log.e(tag,message);
        }
    }
}
