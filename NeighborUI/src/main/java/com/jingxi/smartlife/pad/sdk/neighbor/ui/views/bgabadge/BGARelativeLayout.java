package com.jingxi.smartlife.pad.sdk.neighbor.ui.views.bgabadge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2017/5/15.
 */

public class BGARelativeLayout extends RelativeLayout implements BGABadgeable {
    private BGABadgeViewHelper mBadgeViewHeler;

    public BGARelativeLayout(Context context) {
        this(context, null, 0);
    }

    public BGARelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BGARelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBadgeViewHeler = new BGABadgeViewHelper(this, context, attrs, BGABadgeViewHelper.BadgeGravity.RightTop);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mBadgeViewHeler.onTouchEvent(event);
    }

    @Override
    public boolean callSuperOnTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBadgeViewHeler.drawBadge(canvas);
    }

    @Override
    public void showCirclePointBadge() {
        mBadgeViewHeler.showCirclePointBadge();
    }

    @Override
    public void showTextBadge(String badgeText) {
        mBadgeViewHeler.showTextBadge(badgeText);
    }

    @Override
    public void hiddenBadge() {
        mBadgeViewHeler.hiddenBadge();
    }

    @Override
    public void showDrawableBadge(Bitmap bitmap) {
        mBadgeViewHeler.showDrawable(bitmap);
    }

    @Override
    public void setDragDismissDelegage(BGADragDismissDelegate delegate) {
        mBadgeViewHeler.setDragDismissDelegage(delegate);
    }
}
