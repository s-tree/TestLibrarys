package com.jingxi.smartlife.pad.sdk.neighbor.ui.utils;

import android.content.Context;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;
import com.jingxi.smartlife.pad.util.ConfigUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 阿里云 文件上传
 */
public class AliyunUtils {
    @IntDef({VISIT, HEAD, NEIGHBOR, RED_PACKET})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UploadType {
    }

    public static final String ALI_PIC_SUFFIX = "?x-oss-process=image/resize,m_fill,h_height,w_width";
    private static final String HEIGHT_NAME = "height";
    private static final String WIDTH_NAME = "width";

    public static final int SIZE_THUMB = 200;
    public static final int SIZE_THUMB_Neighbor = 200;

    public static final int VISIT = 1;
    public static final int HEAD = 2;
    public static final int NEIGHBOR = 3;
    public static final int RED_PACKET = 4;

    private static AliyunUtils instance;
    public String OSS_ENDPOINT;
    public String OSS_PATH;
    public String OSS_FILEPATH;
    private String BUCKET_NAME;
    private OSSClient ossClient;

    public String getOssEndpoint() {
        return OSS_ENDPOINT;
    }

    public String getOssPath() {
        return OSS_PATH;
    }

    public String getOssFilepath(@UploadType int type) {
        if(TextUtils.isEmpty(JXContextWrapper.familyInfoId)){
            throw new RuntimeException("JXPadSDK.setFamilyInfoId is not called");
        }
        if (type == VISIT) {
            /**
             * 访客记录
             */
            return TextUtils.concat("visit/",
                    JXContextWrapper.familyInfoId,
                    "/").toString();
        } else if (type == HEAD) {
            /**
             * 头像
             */
            return "headimage/";
        } else if (type == NEIGHBOR) {
            /**
             * 社区新鲜事
             */
            return "neighbor/";
        } else if (type == RED_PACKET) {
            /**
             * 红包
             * */
            return "red_packet/";
        }
        return "images/";
    }

    public String getBucketName() {
        return BUCKET_NAME;
    }

    public static AliyunUtils getInstance(Context context) {
        if (instance == null) {
            instance = new AliyunUtils(context);
        }
        return instance;
    }

    private AliyunUtils(Context context) {
        if (ossClient == null) {
            ossClient = getOSSClient(context);
        }
    }

    /**
     * 初始化
     */
    private OSSClient getOSSClient(Context context) {
        OSSCredentialProvider credentialProvider = null;
        OSS_ENDPOINT = ConfigUtil.OSS_ENDPOINT;
        OSS_PATH = ConfigUtil.OSS_PATH;
        OSS_FILEPATH = "visit/";
        BUCKET_NAME = ConfigUtil.OSS_BUCKET_NAME;
        credentialProvider = new OSSPlainTextAKSKCredentialProvider(ConfigUtil.ALI_ACCESSKEYID, ConfigUtil.ALI_ACCESSKEYSECRET);
        return new OSSClient(context, OSS_ENDPOINT, credentialProvider, initClientConfiguration());
    }

    public OSSClient getOssClient() {
        return ossClient;
    }

    private ClientConfiguration initClientConfiguration() {
        ClientConfiguration conf = ClientConfiguration.getDefaultConf();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(9); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        return conf;
    }

    /**
     * 获取新的图片路径
     */
    public static String getImgUrl(String highImage, View view) {
        if (highImage == null || view == null) {
            return "www.zdl123.com";
        }
        int height = view.getMeasuredHeight();
        if (height == 0) {
            view.measure(0, 0);
            height = view.getMeasuredHeight();
        }
        int width = view.getMeasuredHeight();
        if (height <= 0 || width <= 0 || TextUtils.isEmpty(highImage) || (!highImage.contains(AliyunUtils.getInstance(JXContextWrapper.context).getOssPath()))) {
            return highImage;
        }
        if (height == width) {
            if (height <= 300) {
                height = width = 300;
            } else if (height <= 600) {
                height = width = 600;
            } else if (height <= 900) {
                height = width = 900;
            }
        }
        return highImage + view.getContext().getString(R.string.alibaba_image_server).replace("width", width + "").replace("height", height + "");
    }

    public static String getAliPic(String url, int width, int height) {
        return TextUtils.concat(url,
                ALI_PIC_SUFFIX
                        .replaceAll(HEIGHT_NAME, String.valueOf(height))
                        .replaceAll(WIDTH_NAME, String.valueOf(width)))
                .toString();
    }
}
