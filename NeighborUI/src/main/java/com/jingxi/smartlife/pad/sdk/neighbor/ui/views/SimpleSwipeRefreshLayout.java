package com.jingxi.smartlife.pad.sdk.neighbor.ui.views;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by A04 on 2017/3/16.
 */

public class SimpleSwipeRefreshLayout extends SwipeRefreshLayout {
    public SimpleSwipeRefreshLayout(Context context) {
        super(context);
    }

    public SimpleSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isUp() {
        return isUp;
    }

    private boolean isUp;
    private float preY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            isUp = false;
            preY = ev.getY();
        }
        else if(ev.getAction() == MotionEvent.ACTION_MOVE){
            isUp = ev.getY() < preY;
        }else if (ev.getAction() == MotionEvent.ACTION_UP) {
            isUp = ev.getY() < preY;
            preY = ev.getY();
        }
        return super.onInterceptTouchEvent(ev);
    }

    public interface OnRefreshCallBack extends OnRefreshListener {
        String REFRESH_TAG = SimpleSwipeRefreshLayout.class.getName();
    }
}
