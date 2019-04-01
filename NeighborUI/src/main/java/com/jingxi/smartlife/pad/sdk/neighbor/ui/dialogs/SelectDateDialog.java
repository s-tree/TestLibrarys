package com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.MyAction;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.LibAppUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.StringUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.ToastUtil;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;

/**
 * Created by kxrt_android_03 on 2017/11/10.
 */

public class SelectDateDialog extends BaseLibDialog implements View.OnClickListener {
    private TextView tv_close, tv_comfire;
    private DatePicker dp_YMD;
    private TimePicker tp_HD;
    private MyAction<String> action;

    /**
     * 对话框
     *
     * @param context 上下文
     */
    public SelectDateDialog(@NonNull Context context, MyAction<String> action) {
        super(context);
        this.action = action;
        initDialog();
    }

    public MyAction<String> getAction(){
        return action;
    }

    public void setAction(MyAction<String> action){
        this.action = action;
    }

    private void initDialog() {
        tv_close = (TextView) findViewById(R.id.tv_close);
        tv_comfire = (TextView) findViewById(R.id.tv_comfire);
        dp_YMD = (DatePicker) findViewById(R.id.dp_YMD);
        tp_HD = (TimePicker) findViewById(R.id.tp_HD);
        tp_HD.setIs24HourView(true);
        tv_close.setOnClickListener(this);
        tv_comfire.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_comfire) {
            String time = TextUtils.concat(String.valueOf(dp_YMD.getYear()),"/",
                    zeroFill((dp_YMD.getMonth() + 1)),"/",
                    zeroFill(dp_YMD.getDayOfMonth()) ," ",
                    zeroFill(tp_HD.getHour()),":",zeroFill(tp_HD.getMinute())).toString();
            if ((LibAppUtils.getTimetoLong(time) - System.currentTimeMillis()) < 0) {
                ToastUtil.showToast(StringUtils.getString(R.string.select_date_can_not_early));
                return;
            }

            action.call(time);
        }else if (v.getId() == R.id.tv_close) {
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
        return JXContextWrapper.context.getResources().getDimension(R.dimen.dp_440);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.dialog_select_date;
    }

    @Override
    protected boolean getCanceledOnTouchOutside() {
        return false;
    }

    private String zeroFill(int i) {
        return String.valueOf(i < 10 ? "0" + i : i);
    }
}
