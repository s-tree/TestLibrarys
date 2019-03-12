
package com.jingxi.smartlife.pad.sdk.neighbor.ui.views.bgabadge;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/7/12 下午6:33
 * 描述:拖动大于BGABadgeViewHelper.mMoveHiddenThreshold后抬起手指徽章消失的代理
 */
public interface BGADragDismissDelegate {

    /**
     * 拖动大于BGABadgeViewHelper.mMoveHiddenThreshold后抬起手指徽章消失的回调方法
     */
    void onDismiss(BGABadgeable badgeable);
}