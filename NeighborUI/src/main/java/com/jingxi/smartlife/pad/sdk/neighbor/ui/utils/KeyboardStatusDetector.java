package com.jingxi.smartlife.pad.sdk.neighbor.ui.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

/**
 * <p>Detects Keyboard Status changes and fires events only once for each change.</p>
 */
public class KeyboardStatusDetector implements OnGlobalLayoutListener {
    private static final int SOFT_KEY_BOARD_MIN_HEIGHT = 200;
    private KeyboardVisibilityListener mVisibilityListener;

    private boolean keyboardVisible = false;
    @Nullable
    private View v;

    public KeyboardStatusDetector registerFragment(Fragment f) {
        registerView(f.getView());
        return this;
    }

    public void unregist() {
        if (v != null) {
            v.getViewTreeObserver().addOnGlobalLayoutListener(null);
            v = null;
        }
        mVisibilityListener = null;
    }

    public void registerActivity(Activity a) {
        registerView(a.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    public KeyboardStatusDetector registerView(View v) {
        if (v != null) {
            this.v = v;
            v.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
        return this;
    }

    public KeyboardStatusDetector setmVisibilityListener(KeyboardVisibilityListener listener) {
        mVisibilityListener = listener;
        return this;
    }

    @Override
    public void onGlobalLayout() {
        Rect r = new Rect();
        if (v != null) {
            v.getWindowVisibleDisplayFrame(r);
            int heightDiff = v.getRootView().getHeight() - (r.bottom - r.top);
            if (heightDiff > SOFT_KEY_BOARD_MIN_HEIGHT) { // if more than 100 pixels, its probably a keyboard...
                if (!keyboardVisible) {
                    keyboardVisible = true;
                    if (mVisibilityListener != null) {
                        mVisibilityListener.onVisibilityChanged(true, heightDiff);
                    }
                }
            } else {
                if (keyboardVisible) {
                    keyboardVisible = false;
                    if (mVisibilityListener != null) {
                        mVisibilityListener.onVisibilityChanged(false, 0);
                    }
                }
            }
        }
    }

    public interface KeyboardVisibilityListener {
        void onVisibilityChanged(boolean keyboardVisible, int keyBoardHeight);
    }

}