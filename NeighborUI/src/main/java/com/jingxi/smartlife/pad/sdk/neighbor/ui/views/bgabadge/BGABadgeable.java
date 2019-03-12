
package com.jingxi.smartlife.pad.sdk.neighbor.ui.views.bgabadge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.ViewParent;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/7/6 下午10:42
 * 描述:
 */
public interface BGABadgeable {
    /**
     * 显示圆点徽章
     */
    void showCirclePointBadge();

    /**
     * 显示文字徽章
     */
    void showTextBadge(String badgeText);

    /**
     * 隐藏徽章
     */
    void hiddenBadge();

    /**
     * 显示图像徽章
     */
    void showDrawableBadge(Bitmap bitmap);

    /**
     * 调用父类的onTouchEvent方法
     */
    boolean callSuperOnTouchEvent(MotionEvent event);

    /**
     * 拖动大于BGABadgeViewHelper.mMoveHiddenThreshold后抬起手指徽章消失的代理
     */
    void setDragDismissDelegage(BGADragDismissDelegate delegate);

    int getWidth();

    int getHeight();

    void postInvalidate();

    ViewParent getParent();

    int getId();

    boolean getGlobalVisibleRect(Rect r);

    Context getContext();
}