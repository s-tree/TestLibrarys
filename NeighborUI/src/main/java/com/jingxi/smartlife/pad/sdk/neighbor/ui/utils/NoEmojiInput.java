package com.jingxi.smartlife.pad.sdk.neighbor.ui.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

/**
 * Created by Administrator on 2016/11/18.
 */
public class NoEmojiInput implements InputFilter {
    private static final String reg = "[^a-zA-Z0-9\u4E00-\u9FA5_]";

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (!TextUtils.isEmpty(source)) {
            String input = source.toString().replaceAll(reg, "");
            if (!TextUtils.equals(input, source)) {
                ToastUtil.showToast("仅支持数字、字母、中文");
            }
            return input;
        }
        return null;
    }
}
