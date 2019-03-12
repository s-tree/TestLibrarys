package com.jingxi.smartlife.pad.sdk.neighbor.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;

/**
 * 简单的
 *
 * @author HJK
 */

public class CurrencyEasyTitleBar extends CurrencyBaseTitleBar implements CurrencyBaseTitleBar.CurrencyInitView {
    private String centerText;
    private int leftSrc;
    private ImageView leftView;
    private TextView centerView;
    private CurrencyEasyOnClickListener currencyEasyOnClickListener;
    private boolean leftVisibilty;
    private int centerTextColor;

    public CurrencyEasyTitleBar(@NonNull Context context) {
        this(context, null);
    }

    public CurrencyEasyTitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CurrencyEasyTitleBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArrayCurrencyTitleBarView = getContext().obtainStyledAttributes(attrs, R.styleable.CurrencyTitleBarView);
        leftVisibilty = typedArrayCurrencyTitleBarView.getBoolean(R.styleable.CurrencyTitleBarView_leftVisibility, false);
        centerText = typedArrayCurrencyTitleBarView.getString(R.styleable.CurrencyTitleBarView_centerText);
        TypedArray typedArrayCurrencyEasyTitleBar = getContext().obtainStyledAttributes(attrs, R.styleable.CurrencyEasyTitleBar);
        leftSrc = typedArrayCurrencyEasyTitleBar.getResourceId(R.styleable.CurrencyEasyTitleBar_leftSrc, R.mipmap.library_search);
        centerTextColor = typedArrayCurrencyTitleBarView.getColor(R.styleable.CurrencyTitleBarView_centerTextColor, -1);
    }

    @Override
    public void inflate() {
        leftViewId = R.layout.library_currency_left_image;
        centerViewId = R.layout.library_currency_center;
        setCurrencyInitView(this);
        super.inflate();
    }

    @Override
    public void currencyinitView(View leftView, View centerView) {
        this.leftView = (ImageView) leftView.findViewById(R.id.leftView);
        this.leftView.setImageResource(leftSrc);
        if (leftVisibilty) {
            this.leftView.setVisibility(View.VISIBLE);
            this.leftView.setOnClickListener(this);
        } else {
            this.leftView.setVisibility(View.GONE);
        }
        this.centerView = (TextView) centerView.findViewById(R.id.centerView);
        this.centerView.post(new Runnable() {
            @Override
            public void run() {
                int leftWidth = CurrencyEasyTitleBar.this.leftView.getWidth();
                int closeWidth = currencyClose.getWidth();
                RelativeLayout.LayoutParams centerViewLP = (RelativeLayout.LayoutParams) CurrencyEasyTitleBar.this.centerView.getLayoutParams();
                int margin = closeWidth > leftWidth ? closeWidth : leftWidth;
                centerViewLP.leftMargin = margin;
                centerViewLP.rightMargin = margin;
                CurrencyEasyTitleBar.this.centerView.setLayoutParams(centerViewLP);
            }
        });
        this.centerView.setText(centerText);
        if (centerTextColor != -1) {
            this.centerView.setTextColor(centerTextColor);
        }
    }

    public void setCurrencyEasyOnClickListener(CurrencyEasyOnClickListener currencyEasyOnClickListener) {
        setCurrencyOnClickListener(currencyEasyOnClickListener);
        this.currencyEasyOnClickListener = currencyEasyOnClickListener;
    }

    /**
     * 设置左侧图片显示
     *
     * @param leftVisibilty
     */
    public void setLeftVisibilty(boolean leftVisibilty) {
        this.leftVisibilty = leftVisibilty;
        if (leftView != null) {
            leftView.setVisibility(leftVisibilty ? View.VISIBLE : View.GONE);
        }
    }

    public void setCenterText(String centerText) {
        this.centerText = centerText;
        if (centerView != null) {
            centerView.setText(centerText);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.leftView) {
            if (currencyEasyOnClickListener != null) {
                currencyEasyOnClickListener.leftView(v);
            }
        }
    }

    public interface CurrencyEasyOnClickListener extends CurrencyBaseOnClickListener {
        void leftView(View view);
    }
}
