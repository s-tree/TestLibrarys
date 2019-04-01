package com.jingxi.smartlife.pad.sdk.neighbor.ui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.StringDef;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;
import com.jingxi.smartlife.pad.util.PadHttpParams;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class ImageUploadUtil {
    @StringDef({
            KeyValue.imgName,
            KeyValue.fileUrl,
            KeyValue.jpg,
            KeyValue.name,
            KeyValue.Uri,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface KeyValue {
        String imgName = "imgName";
        String fileUrl = "fileUrl";
        String jpg = ".jpg";
        String name = "name";
        String Uri = "Uri";
    }

    private static ImageUploadUtil instance;
    public static ImageUploadUtil getInstance(){
        if(instance == null){
            synchronized (ImageUploadUtil.class){
                if(instance == null){
                    instance = new ImageUploadUtil();
                }
            }
        }
        return instance;
    }

    public static final String IMAGE_NAME = "imgName";
    public static final String FILE_URL = "fileUrl";

    public void httpUploadTo(Consumer<ArrayMap<String, String>> ac, byte[] bytes, String filename, @AliyunUtils.UploadType int type) {
        if (type == AliyunUtils.VISIT) {
            return;
        }
        /**
         * 保存的图片路径
         */
        AliyunUtils aliyunUtils = AliyunUtils.getInstance(JXContextWrapper.context);
        String imgUrl = TextUtils.concat(aliyunUtils.getOssFilepath(type),
                LibAppUtils.getTimeDataToString(System.currentTimeMillis(), "yyyy-MM-dd"),"/",filename).toString();
        PutObjectRequest put = new PutObjectRequest(aliyunUtils.getBucketName(), imgUrl, bytes);
        try {
            AliyunUtils.getInstance(JXContextWrapper.context).getOssClient().putObject(put);
            ArrayMap<String, String> uploadto = new ArrayMap<>();
            uploadto.put(PadHttpParams.PublicKey.RESULT, PadHttpParams.PublicValue.TRUE);
            uploadto.put(IMAGE_NAME, filename);
            uploadto.put(FILE_URL, aliyunUtils.getOssPath() + imgUrl);
            Observable.just(uploadto).observeOn(AndroidSchedulers.mainThread()).subscribe(ac);
        } catch (Exception e) {
            ArrayMap<String, String> uploadto = new ArrayMap<>();
            uploadto.put(PadHttpParams.PublicKey.RESULT, PadHttpParams.PublicValue.FALSE);
            uploadto.put(IMAGE_NAME, filename);
            uploadto.put(PadHttpParams.PublicKey.MSG,StringUtils.getString(R.string.uploadFaild));
            Observable.just(uploadto).observeOn(AndroidSchedulers.mainThread()).subscribe(ac);
        }
    }

    /**
     * 批量上传
     */
    public void httpUploadTo(Consumer<ArrayMap<String, String>> ac,
                             List<ArrayMap<String, Object>> imagePrepareUpload,
                             @AliyunUtils.UploadType int type) {
        HttpUploadThread thread = new HttpUploadThread(ac, imagePrepareUpload, type);
        thread.start();
    }

    private class HttpUploadThread extends Thread {
        private Consumer<ArrayMap<String, String>> ac;
        private List<ArrayMap<String, Object>> imagePrepareUpload;
        private int type;

        private HttpUploadThread(Consumer<ArrayMap<String, String>> ac,
                                List<ArrayMap<String, Object>> imagePrepareUpload,
                                @AliyunUtils.UploadType int type) {
            this.ac = ac;
            this.imagePrepareUpload = imagePrepareUpload;
            this.type = type;
        }

        @Override
        public void run() {
            for (int i = 0; i < imagePrepareUpload.size(); i++) {
                Uri uri = (Uri) imagePrepareUpload.get(i).get("Uri");
                String name = (String) imagePrepareUpload.get(i).get("name");
                if (uri == null) {
                    ArrayMap<String, String> uploadto = new ArrayMap<>();
                    uploadto.put(PadHttpParams.PublicKey.RESULT, PadHttpParams.PublicValue.FALSE);
                    uploadto.put(IMAGE_NAME, name);
                    uploadto.put(PadHttpParams.PublicKey.MSG,StringUtils.getString(R.string.uploadFaild));
                    Observable.just(uploadto).observeOn(AndroidSchedulers.mainThread()).subscribe(ac);
                    continue;
                }
                Bitmap temp = BitmapFactory.decodeFile(uri.getPath());
                if (temp == null) {
                    temp = getBitmapFromUri(uri);
                    if (temp == null) {
                        /**
                         * 如果没有获取到图片就通知UI
                         */
                        ArrayMap<String, String> uploadto = new ArrayMap<>();
                        uploadto.put(PadHttpParams.PublicKey.RESULT, PadHttpParams.PublicValue.FALSE);
                        uploadto.put(IMAGE_NAME, name);
                        uploadto.put(PadHttpParams.PublicKey.MSG,StringUtils.getString(R.string.uploadFaild));
                        Observable.just(uploadto).observeOn(AndroidSchedulers.mainThread()).subscribe(ac);
                        continue;
                    }
                }
                byte[] bytes = LibAppUtils.compressBitmap(temp, 800);
                if (imagePrepareUpload.size() > i) {
                    httpUploadTo(ac, bytes, (String) imagePrepareUpload.get(i).get("name"), type);
                }
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            /**
             *   读取uri所在的图片
             */
            return MediaStore.Images.Media.getBitmap(JXContextWrapper.context.getContentResolver(), uri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}