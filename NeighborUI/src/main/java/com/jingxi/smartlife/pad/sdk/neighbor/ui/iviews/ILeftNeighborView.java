package com.jingxi.smartlife.pad.sdk.neighbor.ui.iviews;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborDetailDo;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborInfoBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.PersonalInfo;

import java.util.List;

/**
 * Created by lb on 2017/11/13.
 */

public interface ILeftNeighborView {
    void setHeadData(PersonalInfo personalInfo);

    void setLeftListData(List<NeighborInfoBean> infoBeanList, String bz, int detailtype);

    void updateList(List<NeighborInfoBean> infoBeanList);

    void notifyItemChanged(int index, int type);

    void loadMore(List<NeighborInfoBean> content, boolean hasMore);

    void onRefreshErro(String errorNo);

    void showDeleteDialog(boolean isShow);

    Context getTheContext();

    void showLoadingDialog(boolean isShow);

    void send2DetailFragment(NeighborDetailDo neighborDetailDo);

    void setFrefreshing();

    void loadMoreFaild(String erro);

    void setItemArrow(NeighborInfoBean infoBean);

    void showArrow(boolean show);

    void selectFirstVisibleItem();

    void startListen(Runnable runnable, boolean add);
}
