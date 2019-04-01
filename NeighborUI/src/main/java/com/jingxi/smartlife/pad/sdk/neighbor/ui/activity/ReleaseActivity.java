package com.jingxi.smartlife.pad.sdk.neighbor.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingxi.smartlife.pad.sdk.neighbor.NeighborManager;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs.LoadingDialog;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs.SelectDateDialog;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.MyAction;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.AliyunUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.CameraUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.ImageUploadUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.ImageUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.InputMethodUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.LibAppUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.ReleaseUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.SelectPicUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.StringUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.ToastUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.CurrencyEasyTitleBar;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.FlowTagLayout;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.MyEditText;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static android.view.View.GONE;

/**
 * Created by kxrt_android_03 on 2017/11/7.
 */

public class ReleaseActivity extends AppCompatActivity implements View.OnClickListener, CurrencyEasyTitleBar.CurrencyEasyOnClickListener {
    private CurrencyEasyTitleBar ctb_release;
    private EditText /*et_releaseContent,*/ et_originalPrice, et_presentPrice;
    private TextView tv_inputSize, tv_deadlineTime, tv_release;
    private RelativeLayout rl_deadlineTime;
    private LinearLayout ll_sellPrice;
    private FlowTagLayout ftl_addPhoto;
    private ImageView iv_addPhoto;
    private MyEditText et_releaseContent;

