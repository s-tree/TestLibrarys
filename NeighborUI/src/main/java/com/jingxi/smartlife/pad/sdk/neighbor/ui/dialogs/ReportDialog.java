package com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.MyAction;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.ResponseObserver;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.LibAppUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.StringUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.ToastUtil;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;

/**
 * 举报
 *
 * @author HJK
 */
public class ReportDialog extends BaseLibDialog implements View.OnClickListener {
    private MyAction<Boolean> action;
    private TextView lastView,
            garbage/*垃圾信息*/, tort/*侵权*/, sham/*虚假信息*/, cheat/*欺诈骗钱*/, pornographic/*传播色情、暴力、反动信息*/,
            reportBack/*返回*/, reportNext/*举报*/;

    private String neighborBoardId;

    /**
     * 对话框
     *
     * @param context 上下文
     * @param action  回调
     */
    public ReportDialog(@NonNull Context context, MyAction<Boolean> action) {
        super(context);
        this.action = action;
        initView();
    }

    private void initView() {
        garbage = (TextView) findViewById(R.id.garbage);
        garbage.setOnClickListener(this);
        tort = (TextView) findViewById(R.id.tort);
        tort.setOnClickListener(this);
        sham = (TextView) findViewById(R.id.sham);
        sham.setOnClickListener(this);
        cheat = (TextView) findViewById(R.id.cheat);
        cheat.setOnClickListener(this);
        pornographic = (TextView) findViewById(R.id.pornographic);
        pornographic.setOnClickListener(this);
        reportBack = (TextView) findViewById(R.id.report_back);
        reportBack.setOnClickListener(this);
        reportNext = (TextView) findViewById(R.id.report_next);
        reportNext.setOnClickListener(this);
    }

    /**
     * 设置需要举报的社区新鲜事Id
     *
     * @param neighborBoardId
     */
    public void setNeighborBoardId(String neighborBoardId) {
        this.neighborBoardId = neighborBoardId;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.garbage) {
            onTextClick(v);
        } else if (id == R.id.tort) {
            onTextClick(v);
        } else if (id == R.id.sham) {
            onTextClick(v);
        } else if (id == R.id.cheat) {
            onTextClick(v);
        } else if (id == R.id.pornographic) {
            onTextClick(v);
        } else if (id == R.id.report_back) {
            dismiss();
            action.call(false);
        } else if (id == R.id.report_next) {
            if (lastView == null) {
                ToastUtil.showToast(StringUtils.getString(R.string.input_report_detail));
                return;
            }
            JXPadSdk.getNeighborManager().reportNeighborhood(neighborBoardId, lastView.getText().toString())
                    .subscribe(new ResponseObserver<String>() {
                        @Override
                        public void onResponse(String s) {
                            action.call(true);
                        }

                        @Override
                        public void onFaild(String message) {
                            ToastUtil.showToast(message);
                        }
                    });
        }
    }

    private void onTextClick(View view) {
        if (view == lastView) {
            return;
        }
        updataView((TextView) view, true);
        if (lastView != null) {
            updataView(lastView, false);
        }
        lastView = (TextView) view;
    }

    /**
     * 恢复未点击状态
     *
     * @param textView
     * @param selected
     */
    private void updataView(TextView textView, boolean selected) {
        if (textView != null) {
            Drawable drawable = ContextCompat.getDrawable(JXContextWrapper.context, selected ? R.mipmap.neighbor_report_ok : R.mipmap.neighbor_report_no);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(drawable, null, null, null);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        updataView(lastView, false);
        lastView = null;
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
        if (TextUtils.equals(LibAppUtils.getCurrentPhoneLanguage(getContext()), "zh")) {
            return JXContextWrapper.context.getResources().getDimension(R.dimen.dp_350);
        } else if (TextUtils.equals(LibAppUtils.getCurrentPhoneLanguage(getContext()), "en")) {
            return JXContextWrapper.context.getResources().getDimension(R.dimen.dp_400);
        }
        return JXContextWrapper.context.getResources().getDimension(R.dimen.dp_350);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.neighbor_report;
    }

    @Override
    protected boolean getCanceledOnTouchOutside() {
        return true;
    }

}
