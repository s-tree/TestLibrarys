package com.jingxi.smartlife.pad.sdk.neighbor.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;

/**
 * 通用标题栏
 */
public class CurrencyBaseTitleBar extends FrameLayout implements View.OnClickListener {

    protected int leftViewId, centerViewId;
    private View leftView, centerView;
    protected TextView currencyClose;
    private String backText;
    private int backPicture;
    private LayoutInflater inflater;
    protected CurrencyBaseOnClickListener currencyBaseOnClickListener;
    private CurrencyInitView currencyInitView;
    private int background;
    private int backTextColor;

    public CurrencyBaseTitleBar(@NonNull Context context) {
        this(context, null);
    }

    public CurrencyBaseTitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CurrencyBaseTitleBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CurrencyBaseTitleBar);
        leftViewId = typedArray.getResourceId(R.styleable.CurrencyBaseTitleBar_leftView, -1);
        centerViewId = typedArray.getResourceId(R.styleable.CurrencyBaseTitleBar_centerView, -1);
        backPicture = typedArray.getResourceId(R.styleable.CurrencyBaseTitleBar_backPicture, -1);
        backText = typedArray.getString(R.styleable.CurrencyBaseTitleBar_backText);
        background = typedArray.getInteger(R.styleable.CurrencyBaseTitleBar_currencyBackground, Color.TRANSPARENT);
        backTextColor = typedArray.getColor(R.styleable.CurrencyBaseTitleBar_backTextColor, -1);
    }

    private void initView() {
        Context context = getContext();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.library_base_title_bar, this);
        view.findViewById(R.id.currency_bar).setBackgroundColor(background);
        currencyClose = (TextView) view.findViewById(R.id.currency_close);
        if (leftViewId != -1) {
            ViewStub leftViewStub = (ViewStub) view.findViewById(R.id.leftView);
            leftViewStub.setLayoutResource(leftViewId);
            leftView = leftViewStub.inflate();
        }
        if (centerViewId != -1) {
            ViewStub centerViewStub = (ViewStub) view.findViewById(R.id.centerView);
            centerViewStub.setLayoutResource(centerViewId);
            centerView = centerViewStub.inflate();
        }
        if (currencyInitView != null) {
            currencyInitView.currencyinitView(leftView, centerView);
        }
        if (!TextUtils.isEmpty(backText)) {
            currencyClose.setText(backText);
        }
        /**
         * 设置返回图片
         */
        setBackPicture(backPicture);
        if (backTextColor != -1) {
            currencyClose.setTextColor(backTextColor);
        }
        currencyClose.setOnClickListener(this);
    }

    /**
     * 必须要调用这个方法
     */
    public void inflate() {
        if (currencyInitView == null) {
            return;
        }
        initView();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.currency_close) {
            if (currencyBaseOnClickListener != null) {
                currencyBaseOnClickListener.back();
            }
        }
    }

    /**
     * 设置返回图片样式
     *
     * @param backPicture
     */
    public void setBackPicture(@DrawableRes int backPicture) {
        this.backPicture = backPicture;
        if (backPicture != -1 && currencyClose != null) {
            Drawable drawable = ContextCompat.getDrawable(JXContextWrapper.context, backPicture);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            currencyClose.setCompoundDrawables(drawable, null, null, null);
        }
    }

    /**
     * 设置返回文字显示
     *
     * @param backText
     */
    public void setBackText(String backText) {
        this.backText = backText;
        if (!TextUtils.isEmpty(backText)) {
            currencyClose.setText(backText);
        }
    }

    /**
     * 界面初始化监听
     *
     * @param currencyInitView
     */
    public void setCurrencyInitView(CurrencyInitView currencyInitView) {
        this.currencyInitView = currencyInitView;
    }

    /**
     * 添加事件监听
     *
     * @param currencyBaseOnClickListener
     */
    public void setCurrencyOnClickListener(CurrencyBaseOnClickListener currencyBaseOnClickListener) {
        this.currencyBaseOnClickListener = currencyBaseOnClickListener;
    }

    public interface CurrencyInitView {
        void currencyinitView(View leftView, View centerView);
    }
}
