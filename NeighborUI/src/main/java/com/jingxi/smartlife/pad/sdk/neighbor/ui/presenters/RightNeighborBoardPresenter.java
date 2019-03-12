package com.jingxi.smartlife.pad.sdk.neighbor.ui.presenters;

import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.CommentBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.EnrollPeopleBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborInfoBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.ipresenters.IRightNeighborBoardPresenter;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.iviews.IRightNeighborView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.ResponseListObserver;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.ResponseObserver;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.ToastUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.widget.NeighborRightView;
import com.jingxi.smartlife.pad.sdk.network.BaseEntry;

import java.util.List;

/**
 * Created by lb on 2017/11/13.
 */

public class RightNeighborBoardPresenter implements IRightNeighborBoardPresenter {
    private IRightNeighborView iRightNeighborView;
    private NeighborInfoBean neighborInfoBean;
    public RightNeighborBoardPresenter(NeighborRightView iRightNeighborView) {
        this.iRightNeighborView = iRightNeighborView;
    }

    @Override
    public void setData(NeighborInfoBean neighborInfoBean, String bz) {
        this.neighborInfoBean = neighborInfoBean;
        if(iRightNeighborView != null){
            iRightNeighborView.setData(neighborInfoBean);
        }
    }

    @Override
    public void setComment(List<CommentBean> commentBeanList, String totalSize) {
        if(iRightNeighborView != null){
            iRightNeighborView.setComment(commentBeanList,totalSize);
        }
    }

    @Override
    public void OnleftItemSelect(NeighborInfoBean neighborInfoBean) {
        if(iRightNeighborView != null){
            if (neighborInfoBean != null) {
                if (iRightNeighborView != null) {
                    iRightNeighborView.startRefresh();
                }
                getNewData(neighborInfoBean.neighborBoardId);
            } else {
                if (iRightNeighborView != null) {
                    iRightNeighborView.setNewData(null);
                }
            }

        }
    }

    private void getNewData(String neighborBoardId) {
        JXPadSdk.getNeighborManager().getNeighborBoardInfo(neighborBoardId)
                .subscribe(new ResponseObserver<NeighborInfoBean>() {
                    @Override
                    public void onResponse(NeighborInfoBean neighborInfoBean) {
                        stopViewRefresh();
                        if (iRightNeighborView != null) {
                            iRightNeighborView.setNewData(neighborInfoBean);
                        }
                    }

                    @Override
                    public void onFaild(String message) {
                        stopViewRefresh();
                        ToastUtil.showToast(message);
                    }
                });
    }

    @Override
    public void loadMoreComment(List<CommentBean> commentBeanList) {
        if(iRightNeighborView != null){
            iRightNeighborView.loadMoreComment(commentBeanList);
        }
    }

    @Override
    public void updateFavour(NeighborInfoBean mData) {
        if(iRightNeighborView != null){
            iRightNeighborView.updateFavour(mData);
        }
    }

    @Override
    public void setEnrollPeople(List<EnrollPeopleBean> enrollPeopleBeanList) {
        if(iRightNeighborView != null){
            iRightNeighborView.setEnrollPeople(enrollPeopleBeanList);
        }
    }

    @Override
    public void getNeighborBoardInfo(String neighborBoardId) {
        JXPadSdk.getNeighborManager().getNeighborBoardInfo(neighborBoardId)
                .subscribe(new ResponseObserver<NeighborInfoBean>() {
                    @Override
                    public void onResponse(NeighborInfoBean neighborInfoBean) {
                        stopViewRefresh();
                        if (iRightNeighborView != null) {
                            iRightNeighborView.sendBean(neighborInfoBean);
                            iRightNeighborView.setTopInfo(neighborInfoBean, false);
                        }
                    }

                    @Override
                    public void onFaild(String message) {
                        stopViewRefresh();
                        ToastUtil.showToast(message);

                    }
                });
    }

    @Override
    public void getComment(String neighborBoardId, final int pageIndex) {
        JXPadSdk.getNeighborManager().queryNeighborBoardReply(neighborBoardId,pageIndex)
                .subscribe(new ResponseListObserver<CommentBean>() {
                    @Override
                    public void onResponse(List<CommentBean> commentBeans, BaseEntry baseEntry) {
                        stopViewRefresh();
                        if (pageIndex == 1) {
                            setComment(commentBeans, baseEntry.totalSize);
                        } else {
                            loadMoreComment(commentBeans);
                        }
                    }

                    @Override
                    public void onFaild(String message) {
                        stopViewRefresh();
                        ToastUtil.showToast(message);
                    }
                });
    }

    @Override
    public void getEnrollPeople(String neighborBoardId) {
        JXPadSdk.getNeighborManager().getActivityMember(neighborBoardId)
                .subscribe(new ResponseListObserver<EnrollPeopleBean>() {
                    @Override
                    public void onResponse(List<EnrollPeopleBean> enrollPeopleBeen) {
                        stopViewRefresh();
                        setEnrollPeople(enrollPeopleBeen);
                    }

                    @Override
                    public void onFaild(String message) {
                        stopViewRefresh();
                        ToastUtil.showToast(message);
                    }
                });
    }

    private void stopViewRefresh(){
        if(iRightNeighborView != null){
            iRightNeighborView.stopRefresh();
        }
    }

}
