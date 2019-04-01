package com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.MyAction;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.ResponseObserver;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.InputMethodUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.LibAppUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.NoEmojiInput;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.StringUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.ToastUtil;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;

/**
 * 邻里活动对话框
 *
 * @author HJK
 */
public class NeighborhoodActivityDialog extends BaseLibDialog implements View.OnClickListener, View.OnFocusChangeListener {
    private EditText neighborName, neighborPhone;
    private TextView neighborAdress;
    private TextView neighborBack;
    private TextView neighborNext;
    private MyAction<Boolean> action;
    private ImageView cleanName, cleanPhone;

    private String neighborBoardId;

    /**
     * 对话框
     *
     * @param context 上下文
     */
    public NeighborhoodActivityDialog(@NonNull Context context, @NonNull MyAction<Boolean> action) {
        super(context);
        this.action = action;
        initView();
    }

    public void setNeighborBoardId(String neighborBoardId) {
        this.neighborBoardId = neighborBoardId;
    }

    private void initView() {
        cleanName = (ImageView) findViewById(R.id.cleanName);
        cleanName.setOnClickListener(this);
        cleanPhone = (ImageView) findViewById(R.id.cleanPhone);
        cleanPhone.setOnClickListener(this);
        neighborName = (EditText) findViewById(R.id.neighborName);
        neighborName.setOnFocusChangeListener(this);
        /**
         * 过滤字符并最大长度为8
         */
        neighborName.setFilters(new InputFilter[]{new NoEmojiInput(), new InputFilter.LengthFilter(10)});
        neighborPhone = (EditText) findViewById(R.id.neighborPhone);
        neighborPhone.setOnFocusChangeListener(this);
        /**
         * todo
         */
//        neighborPhone.setText(significanceData.ownerMobile);
        neighborAdress = (TextView) findViewById(R.id.neighborAdress);
//        neighborAdress.setText(TextUtils.concat(significanceData.houseNO));
        neighborBack = (TextView) findViewById(R.id.neighborBack);
        neighborBack.setOnClickListener(this);
        neighborNext = (TextView) findViewById(R.id.neighborNext);
        neighborNext.setOnClickListener(this);
    }

    @Override
    public void show() {
        if (neighborPhone != null) {
//            neighborPhone.setText(significanceData.ownerMobile);
        }
        super.show();
        if (neighborName != null) {
            neighborName.setText("");
            neighborName.requestFocus();
            InputMethodUtils.showSoftInput(neighborName);
        }
    }

    @Override
    protected boolean getHideInput() {
        return true;
    }

    @Override
    protected float getWidth() {
        return JXContextWrapper.context.getResources().getDimension(R.dimen.dp_500);
    }

    @Override
    protected float getHeight() {
        return JXContextWrapper.context.getResources().getDimension(R.dimen.dp_433_3);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.neighbor_dialog_activity;
    }

    @Override
    protected boolean getCanceledOnTouchOutside() {
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.neighborBack) {
            action.call(false);
            dismiss();
        } else if (id == R.id.neighborNext) {
            String name = neighborName.getText().toString();
            String phone = neighborPhone.getText().toString();
            if (TextUtils.isEmpty(name)) {
                ToastUtil.showToast(StringUtils.getString(R.string.please_input_name));
                return;
            }
            if (TextUtils.isEmpty(phone)) {
                ToastUtil.showToast(StringUtils.getString(R.string.please_input_mobile));
                return;
            }
            if (!LibAppUtils.isMobileNo(phone)) {
                ToastUtil.showToast(StringUtils.getString(R.string.please_input_trueth_mobile));
                return;
            }
            JXPadSdk.getNeighborManager().joinActivity(neighborBoardId, name, phone, neighborAdress.getText().toString())
                    .subscribe(new ResponseObserver<String>() {

                        @Override
                        public void onResponse(String string) {
                            action.call(true);
                        }

                        @Override
                        public void onFaild(String message) {
                            ToastUtil.showToast(message);
                        }
                    });
        } else if (id == R.id.cleanName) {
            neighborName.setText("");
        } else if (id == R.id.cleanPhone) {
            neighborPhone.setText("");
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        if (hasFocus) {
            if (id == R.id.neighborName) {
                cleanName.setVisibility(View.VISIBLE);
                cleanPhone.setVisibility(View.GONE);
            } else if (id == R.id.neighborPhone) {
                cleanPhone.setVisibility(View.VISIBLE);
                cleanName.setVisibility(View.GONE);
            } else {
                cleanPhone.setVisibility(View.GONE);
                cleanPhone.setVisibility(View.GONE);
            }
        } else {
            cleanPhone.setVisibility(View.GONE);
            cleanPhone.setVisibility(View.GONE);
        }
    }
}
