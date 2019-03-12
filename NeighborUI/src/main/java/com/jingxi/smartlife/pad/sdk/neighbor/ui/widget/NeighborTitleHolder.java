package com.jingxi.smartlife.pad.sdk.neighbor.ui.widget;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;


/**
 * Created by lb on 2017/11/8.
 */

public class NeighborTitleHolder implements View.OnClickListener {

    private TabLayout tabLayout;
    private TextView tvTitle;
    private TextView tvBack;
    private OnTitleClickListener onTitleClickListener;

    public static NeighborTitleHolder init(View root) {
        if (root == null) {
            throw new RuntimeException("RootView can not be null");
        }
        return new NeighborTitleHolder(root);
    }

    public NeighborTitleHolder(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.titlebar_tab);
        tvTitle = (TextView) view.findViewById(R.id.titlebar_title);
        tvBack = (TextView) view.findViewById(R.id.titlebar_back);
        tvBack.setOnClickListener(this);
    }

    public TabLayout getTablayout() {
        return tabLayout;
    }

    public void setOnTitleClickListener(OnTitleClickListener onTitleClickListener) {
        this.onTitleClickListener = onTitleClickListener;
    }

    public void setTitleText(String title) {
        tvTitle.setText(title);
        tabLayout.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
    }

    public void showTab() {
        tabLayout.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.GONE);
    }

    public void setTabLayout(final ViewPager viewPager) {
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tagPosition = tab.getPosition();
                int nowPosition = viewPager.getCurrentItem();
                if (Math.abs(tagPosition - nowPosition) <= 1){
                    viewPager.setCurrentItem(tagPosition);
                }
                else {
                    // TODO: 2017/9/7 发现关闭动画效果  会发生界面不显示   数据加载完成bug
                    viewPager.setCurrentItem(tagPosition, true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.titlebar_back) {
            onTitleClickListener.onTitleBack();
        }
    }

    public interface OnTitleClickListener {
        void onTitleBack();
    }

}
