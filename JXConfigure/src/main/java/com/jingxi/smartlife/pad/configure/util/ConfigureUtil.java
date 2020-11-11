package com.jingxi.smartlife.pad.configure.util;

import android.os.Build;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.jingxi.smartlife.pad.configure.ConfigureFinal;
import com.jingxi.smartlife.pad.configure.bean.CommunityInfo;

import java.io.Closeable;
import java.io.IOException;

public class CommunityUtil {

    public static <T> T parseObject(String data,Class<T> clazz){
        if(TextUtils.isEmpty(data)){
            return null;
        }
        try{
            return JSON.parseObject(data,clazz);
        }catch (Exception e){
            ConfigureLogUtil.logV("[CommunityUtil.parseObject]","parseObject failed ");
            e.printStackTrace();
            return null;
        }
    }

    public static CommunityInfo parseCommunityInfo(String data){
        if(TextUtils.isEmpty(data)){
            ConfigureLogUtil.logV("[CommunityUtil.parseCommunityInfo]","data is empty");
            return null;
        }
        CommunityInfo communityInfo = parseObject(data,CommunityInfo.class);
        ConfigureFinal.communityInfo = communityInfo;
        return communityInfo;
    }

    public static void closeIO(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getSn() {
        return Build.SERIAL;
    }
}
