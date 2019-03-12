package com.jingxi.smartlife.pad.sdk.neighbor.ui.ipresenters;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.CommentBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.EnrollPeopleBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborInfoBean;

import java.util.List;

/**
 * Created by lb on 2017/11/13.
 */

public interface IRightNeighborBoardPresenter {
    void setData(NeighborInfoBean neighborInfoBean, String bz);

    void getComment(String neighborBoardId, int pageIndex);

    void setComment(List<CommentBean> commentBeanList, String totalSize);

    void OnleftItemSelect(NeighborInfoBean neighborInfoBean);

    void loadMoreComment(List<CommentBean> commentBeanList);

    void updateFavour(NeighborInfoBean mData);

    void getEnrollPeople(String neighborBoardId);

    void setEnrollPeople(List<EnrollPeopleBean> enrollPeopleBeanList);

    void getNeighborBoardInfo(String neighborBoardId);
}
