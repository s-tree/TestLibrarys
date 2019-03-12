package com.jingxi.smartlife.pad.sdk.neighbor.ui.views;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by A04 on 2017/3/16.
 */

public class NeighborSwipeRefreshLayout extends SwipeRefreshLayout {
    public NeighborSwipeRefreshLayout(Context context) {
        super(context);
    }

    public NeighborSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private float upReY;
    private float preY;
    private float preX;
    private boolean isUp;

    public boolean isUp() {
        return isUp;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean res = super.onInterceptTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            preX = ev.getX();
            preY = ev.getY();
            upReY = ev.getY();
        } else {
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                isUp = ev.getY() < upReY;
                upReY = ev.getY();
            }
            if (Math.abs(ev.getX() - preX) > Math.abs(ev.getY() - preY)) {
                return false;
            } else {
                preX = ev.getX();
                preY = ev.getY();
            }
        }
        return res;
    }

    public interface OnRefreshCallBack extends OnRefreshListener {
        String REFRESH_TAG = NeighborSwipeRefreshLayout.class.getName();
    }

}
