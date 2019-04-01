package com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.StringUtils;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 举报成功
 *
 * @author HJK
 */
public class NeighborhoodOKDialog extends BaseLibDialog implements View.OnClickListener {
    private TextView neighbor_ok_title;
    private TextView neighbor_ok_content;
    @NeighborhoodType
    private int mNeighborhoodType;

    /**
     * 对话框
     *
     * @param context 上下文
     */
    public NeighborhoodOKDialog(@NonNull Context context) {
        super(context);
        neighbor_ok_title = (TextView) findViewById(R.id.neighbor_ok_title);
        neighbor_ok_content = (TextView) findViewById(R.id.neighbor_ok_content);
        findViewById(R.id.report_ok).setOnClickListener(this);
    }

    /**
     * 设置状态
     *
     * @param mNeighborhoodType
     * @see NeighborhoodType
     */
    public void setNeighborhoodType(@NeighborhoodType int mNeighborhoodType) {
        this.mNeighborhoodType = mNeighborhoodType;
    }

    @Override
    public void show() {
        super.show();
        if (mNeighborhoodType == NeighborhoodType.ACTIVITY_TYPE) {
            neighbor_ok_title.setText(StringUtils.getString(R.string.sign_up_neibhbor_activity_ok));
            neighbor_ok_content.setText("");
        } else if (mNeighborhoodType == NeighborhoodType.REPRT_TYPE) {
            neighbor_ok_title.setText(StringUtils.getString(R.string.have_reported));
            neighbor_ok_content.setText(StringUtils.getString(R.string.have_reported_doing));
        }
    }

    @Override
    protected boolean getHideInput() {
        return false;
    }

    @Override
    protected float getWidth() {
        return JXContextWrapper.context.getResources().getDimension(R.dimen.dp_500);
    }

    @Override
    protected float getHeight() {
        return JXContextWrapper.context.getResources().getDimension(R.dimen.dp_300);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.neighbor_ok;
    }

    @Override
    protected boolean getCanceledOnTouchOutside() {
        return true;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @IntDef({NeighborhoodType.REPRT_TYPE, NeighborhoodType.ACTIVITY_TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NeighborhoodType {
        /**
         * 举报
         */
        int REPRT_TYPE = 0;
        /**
         * 活动
         */
        int ACTIVITY_TYPE = 1;
    }
}
