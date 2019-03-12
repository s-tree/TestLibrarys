package com.jingxi.smartlife.pad.sdk.neighbor.ui.utils;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.UpdateData;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.ResponseTagObserver;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.Bus;
import com.jingxi.smartlife.pad.util.PadHttpParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.functions.Consumer;


/**
 * 发布新的邻里墙
 */
public class ReleaseUtil {
    private static int index = 0;
    private static String[] images;
    private static List<ArrayMap<String, Object>> imagePrepareUpload = new ArrayList<>();

    /**
     * 上传图片
     */
    public static synchronized void uploadPhoto(final String content, final String boardType,
                                                final ArrayList<ArrayMap<String, Object>> image, final String originalPrice,
                                                final String price, final String deadline, final String activityInfo) {
        index = 0;
        images = null;
        images = new String[image.size()];
        imagePrepareUpload.clear();
        imagePrepareUpload.addAll(image);
        ToastUtil.showToast(StringUtils.getString(R.string.reporting));
        ImageUploadUtil.getInstance().httpUploadTo(new Consumer<ArrayMap<String, String>>() {

            @Override
            public void accept(ArrayMap<String, String> stringStringArrayMap) throws Exception {
                for (int i = 0; i < imagePrepareUpload.size(); i++) {
                    if (TextUtils.isEmpty(stringStringArrayMap.get(ImageUploadUtil.IMAGE_NAME))) {
                        index++;
                        break;
                    }
                    if (TextUtils.equals((String) imagePrepareUpload.get(i).get("name"), stringStringArrayMap.get(ImageUploadUtil.IMAGE_NAME))) {
                        index++;
                        if (TextUtils.equals(stringStringArrayMap.get(PadHttpParams.PublicKey.RESULT), PadHttpParams.PublicValue.TRUE)) {
                            images[i] = stringStringArrayMap.get(ImageUploadUtil.FILE_URL);
                        } else {
                            ToastUtil.showToast(stringStringArrayMap.get(PadHttpParams.PublicKey.MSG));
                        }
                    }
                }
                if (imagePrepareUpload.size() == index) {
                    publish(content, boardType, Arrays.asList(images), originalPrice, price, deadline, activityInfo);
                }
            }
        }, imagePrepareUpload, AliyunUtils.NEIGHBOR);
    }

    /**
     * 获取楼列表
     *
     * @param content       发布内容
     * @param imageList     图片集合
     * @param boardType     社区新鲜事分类
     * @param originalPrice 原价
     * @param price         现价
     * @param deadline      活动报名截止时间(邻里闲置使)
     * @param activityInfo  活动详情(邻里闲置使)
     */
    public static synchronized void publish(String content, String boardType, List<String> imageList,
                                            String originalPrice, String price, String deadline, String activityInfo) {
        /**
         * 上传好之后才能清掉之前的图片路径和名称
         */
        JXPadSdk.getNeighborManager().uploadNewNeighbor(content,boardType,imageList,originalPrice,price,
                deadline,activityInfo)
                .subscribe(new ResponseTagObserver<String,String>(boardType) {
                    @Override
                    public void onResponse(String jsonObject) {
                        UpdateData data = new UpdateData();
                        JSONObject object = new JSONObject();
                        object.put(PadHttpParams.NeighborHood.Key.NEIGHBOR_TYPE_ID, tag);
                        data.setJsonObject(object);
                        data.setId(UpdateData.TYPE_ADD_NEIGHBOR);
                        Bus.getDefault().post(data);
                    }

                    @Override
                    public void onFaild(String message) {
                        ToastUtil.showToast(message);
                    }
                });
    }
}