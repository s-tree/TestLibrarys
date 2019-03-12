package com.jingxi.smartlife.pad.sdk.neighbor.ui.views;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;


/**
 * 拍照控件
 * 
 * @author wuql
 *
 */
public class SelectPicPopupWindow extends PopupWindow implements
		View.OnClickListener {

	private Button btn_take_photo, btn_pick_photo, btn_cancel;
	private View mMenuView;
	private Callback mCallback;

	public SelectPicPopupWindow(Context context, Callback callback) {
		super(context);
		mCallback = callback;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.pic_alert_dialog, null);
		btn_take_photo = (Button) mMenuView.findViewById(R.id.btn_take_photo);
		btn_pick_photo = (Button) mMenuView.findViewById(R.id.btn_pick_photo);
		btn_cancel = (Button) mMenuView.findViewById(R.id.btn_cancel);

		btn_cancel.setOnClickListener(this);
		btn_pick_photo.setOnClickListener(this);
		btn_take_photo.setOnClickListener(this);

		this.setContentView(mMenuView);
		/**
		 * 设置SelectPicPopupWindow弹出窗体的宽
 		 */
		this.setWidth(LayoutParams.MATCH_PARENT);

		/**
		 * 设置SelectPicPopupWindow弹出窗体的高
 		 */
		this.setHeight(LayoutParams.MATCH_PARENT);

		/**
		 * 设置SelectPicPopupWindow弹出窗体可点击
 		 */
		this.setFocusable(true);

		/**
		 * 设置SelectPicPopupWindow弹出窗体动画效果
		 */
		//this.setAnimationStyle(R.style.AnimBottom);

		/**
		 *
		 * 实例化一个ColorDrawable颜色为半透明
		 */
		ColorDrawable dw = new ColorDrawable(0xb0000000);

		/**
		 * 设置SelectPicPopupWindow弹出窗体的背景
 		 */
		this.setBackgroundDrawable(dw);
		/**
		 * mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		 */
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

	}

	@Override
	public void onClick(View v) {
		dismiss();
		if (mCallback != null) {
			if (v == btn_pick_photo) {
				mCallback.onPickPhoto();
			} else if (v == btn_take_photo) {
				mCallback.onTakePhoto();
			}
		}
	}

	public void destroy(){
		mCallback = null;
	}

	public interface Callback {

		void onTakePhoto();

		void onPickPhoto();
	}

}
