package com.jingxi.smartlife.pad.sdk.neighbor.ui.iviews;

import android.content.Context;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborBoardTypeBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborInfoBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/5/10.
 */

public interface INeighborhoodListView {

    void setTypes(ArrayList<NeighborBoardTypeBean> beans);

    void showDeleteDialog(boolean isShow);

    boolean showLoadingDialog(boolean isShow);

    void willGoDetail(NeighborInfoBean jsonObject, int detailType);

    HashMap<String,View> getPagerViews();

    Context getTheContext();

    void setHeadImg(String familyHeadImg);
}
