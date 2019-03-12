package com.jingxi.smartlife.pad.sdk.neighbor.ui.transformers;

import android.support.v4.view.ViewPager;
import android.view.View;

public class ZoomOutTransformer implements ViewPager.PageTransformer {
    private static float MIN_SCALE = 0.5f;

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        if (position < -1) { // [-Infinity,-1)
            // 这一页是屏幕左边。
            view.setAlpha(0);
        } else if (position <= 0) { // [-1,0]
            // 当使用默认的幻灯片过渡
            // 向左移动页面
            view.setAlpha(1);
            view.setTranslationX(0);
            view.setScaleX(1);
            view.setScaleY(1);
        } else if (position <= 1) { // (0,1]
            // 褪色的页面。
            view.setAlpha(1 - position);
            // 抵消默认幻灯片过渡
            view.setTranslationX(pageWidth * -position);
            // 规模的页面(MIN_SCALE和1之间)
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
                    * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        } else { // (1,+Infinity]
            // 这个页面是屏幕。
            view.setAlpha(0);

        }
    }

}
