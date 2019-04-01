package com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;


public class LoadingDialog extends Dialog {


    public LoadingDialog(Context context) {

        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading);
        setCanceledOnTouchOutside(false);
        if(getWindow() != null){
            getWindow().setBackgroundDrawable(
                    new ColorDrawable(JXContextWrapper.context.getResources().getColor(android.R.color.transparent)));
        }
    }


}
