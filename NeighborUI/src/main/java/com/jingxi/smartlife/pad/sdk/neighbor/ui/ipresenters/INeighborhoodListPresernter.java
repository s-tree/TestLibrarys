
package com.jingxi.smartlife.pad.sdk.neighbor.ui.ipresenters;
import com.alibaba.fastjson.JSONObject;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs.LibTipDialog;

/**
 * Created by Administrator on 2017/5/10.
 */

public interface INeighborhoodListPresernter {

    void goDetail(JSONObject jsonObject);

    void favourite(JSONObject jsonObject);

    void delete(JSONObject jsonObject);

    void getNeighborType();

    void destroy();

    LibTipDialog createDeleteDialog();

    void published(JSONObject jsonObject);

    void deleted(JSONObject jsonObject);

    void favourited(JSONObject jsonObject);

    void commented(JSONObject jsonObject);

    void updateData(JSONObject jsonObject, int type);

    void getHeadImg();
}
