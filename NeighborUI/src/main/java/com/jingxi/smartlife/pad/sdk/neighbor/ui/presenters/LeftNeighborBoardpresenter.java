package com.jingxi.smartlife.pad.sdk.neighbor.ui.presenters;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborDetailDo;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborInfoBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.PersonalInfo;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.UpdateData;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs.LibTipDialog;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.ipresenters.ILeftNeighborBoardPresenter;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.iviews.ILeftNeighborView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.MyAction;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.ResponseListObserver;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.ResponseObserver;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.NeighborhoodUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.StringUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.ToastUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.widget.NeighborLeftView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.Bus;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;
import com.jingxi.smartlife.pad.util.PadHttpParams;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * Created by lb on 2017/11/13.
 */

public class LeftNeighborBoardpresenter implements ILeftNeighborBoardPresenter {
    private ILeftNeighborView leftView;
    private int pageIndex = 1;
    private String bz;
    private List<NeighborInfoBean> neighborInfoList;
    private NeighborInfoBean neighborInfoBean;
    private boolean isDeleting;
    private NeighborInfoBean deleteBean;
    private int detailtype = -2;
    private PersonalInfo headInfo;
    private String currentShowAccId;
    private boolean isLoading;
    public static final int IS_LOADING = 0;
    private boolean needShowFirstItem;
    private boolean onHead;

    public LeftNeighborBoardpresenter(NeighborLeftView leftView) {
        this.leftView = leftView;
        neighborInfoList = new ArrayList<>();
    }

    @Override
    public void setData(NeighborInfoBean neighborInfoBean, String bz, int showtype) {
        this.bz = bz;
        this.neighborInfoBean = neighborInfoBean;
        if (showtype != detailtype) {
            isLoading = false;
        }
        /**
         * 从我发布的切换到我的消息不需要刷新列表，只需要切换箭头
         */
        if (this.detailtype == 0 && showtype == 1) {
            this.detailtype = showtype;
            if (leftView != null) {
                leftView.showArrow(false);
            }
            return;
        }
        /**
         * 从我的消息切换到我发布的不需要刷新列表，只需要切换箭头
         */
        if (this.detailtype == 1 && showtype == 0) {
            this.detailtype = showtype;
            if (leftView != null) {
                leftView.showArrow(true);
                leftView.selectFirstVisibleItem();
            }
            return;
        }
        this.detailtype = showtype;
        if (detailtype == -1) {
            currentShowAccId = neighborInfoBean.accid;
            needShowFirstItem = false;
        } else {
            currentShowAccId = JXContextWrapper.accid;
            needShowFirstItem = true;
        }
        if (leftView != null) {
            leftView.showArrow(true);
        }
        getHeadData(neighborInfoBean);
        getLeftListData(neighborInfoBean);
    }

    @Override
    public void getHeadData(NeighborInfoBean neighborInfoBean) {
        if (onHead) {
            return;
        }
        onHead = true;
        String accId = "";
        if (detailtype != -1) {
            accId = JXContextWrapper.accid;
        } else {
            accId = neighborInfoBean.accid;
        }
        JXPadSdk.getNeighborManager().getNeighborBoardMemberInfo(accId)
                .subscribe(new ResponseObserver<PersonalInfo>() {
                    @Override
                    public void onResponse(PersonalInfo personalInfo) {
                        onHead = false;
                        if (leftView != null) {
                            personalInfo.setBz(bz);
                            headInfo = personalInfo;
                            leftView.setHeadData(personalInfo);
                        }
                    }

                    @Override
                    public void onFaild(String message) {
                        onHead = false;
                        ToastUtil.showToast(message);
                    }
                });
    }

    @Override
    public void getLeftListData(final NeighborInfoBean neighborInfoBean) {
        if (leftView != null && detailtype != -1) {
        }
        if (isLoading) {
            if (leftView != null) {
                leftView.onRefreshErro(String.valueOf(IS_LOADING));
            }
            return;
        }

        isLoading = true;
        if (leftView != null) {
            leftView.setFrefreshing();
        }
        pageIndex = 1;
        if (detailtype == -1 || detailtype == 0 || detailtype == 1) {
            String otherAccId = "";
            if (detailtype == -1) {
                otherAccId = neighborInfoBean.accid;
            } else {
                otherAccId = JXContextWrapper.accid;
            }
            JXPadSdk.getNeighborManager().getNeighborBoardList("",String.valueOf(pageIndex), otherAccId)
                    .subscribe(action);
        } else if (detailtype == 1) {
//            NeighborhoodUtil.getAboutMeNeighborList("notice", String.valueOf(pageIndex), action);
        } else if (detailtype == 2) {
            JXPadSdk.getNeighborManager().getAboutMeNeighborList(PadHttpParams.NeighborHood.Value.SCENE_FAVOUR,String.valueOf(pageIndex))
                    .subscribe(action);
        } else if (detailtype == 3) {
            JXPadSdk.getNeighborManager().getAboutMeNeighborList(PadHttpParams.NeighborHood.Value.SCENE_ACTIVITY,String.valueOf(pageIndex))
                    .subscribe(action);
        }
    }

