package com.jingxi.smartlife.pad.sdk.neighbor.ui.iviews;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.CommentBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.EnrollPeopleBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborInfoBean;

import java.util.List;

/**
 * Created by lb on 2017/11/13.
 */

public interface IRightNeighborView {
    void setData(NeighborInfoBean neighborInfoBean);

    void setComment(List<CommentBean> commentBeanList, String totalSize);

    void loadMoreComment(List<CommentBean> commentBeanList);

    void setNewData(NeighborInfoBean neighborInfoBean);

    void updateFavour(NeighborInfoBean mData);

    void setEnrollPeople(List<EnrollPeopleBean> enrollPeopleBeanList);

    void stopRefresh();

    void onDestroy();

    void setTopInfo(NeighborInfoBean neighborInfoBean, boolean first);

    void startRefresh();

    void sendBean(NeighborInfoBean neighborInfoBean);

    void onKeyBoardVisibilityChanged(boolean keyboardVisible);
}
