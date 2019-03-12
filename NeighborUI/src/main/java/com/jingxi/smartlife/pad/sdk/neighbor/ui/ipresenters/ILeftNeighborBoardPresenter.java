package com.jingxi.smartlife.pad.sdk.neighbor.ui.ipresenters;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborInfoBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs.LibTipDialog;

/**
 * Created by lb on 2017/11/13.
 */

public interface ILeftNeighborBoardPresenter {
    void setData(NeighborInfoBean neighborInfoBean, String bz, int type);

    void getHeadData(NeighborInfoBean neighborInfoBean);

    void getLeftListData(NeighborInfoBean neighborInfoBean);

    void updateItem(int type, NeighborInfoBean mData);

    void OnRefresh();

    void loadMore();

    void deleteItem(NeighborInfoBean neighborInfoBean);

    LibTipDialog createDeleteDialog();

    void setLeftCommentCount(NeighborInfoBean neighborInfoBean);

    void onDestroy();

    int getDetailType();

    void onRead(String neighborBoardId);

    boolean isNeedShowFirstItem();

    void setNeedShowFirstItem(boolean needShowFirstItem);

    void setLeftbean(NeighborInfoBean neighborInfoBean);
}
