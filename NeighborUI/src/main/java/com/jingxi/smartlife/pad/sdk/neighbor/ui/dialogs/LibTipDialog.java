package com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.MyAction;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;

/**
 * 提醒对话框
 *
 * @author HJK
 */

public class LibTipDialog extends BaseLibDialog implements View.OnClickListener {
    private MyAction<Object> action;
    private TextView tvHintMsg, tvAffirm, tvCancel;
    private Object object;
    public static final int DIALOG_CANCLE = -401;

    /**
     * 对话框
     *
     * @param context 上下文
     */
    public LibTipDialog(@NonNull Context context, MyAction<Object> action) {
        super(context);
        this.action = action;
        initView();
    }

    private void initView() {
        tvHintMsg = (TextView) findViewById(R.id.tv_hint_message);
        tvAffirm = (TextView) findViewById(R.id.tv_affirm);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvAffirm.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    /**
     * 一个界面有多个提示回调需要此方法
     *
     * @param action
     */
    public void setAction(MyAction<Object> action) {
        this.action = action;
    }

    public void setObject(Object object, String tip) {
        this.object = object;
        if (tvHintMsg != null) {
            tvHintMsg.setText(tip);
        }
    }

    public void setAffirm(String str) {
        tvAffirm.setText(str);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_affirm) {
            action.call(object);
            dismiss();
        } else if (v.getId() == R.id.tv_cancel) {
            action.faild(DIALOG_CANCLE);
            dismiss();
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
        return JXContextWrapper.context.getResources().getDimension(R.dimen.dp_180);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.contacts_affirm_dialog;
    }

    @Override
    protected boolean getCanceledOnTouchOutside() {
        return true;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        action.faild(DIALOG_CANCLE);
    }
}