    private void clearLeftListData() {
        neighborInfoList.clear();
        if (leftView != null) {
            leftView.updateList(neighborInfoList);
        }
    }

    /**
     * @param type 0 : 删除 ; 1 :点赞 ; 2 : 评论数 ; 3 : 新增 ;4 : 评论和点赞;5;
     * @param
     */
    @Override
    public void updateItem(final int type, final NeighborInfoBean neighborInfoBean) {
        Observable.just(neighborInfoBean.neighborBoardId)
                .observeOn(io.reactivex.schedulers.Schedulers.newThread())
                .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                .map(new Function<String, Integer>() {
                    @Override
                    public Integer apply(String s) {
                        for (int i = 0; i < neighborInfoList.size(); i++) {
                            if (TextUtils.equals(neighborInfoList.get(i).neighborBoardId, s)) {
                                if (type == 0) {
//                                    if (TextUtils.equals(currentShowAccId, neighborInfoBean.accid)) {
//                                        headcontent.put("neighborBoardCount", headcontent.getIntValue("neighborBoardCount") - 1);
//                                        headcontent.put("neighborBoardFavouredCount", headcontent.getIntValue("neighborBoardFavouredCount") - neighborInfoBean.favourCounts);
//                                    }
                                    neighborInfoList.remove(i);
                                } else if (type == 1) {
                                    if (TextUtils.equals(currentShowAccId, neighborInfoBean.accid)) {
                                        headInfo.setNeighborBoardFavouredCount(headInfo.getNeighborBoardFavouredCount() + (neighborInfoBean.isFavour ? 1 : -1));
                                    }
                                    neighborInfoList.get(i).isFavour = neighborInfoBean.isFavour;
                                    neighborInfoList.get(i).favourCounts = neighborInfoBean.favourCounts;
                                } else if (type == 2) {
                                    neighborInfoList.get(i).replyCounts = neighborInfoBean.replyCounts;
                                } else if (type == 4) {
                                    neighborInfoList.get(i).isFavour = neighborInfoBean.isFavour;
                                    neighborInfoList.get(i).favourCounts = neighborInfoBean.favourCounts;
                                    neighborInfoList.get(i).replyCounts = neighborInfoBean.replyCounts;
                                } else if (type == 5) {
                                    //                                    neighborInfoList.get(i).noticeIsRead = true;
                                }
                                return i;
                            }
                        }
                        return -1;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        if (integer != -1) {
                            if (leftView == null) {
                                return;
                            }
                            leftView.notifyItemChanged(integer, type);
                            if (type == 0) {
                                NeighborInfoBean infoBean;
                                if (integer == neighborInfoList.size()) {
                                    infoBean = neighborInfoList.size() > 0 ? neighborInfoList.get(neighborInfoList.size() - 1) : null;
                                } else {
                                    infoBean = neighborInfoList.size() > 0 ? neighborInfoList.get(integer) : null;
                                }
                                NeighborDetailDo neighborDetailDo = new NeighborDetailDo();
                                neighborDetailDo.type = detailtype == 1 ? NeighborDetailDo.TYPE_MY_MESSAGE : NeighborDetailDo.TYPE_DETAIL;
                                neighborDetailDo.neighborInfoBean = infoBean;
                                //                                //删除我的消息条目，需判断发送事件更改小红点
                                //                                if (TextUtils.equals(currentShowAccId, neighborInfoBean.accid) && !neighborInfoBean.noticeIsRead) {
                                //                                    neighborDetailDo.type = 4;
                                //                                }
                                if (leftView != null && detailtype != 1) {
                                    leftView.send2DetailFragment(neighborDetailDo);
                                    leftView.setItemArrow(infoBean);
                                }
                            }
                            if (TextUtils.equals(currentShowAccId, neighborInfoBean.accid)) {
                                if (type == 1) {
                                    leftView.setHeadData(headInfo);
                                } else if (type == 0 || type == 4) {
                                    getHeadData(LeftNeighborBoardpresenter.this.neighborInfoBean);
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void OnRefresh() {
        pageIndex = 1;
        getLeftListData(neighborInfoBean);
        getHeadData(neighborInfoBean);
    }

    @Override
    public void loadMore() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        pageIndex += 1;
        if (detailtype == -1 || detailtype == 0 || detailtype == 1) {
            String otherAccId = "";
            if (detailtype == -1) {
                otherAccId = neighborInfoBean.accid;
            } else {
                otherAccId = JXContextWrapper.accid;
            }
            JXPadSdk.getNeighborManager().getNeighborBoardList("",String.valueOf(pageIndex),otherAccId)
                    .subscribe(loadMoreAction);
        } else if (detailtype == 1) {
//            NeighborhoodUtil.getAboutMeNeighborList("notice", String.valueOf(pageIndex), loadMoreAction);
        } else if (detailtype == 2) {
            JXPadSdk.getNeighborManager().getAboutMeNeighborList(PadHttpParams.NeighborHood.Value.SCENE_FAVOUR, String.valueOf(pageIndex))
                    .subscribe(loadMoreAction);
        } else if (detailtype == 3) {
            JXPadSdk.getNeighborManager().getAboutMeNeighborList(PadHttpParams.NeighborHood.Value.SCENE_ACTIVITY, String.valueOf(pageIndex))
                    .subscribe(loadMoreAction);
        }
    }

    @Override
    public void deleteItem(NeighborInfoBean neighborInfoBean) {
        if (isDeleting) {
            return;
        }
        isDeleting = true;
        this.deleteBean = neighborInfoBean;
        leftView.showDeleteDialog(true);
    }

    @Override
    public LibTipDialog createDeleteDialog() {
        if (leftView == null) {
            return null;
        }
        LibTipDialog libTipDialog = new LibTipDialog(leftView.getTheContext(), deleteCheckAction);
        libTipDialog.setObject(deleteBean.neighborBoardId, StringUtils.getString(R.string.is_delete_this_neighbor));
        return libTipDialog;
    }

    @Override
    public void setLeftCommentCount(NeighborInfoBean neighborInfoBean) {
        updateItem(2, neighborInfoBean);
    }

    private MyAction<Object> deleteCheckAction = new MyAction<Object>() {
        @Override
        public void call(Object object) {
            JXPadSdk.getNeighborManager().deleteNeighborBoard(deleteBean.neighborBoardId)
                    .subscribe(deleteResultAction);
        }

        @Override
        public void faild(int errorNo) {
            isDeleting = false;
        }
    };

    private ResponseObserver<String> deleteResultAction = new ResponseObserver<String>() {
        @Override
        public void onResponse(String s) {
            ToastUtil.showToast(StringUtils.getString(R.string.neighbor_delete_ok));
            updateItem(0, deleteBean);
            UpdateData updateData = new UpdateData();
            updateData.setId(UpdateData.TYPE_NEIGHBOR_DEL);
            JSONObject object = JSONObject.parseObject(JSONObject.toJSONString(deleteBean));
            object.put("from", detailtype == -1 ? -1 : 0);
            updateData.setJsonObject(object);
            sendToNewNeighFragment(updateData);
        }

        @Override
        public void onFaild(String message) {
            isDeleting = false;
            ToastUtil.showToast(message);
        }
    };

    public void sendToNewNeighFragment(UpdateData data) {
        Bus.getDefault().post(data);
    }

    ResponseListObserver<NeighborInfoBean> action = new ResponseListObserver<NeighborInfoBean>() {
        @Override
        public void onResponse(List<NeighborInfoBean> infoBeans) {
            isLoading = false;
            neighborInfoList.addAll(infoBeans);
            leftView.setLeftListData(neighborInfoList, bz, detailtype);
        }

        @Override
        public void onFaild(String message) {
            isLoading = false;
            if (leftView != null) {
                leftView.onRefreshErro(message);
            }
        }
    };

    @Override
    public void onDestroy() {
        leftView = null;
    }

    @Override
    public int getDetailType() {
        return detailtype;
    }

    boolean hasMore = true;
    ResponseListObserver<NeighborInfoBean> loadMoreAction = new ResponseListObserver<NeighborInfoBean>() {
        @Override
        public void onResponse(List<NeighborInfoBean> infoBeans) {
            isLoading = false;
            hasMore = true;
            Observable.just(infoBeans)
                    .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                    .observeOn(io.reactivex.schedulers.Schedulers.newThread())
                    .map(new Function<List<NeighborInfoBean>, List<NeighborInfoBean>>() {
                        @Override
                        public List<NeighborInfoBean> apply(List<NeighborInfoBean> infoBeans) throws Exception {
                            if(infoBeans.size() >= NeighborhoodUtil.PAGESIZE ){
                                hasMore = false;
                            }
                            return NeighborhoodUtil.getDuplicatedList(neighborInfoList, infoBeans);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<NeighborInfoBean>>() {

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<NeighborInfoBean> neighborInfoBeen) {
                            leftView.loadMore(neighborInfoBeen, neighborInfoBeen.size() >= NeighborhoodUtil.PAGESIZE);
                        }
                    });
        }

        @Override
        public void onFaild(String message) {
            isLoading = false;
            if (leftView != null) {
                leftView.loadMoreFaild(message);
            }
        }
    };

    @Override
    public void onRead(String neighborBoardId) {
//        if (detailtype == 1) {
//            NeighborInfoBean infoBean = new NeighborInfoBean();
//            infoBean.neighborBoardId = neighborBoardId;
//            updateItem(5,infoBean);
//        }
    }

    @Override
    public boolean isNeedShowFirstItem() {
        return needShowFirstItem;
    }

    @Override
    public void setNeedShowFirstItem(boolean needShowFirstItem) {
        this.needShowFirstItem = needShowFirstItem;
    }

    @Override
    public void setLeftbean(NeighborInfoBean neighborInfoBean) {
        updateItem(4, neighborInfoBean);
    }

}
