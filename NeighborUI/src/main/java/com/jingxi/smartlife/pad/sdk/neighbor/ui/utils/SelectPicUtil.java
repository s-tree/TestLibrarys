package com.jingxi.smartlife.pad.sdk.neighbor.ui.utils;

import android.app.Activity;
import android.view.Gravity;
import android.view.WindowManager;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.SelectPicPopupWindow;

/**
 * 照片选择器
 *
 * @author engrh
 */
public class SelectPicUtil {
    private SelectPicPopupWindow menuWindow;
    private int selectPicCount = 0;
    private Activity activity;

    public SelectPicUtil(Activity _activity) {
        this.activity = _activity;
        menuWindow = new SelectPicPopupWindow(activity,
                new SelectPicPopupWindow.Callback() {
                    @Override
                    public void onTakePhoto() {
                        CameraUtil.goToCam(activity);
                    }

                    @Override
                    public void onPickPhoto() {
                        CameraUtil.pickPhoto(activity);
                    }
                });
        menuWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    /**
     * @param selectPicCount 选择的照片数，单选传1
     */
    public void show(int selectPicCount) {
        if (menuWindow == null) {
            return;
        }
        this.selectPicCount = selectPicCount;
        menuWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


    public void dismiss() {
        menuWindow.destroy();
        menuWindow.dismiss();
        menuWindow = null;
        activity = null;
    }
}
