package com.jingxi.smartlife.pad.sdk.neighbor.ui.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;

import java.lang.reflect.Field;

public class DisplayUtil {

    public static int dip2px(float dpValue) {
        final float scale = JXContextWrapper.context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getScreanWidth() {
        WindowManager windowManager = (WindowManager) JXContextWrapper.context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    public static int getScreanHeight() {
        WindowManager windowManager = (WindowManager) JXContextWrapper.context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        float density = JXContextWrapper.context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / density + 0.5f);
    }

    private static int getStatusBarHeight(Context context) {
        Class<?> c;
        Object obj;
        Field field;
        int x, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    //获取除去状态栏的高度
    public static int getContentViewHeight() {
        int screenHeight = getScreanHeight();
        return screenHeight - getStatusBarHeight(JXContextWrapper.context);
    }

}
