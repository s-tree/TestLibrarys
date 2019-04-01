package com.jingxi.smartlife.pad.sdk.neighbor.ui.views;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;

public class MyToast {


	private WindowManager mWdm;
	private ViewGroup mToastView;
	private WindowManager.LayoutParams mParams;
	private boolean mShowTime;
	private boolean mIsShow;
	
	public static final boolean LENGTH_LONG = true;
	public static final boolean LENGTH_SHORT = false;
	public static MyToast miuiToast;
	private TextView textView;
	private MyHandler myHandler = new MyHandler();
	
	private MyToast(Context context, String text, boolean showTime ){
		mShowTime = showTime;
		mIsShow = false;
		mWdm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		mToastView = (ViewGroup)Toast.makeText(context, text, Toast.LENGTH_SHORT).getView();
		textView = (TextView) mToastView.findViewById(android.R.id.message);
		if(textView == null){
			textView = new TextView(context);
			return;
		}
		textView.setTextSize(textView.getTextSize() + 0.5f);
		((View)textView.getParent()).setBackgroundResource(R.drawable.drawable_toast_bg);
		/**
		 * 由于用自定义的drawable作为bg时左侧有部分缺失，故外层添加一个LinearGroup，加上padding，使此部分能展示出来
		 */
		LinearLayout linearLayout = new LinearLayout(context);
		int padding = (int) JXContextWrapper.context.getResources().getDimension(R.dimen.dp_10);
		linearLayout.setPadding(padding,0,padding,0);
		linearLayout.addView(mToastView);
		mToastView = linearLayout;
		setParams();
	}

	private static class MyHandler extends Handler{
	}
	
	private void setParams() {
		mParams = new WindowManager.LayoutParams();
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;  
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;  
		mParams.format = PixelFormat.TRANSLUCENT;  
		mParams.windowAnimations = R.style.toast_anim_view;
		mParams.type = WindowManager.LayoutParams.TYPE_TOAST;  
		mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON  
	            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  
	            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		mParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
//		mParams.y =  DisplayUtil.dip2px(250);
	}
	
	public MyToast setText(String text){
		if(textView != null){
			textView.setText(text);
		}
		return this;
	}
		
	public void show(){
		if(!mIsShow){
			mIsShow = true;
			mWdm.addView(mToastView, mParams);
			myHandler.postDelayed(closeRunnable, (long)(mShowTime ? 3500 : 2000));
		}
		else{
			myHandler.removeCallbacks(closeRunnable);
			myHandler.postDelayed(closeRunnable, (long)(mShowTime ? 3500 : 2000));
		}
	}
	
	public void cancle(){
		mWdm.removeViewImmediate(mToastView);
		mIsShow = false;
	}
	
	private Runnable closeRunnable = new Runnable(){

		@Override
		public void run(){
			try {
				mWdm.removeViewImmediate(mToastView);
			} catch (Exception e) {
				e.printStackTrace();
			}
        	mIsShow = false;
		}
	};
	
	public static void showText(String text){
		try{
			if(miuiToast == null){
				miuiToast = new MyToast(JXContextWrapper.context, text, LENGTH_SHORT);
			}
			miuiToast.setText(text).show();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void cancel(){
		if(miuiToast == null){
			return;
		}
		miuiToast.cancle();
	}
}
