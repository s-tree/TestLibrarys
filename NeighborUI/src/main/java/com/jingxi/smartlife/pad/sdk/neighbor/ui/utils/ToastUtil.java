package com.jingxi.smartlife.pad.sdk.neighbor.ui.utils;

import android.text.TextUtils;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.MyToast;

public class ToastUtil {
    private static final ToastUtil instance = new ToastUtil();

    private ToastUtil() {
    }

    public static ToastUtil getInstance() {
        return instance;
    }

    public static void showToast(String text) {
        if (TextUtils.isEmpty(text)){
            return;
        }
        try {
            MyToast.showText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