    private List<RelativeLayout> imageViewList;
//    private SelectPicUtil selectPicUtil;
    private LoadingDialog loadingDialog;
    /**
     * 准备上传的图片名称及新加的名称
     */
    private ArrayList<ArrayMap<String, Object>> imagePrepareUpload, newImagePrepareUpload;
    private String releaseType, releaseName, type;
    private SelectDateDialog selectDateDialog;
    private String temPrice;
    private SelectPicUtil selectPicUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        releaseType = intent.getStringExtra("releaseType");
        releaseName = intent.getStringExtra("releaseName");
        type = intent.getStringExtra("type");
        setContentView(R.layout.activity_release);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ReleaseActivity.this, R.style.alertdialog);
        builder.setTitle(StringUtils.getString(R.string.prompt));
        builder.setMessage(StringUtils.getString(R.string.confirm_delete));
        builder.setNegativeButton(StringUtils.getString(R.string.cancel), null);
        newImagePrepareUpload = new ArrayList<>();
        imagePrepareUpload = new ArrayList<>();
        imageViewList = new ArrayList<>();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ctb_release = (CurrencyEasyTitleBar) findViewById(R.id.ctb_release);
        ctb_release.setCurrencyEasyOnClickListener(this);
        ctb_release.inflate();
        et_releaseContent = (MyEditText) findViewById(R.id.et_releaseContent);
        et_releaseContent.requestFocus();
        et_originalPrice = (EditText) findViewById(R.id.et_originalPrice);
        et_presentPrice = (EditText) findViewById(R.id.et_presentPrice);
        tv_inputSize = (TextView) findViewById(R.id.tv_inputSize);
        tv_deadlineTime = (TextView) findViewById(R.id.tv_deadlineTime);
        tv_release = (TextView) findViewById(R.id.tv_release);
        rl_deadlineTime = (RelativeLayout) findViewById(R.id.rl_deadlineTime);
        ll_sellPrice = (LinearLayout) findViewById(R.id.ll_sellPrice);
        ftl_addPhoto = (FlowTagLayout) findViewById(R.id.ftl_addPhoto);
        iv_addPhoto = (ImageView) findViewById(R.id.iv_addPhoto);
        et_originalPrice.addTextChangedListener(watcher);
        et_presentPrice.addTextChangedListener(watcher);
        iv_addPhoto.setOnClickListener(this);
        tv_release.setOnClickListener(this);
        rl_deadlineTime.setOnClickListener(this);
        et_releaseContent.addTextChangedListener(textWatcher);
        tv_inputSize.setText("0/500");
        InputMethodUtils.showSoftInput(et_releaseContent);
        /**
         *1:邻里闲聊2:邻里活动3:邻里互助4:邻里闲置
         */
        if (TextUtils.equals(type, NeighborManager.NEIGHBOR_TYPE_CHAT)) {
            rl_deadlineTime.setVisibility(GONE);
            ll_sellPrice.setVisibility(GONE);
            ctb_release.setCenterText(StringUtils.getString(R.string.upload_neighbor_chat));
            et_releaseContent.setHint(StringUtils.getString(R.string.share_new_message_to_neighbor));
        } else if (TextUtils.equals(type, NeighborManager.NEIGHBOR_TYPE_ACTIVITY)) {
            rl_deadlineTime.setVisibility(View.VISIBLE);
            ll_sellPrice.setVisibility(GONE);
            ctb_release.setCenterText(StringUtils.getString(R.string.upload_neighbor_activity));
            et_releaseContent.setHint(StringUtils.getString(R.string.neighbor_activity_infos));
        } else if (TextUtils.equals(type, NeighborManager.NEIGHBOR_TYPE_HELP)) {
            rl_deadlineTime.setVisibility(GONE);
            ll_sellPrice.setVisibility(View.GONE);
            ctb_release.setCenterText(StringUtils.getString(R.string.upload_neighbor_help));
            et_releaseContent.setHint(StringUtils.getString(R.string.ask_neighbor_something));
        } else if (TextUtils.equals(type, NeighborManager.NEIGHBOR_TYPE_SELL)) {
            rl_deadlineTime.setVisibility(GONE);
            ll_sellPrice.setVisibility(View.VISIBLE);
            ctb_release.setCenterText(StringUtils.getString(R.string.upload_neigbor_sell));
            et_releaseContent.setHint(StringUtils.getString(R.string.tell_neighbor_something_about_something));
        }
    }

    public void cancelLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /**
     * 登录临时调用的方法
     */
    public void showProgressDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
            loadingDialog.setCanceledOnTouchOutside(false);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_addPhoto) {
            if (imageViewList.size() < 9) {
                if (selectPicUtil == null) {
                    selectPicUtil = new SelectPicUtil(ReleaseActivity.this);
                }
                selectPicUtil.show(imageViewList.size());
            } else {
                ToastUtil.showToast(StringUtils.getString(R.string.selectd_pic_too_many));
            }
        } else if (v.getId() == R.id.tv_release) {
            if (LibAppUtils.isFastClick()) {
                cancelLoadingDialog();
                return;
            }
            if (TextUtils.isEmpty(et_releaseContent.getText().toString().replaceAll("\\s+", "")) && imageViewList.size() == 0) {
                cancelLoadingDialog();
                ToastUtil.showToast(StringUtils.getString(R.string.please_input_something_about_neighbor));
                return;
            }
            if (TextUtils.equals(type, NeighborManager.NEIGHBOR_TYPE_ACTIVITY)) {
                if (TextUtils.isEmpty(tv_deadlineTime.getText())) {
                    ToastUtil.showToast(StringUtils.getString(R.string.activity_deadline_can_not_be_empty));
                    return;
                }
            } else if (TextUtils.equals(type, NeighborManager.NEIGHBOR_TYPE_SELL)) {
                if (TextUtils.isEmpty(et_presentPrice.getText())) {
                    ToastUtil.showToast(StringUtils.getString(R.string.sell_price_can_not_be_empty));
                    return;
                }
            }
            if (imagePrepareUpload.size() > 0) {
                ReleaseUtil.uploadPhoto(et_releaseContent.getText().toString().trim(), releaseType, imagePrepareUpload, et_originalPrice.getText().toString(),
                        et_presentPrice.getText().toString(), tv_deadlineTime.getText().toString().replaceAll("/", "-"), et_releaseContent.getText().toString().trim());
            } else {
                ReleaseUtil.publish(et_releaseContent.getText().toString().trim(), releaseType, new ArrayList<String>(), et_originalPrice.getText().toString(),
                        et_presentPrice.getText().toString(), tv_deadlineTime.getText().toString().replaceAll("/", "-"), et_releaseContent.getText().toString().trim());
            }
            finish();
        } else if (v.getId() == R.id.iv_deletePhoto) {
            ftl_addPhoto.removeView((View) v.getParent());
            imageViewList.remove((RelativeLayout) v.getParent());
            for (ArrayMap img : imagePrepareUpload) {
                if (v.getTag().equals(img.get("name"))) {
                    /**
                     * 移除list中的图片名称及路径
                     */
                    imagePrepareUpload.remove(img);
                    iv_addPhoto.setVisibility(imagePrepareUpload.size() != 9 ? View.VISIBLE : View.GONE);
                    break;
                }
            }
            boolean b = !TextUtils.isEmpty(tv_inputSize.getText().toString().trim()) || hasPicture();
            tv_release.setEnabled(b);
        } else if (v.getId() == R.id.rl_deadlineTime) {
            showSelectDialog();
        }
    }

    private void showSelectDialog() {
        if (selectDateDialog == null) {
            selectDateDialog = new SelectDateDialog(this, new MyAction<String>() {
                @Override
                public void faild(int errorNo) {

                }

                @Override
                public void call(String time) {
                    if (TextUtils.isEmpty(time)) {
                        return;
                    }
                    tv_deadlineTime.setText(time);
                    selectDateDialog.dismiss();
                }
            });
        }
        if (selectDateDialog.isShowing()) {
            return;
        }
        selectDateDialog.show();
    }

    /**
     * 判断是否有图片
     *
     * @return
     */
    private boolean hasPicture() {
        return imagePrepareUpload != null && imagePrepareUpload.size() > 0;
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(s.toString().trim()) || hasPicture()) {
                tv_release.setEnabled(true);
            } else {
                tv_release.setEnabled(false);
            }
            tv_inputSize.setText(s.length() + "/500");
        }
    };

    TextWatcher watcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temPrice = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String temp = s.toString();
            int posDot = temp.indexOf(".");
            if (posDot > 0) {
                if (temp.length() - posDot - 1 > 2) {
                    s.delete(posDot + 3, posDot + 4);
                }
            }
            if (et_presentPrice.isFocused()) {
                if (et_presentPrice.length() == 1 && TextUtils.equals(et_presentPrice.getText(), ".")) {
                    et_presentPrice.setText("0.");
                    et_presentPrice.setSelection(LibAppUtils.twoAfterPoint(et_presentPrice.getText().toString()).length());
                    return;
                }
                if (!TextUtils.isEmpty(et_presentPrice.getText().toString()) && Double.parseDouble(et_presentPrice.getText().toString()) - 9999999.99 > 0.01) {
                    et_presentPrice.setText(temPrice);
                    et_presentPrice.setSelection(LibAppUtils.twoAfterPoint(et_presentPrice.getText().toString()).length());
                    return;
                }
                if (!TextUtils.equals(et_presentPrice.getText().toString(), LibAppUtils.twoAfterPoint(et_presentPrice.getText().toString()))) {
                    et_presentPrice.setText(LibAppUtils.twoAfterPoint(et_presentPrice.getText().toString()));
                    et_presentPrice.setSelection(LibAppUtils.twoAfterPoint(et_presentPrice.getText().toString()).length());
                }
            } else if (et_originalPrice.isFocused()) {
                if (et_originalPrice.length() == 1 && TextUtils.equals(et_originalPrice.getText(), ".")) {
                    et_originalPrice.setText("0.");
                    et_originalPrice.setSelection(LibAppUtils.twoAfterPoint(et_originalPrice.getText().toString()).length());
                    return;
                }
                if (!TextUtils.isEmpty(et_originalPrice.getText().toString()) && Double.parseDouble(et_originalPrice.getText().toString()) - 9999999.99 > 0.01) {
                    et_originalPrice.setText(temPrice);
                    et_originalPrice.setSelection(LibAppUtils.twoAfterPoint(et_originalPrice.getText().toString()).length());
                    return;
                }
                if (!TextUtils.equals(et_originalPrice.getText().toString(), LibAppUtils.twoAfterPoint(et_originalPrice.getText().toString()))) {
                    et_originalPrice.setText(LibAppUtils.twoAfterPoint(et_originalPrice.getText().toString()));
                    et_originalPrice.setSelection(LibAppUtils.twoAfterPoint(et_originalPrice.getText().toString()).length());
                }
            }
            return;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        newImagePrepareUpload.clear();

        if (resultCode == RESULT_OK) {
            if (requestCode == CameraUtil.PHOTO_REQUEST_CODE) {
                ImageUtil.getImageFileFromUri(this.getContentResolver(),intent.getData())
                        .subscribe(new Observer<File>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(File file) {
                                ArrayMap<String, Object> image = new ArrayMap<>();
                                image.put(ImageUploadUtil.KeyValue.name, file.getName());
                                if ((Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))) {
                                    image.put(ImageUploadUtil.KeyValue.Uri, Uri.fromFile(file));
                                }
                                newImagePrepareUpload.add(image);
                                updateUI(newImagePrepareUpload);
                            }

                            @Override
                            public void onError(Throwable e) {
                                if(e instanceof FileNotFoundException){
                                    ToastUtil.showToast("文件不存在");
                                    return;
                                }
                                e.printStackTrace();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            } else if (requestCode == CameraUtil.CAMERA_REQUEST_CODE) {
                //相机
                String imageFile = CameraUtil.getImageFile();
                if (TextUtils.isEmpty(imageFile)) {
                    return;
                }
                File imgInfo = CameraUtil.getPhoto(imageFile);
                ArrayMap<String, Object> image;
                image = new ArrayMap<>();
                image.put(ImageUploadUtil.KeyValue.name, imgInfo.getName());
                image.put(ImageUploadUtil.KeyValue.Uri, Uri.fromFile(imgInfo));
                newImagePrepareUpload.add(image);
                Uri localUri = Uri.fromFile(imgInfo);
                Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
                sendBroadcast(localIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Intent mediaScanIntent = new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(imgInfo);
                    mediaScanIntent.setData(contentUri);
                    sendBroadcast(mediaScanIntent);
                } else {
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                            Uri.parse(imageFile)));
                }
            }
            updateUI(newImagePrepareUpload);
        }
    }

    private void updateUI(ArrayList<ArrayMap<String, Object>> newImagePrepareUpload) {
        imagePrepareUpload.addAll(newImagePrepareUpload);
        for (int i = 0; i < newImagePrepareUpload.size(); i++) {
            /**
             * 添加图片
             */
            RelativeLayout imageViews = (RelativeLayout) ReleaseActivity.this.getLayoutInflater().inflate(R.layout.item_add_photo, (FlowTagLayout) findViewById(R.id.ftl_addPhoto), false);
            ImageView imageView = (ImageView) imageViews.findViewById(R.id.iv_photo);
            ImageView iv_deletePhoto = (ImageView) imageViews.findViewById(R.id.iv_deletePhoto);
            Picasso.with(JXContextWrapper.context).load((Uri) newImagePrepareUpload.get(i).get("Uri"))
                    .placeholder(R.mipmap.ic_placeholderimg)
                    .error(R.mipmap.ic_placeholderimg)
                    .resize(AliyunUtils.SIZE_THUMB_Neighbor, AliyunUtils.SIZE_THUMB_Neighbor)
                    .centerCrop()
                    .into(imageView);
            iv_deletePhoto.setOnClickListener(this);
            /**
             * 添加图片名称给控件tag
             */
            iv_deletePhoto.setTag(newImagePrepareUpload.get(i).get("name"));
            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams((int) JXContextWrapper.context.getResources().getDimension(R.dimen.dp_132),
                    (int) JXContextWrapper.context.getResources().getDimension(R.dimen.dp_127));
            layoutParams.bottomMargin = (int) JXContextWrapper.context.getResources().getDimension(R.dimen.dp_10);
            layoutParams.rightMargin = (int) JXContextWrapper.context.getResources().getDimension(R.dimen.dp_13);
            layoutParams.leftMargin = (int) JXContextWrapper.context.getResources().getDimension(R.dimen.dp_13);
            layoutParams.topMargin = (int) JXContextWrapper.context.getResources().getDimension(R.dimen.dp_8);
            imageViewList.add(imageViews);
            if (imageViewList != null && imageViewList.size() > 0) {
                ftl_addPhoto.addView(imageViews, imageViewList.size() - 1, layoutParams);
            } else {
                ftl_addPhoto.addView(imageViews, 0, layoutParams);
            }
        }
        if (null != imagePrepareUpload && imagePrepareUpload.size() == 9) {
            iv_addPhoto.setVisibility(GONE);
        }
        tv_release.setEnabled(hasPicture());
    }

    @Override
    public void back() {
        finish();
    }

    @Override
    public void leftView(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(selectDateDialog != null){
            selectDateDialog.setAction(null);
        }
        if(selectPicUtil != null){
            selectPicUtil.dismiss();
        }
    }
}
