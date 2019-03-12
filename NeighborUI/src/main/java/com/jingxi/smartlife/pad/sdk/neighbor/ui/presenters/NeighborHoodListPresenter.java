package com.jingxi.smartlife.pad.sdk.neighbor.ui.presenters;

import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborBoardTypeBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborInfoBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs.LibTipDialog;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.ipresenters.INeighborhoodListPresernter;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.iviews.INeighborhoodListView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.MyAction;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.ResponseListObserver;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.ResponseObserver;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.ResponseTagObserver;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.StringUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.ToastUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.widget.NeighborhoodView;
import com.jingxi.smartlife.pad.sdk.network.BaseEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/5/10.
 */

public class NeighborHoodListPresenter implements INeighborhoodListPresernter {
    private INeighborhoodListView iView;
    private String neighborhoodId;

    /**
     * 删除
     */
    private final int type_delete = 0;
    /**
     * 点赞变更
     */
    private final int type_favourite = 1;
    /**
     * 评论变更
     */
    private final int type_comment = 2;
    /**
     * 新增
     */
    private final int type_insert = 3;
    /**
     * 评论和点赞
     */
    private final int type_favourite_and_comment = 4;

    public NeighborHoodListPresenter(INeighborhoodListView iView) {
        this.iView = iView;
    }

    @Override
    public void destroy() {
        iView = null;
    }

    @Override
    public void deleted(JSONObject jsonObject) {
        updateItem(jsonObject, type_delete);
    }

    @Override
    public void favourited(JSONObject jsonObject) {
        updateItem(jsonObject, type_favourite);
    }

    @Override
    public void commented(JSONObject jsonObject) {
        updateItem(jsonObject, type_comment);
    }

    /**
     * @param data
     * @param type 1 : 更新联系人
     *             2 : 更新数据
     *             3 : 删除数据
     *             4 : 更新头像
     */
    @Override
    public void updateData(JSONObject data, int type) {
        if (type == 1) {
//            updatePersons(data);
        } else if (type == 2) {
            updateItem(data, type_favourite_and_comment);
        } else if (type == 3) {
            updateItem(data, type_delete);
        } else if (type == 4) {
//            updateHeadImage(data.getString("accid"), data.getString("headImage"));
        }
    }

    // --------------------------------点赞 start ------------------------------------------------------
    private boolean onFavour = false;

    @Override
    public void favourite(JSONObject jsonObject) {
        onFavour(jsonObject);
    }

    public void onFavour(JSONObject jsonObject) {
        if (onFavour) {
            return;
        }
        onFavour = true;
        JXPadSdk.getNeighborManager().updateFavour(
                jsonObject.getString("neighborBoardId"),
                !jsonObject.getBoolean("isFavour"))
                .subscribe(new ResponseTagObserver<String,JSONObject>(jsonObject) {

                    @Override
                    public void onResponse(String s) {
                        onFavour = false;
                        if (tag != null) {
                            boolean oldIsFavour = tag.getBoolean("isFavour");
                            tag.put("favourCounts", tag.getInteger("favourCounts") + (oldIsFavour ? -1 : 1));
                            tag.put("isFavour", !oldIsFavour);
                            updateItem(tag, type_favourite);
                        }
                    }

                    @Override
                    public void onFaild(String message) {
                        onFavour = false;
                        ToastUtil.showToast(message);
                    }
                });
    }

    // --------------------------------点赞 end ------------------------------------------------------
// --------------------------------发布成功 ------------------------------------------------------
    @Override
    public void published(JSONObject jsonObject) {
        updateItem(jsonObject, type_insert);
    }

    //------------------------------发布成功 end -------------------------------------------------
    //---------------------------------------- 删除社区新鲜事 ------------------------------------------
    private boolean onDelete = false;
    private JSONObject deleteJb;

    @Override
    public void delete(JSONObject jsonObject) {
        if (onDelete) {
            return;
        }
        onDelete = true;
        this.deleteJb = jsonObject;
        this.neighborhoodId = deleteJb.getString("neighborBoardId");
        iView.showDeleteDialog(true);
    }

    @Override
    public LibTipDialog createDeleteDialog() {
        if (iView == null) {
            return null;
        }
        LibTipDialog  libTipDialog = new LibTipDialog(iView.getTheContext(), deleteCheckAction);
        libTipDialog.setObject(neighborhoodId, StringUtils.getString(R.string.is_delete_this_neighbor));
        return libTipDialog;
    }

    private MyAction<Object> deleteCheckAction = new MyAction<Object>() {

        @Override
        public void call(Object o) {
            JXPadSdk.getNeighborManager().deleteNeighborBoard(neighborhoodId)
                    .subscribe(deleteResultAction);
        }

        @Override
        public void faild(int errorNo) {
            onDelete = false;
        }
    };

    private ResponseObserver<String> deleteResultAction = new ResponseObserver<String>() {
        @Override
        public void onResponse(String s) {
            onDelete = false;
            ToastUtil.showToast(StringUtils.getString(R.string.neighbor_delete_ok));
            updateItem(deleteJb, type_delete);
        }

        @Override
        public void onFaild(String message) {
            onDelete = false;
            ToastUtil.showToast(message);
        }
    };

    //------------------------------------ 删除社区新鲜事 end ------------------------------------------

    @Override
    public void getNeighborType() {
        JXPadSdk.getNeighborManager().queryNeighborBoardTypeList()
                .subscribe(new ResponseListObserver<NeighborBoardTypeBean>() {
                    @Override
                    public void onResponse(List<NeighborBoardTypeBean> neighborBoardTypeBeans) {
                        NeighborBoardTypeBean typeBean = new NeighborBoardTypeBean();
                        typeBean.name = StringUtils.getString(R.string.default_neighbor_type_name);
                        typeBean.neighborBoardTypeId = "all";
                        neighborBoardTypeBeans.add(0,typeBean);
                        if (iView != null) {
                            iView.setTypes((ArrayList<NeighborBoardTypeBean>) neighborBoardTypeBeans);
                        }
                    }

                    @Override
                    public void onFaild(String message) {
                        ToastUtil.showToast(message);
                    }
                });
    }

    //--------------------------- 点击社区新鲜事详情准备跳转 start-------------------------------------
    private boolean onVerify = false;

    @Override
    public void goDetail(JSONObject jsonObject) {
        clickNeighborhood(jsonObject);
    }

    public void clickNeighborhood(JSONObject mData) {
        if (onVerify) {
            return;
        }
        iView.showLoadingDialog(true);
        onVerify = true;
        JXPadSdk.getNeighborManager().getNeighborBoardInfo(mData.getString("neighborBoardId"))
                .subscribe(new ResponseTagObserver<NeighborInfoBean,JSONObject>(mData){

                    @Override
                    public void onResponse(NeighborInfoBean data, BaseEntry entry) {
                        onVerify = false;
                        iView.showLoadingDialog(false);
                        iView.willGoDetail(data,-1);
                    }

                    @Override
                    public void onFaild(String message) {
                        onVerify = false;
                        iView.showLoadingDialog(false);
                        ToastUtil.showToast(message);
                    }

                    @Override
                    public void onFaild(int code, String message) {
                        onVerify = false;
                        iView.showLoadingDialog(false);
                        /**
                         * 已删除
                         */
                        if(code == 9007){
                            updateItem(tag, type_delete);
                        }
                        ToastUtil.showToast(message);
                    }
                });
    }


    //--------------------------- 点击社区新鲜事详情准备跳转 end-------------------------------------

    public void updateItem(JSONObject jsonObject, int type) {
        HashMap<String, View> views = iView.getPagerViews();
        String typeId = jsonObject.getString("neighborBoardTypeId");
        View all = views.get("all");
        if (all != null) {
            NeighborhoodView neighborhoodView = (NeighborhoodView) all.findViewById(R.id.neighborhood);
            neighborhoodView.updateItem(jsonObject, type);
        }
        View child = views.get(typeId);
        if (child != null) {
            NeighborhoodView neighborhoodView = (NeighborhoodView) child.findViewById(R.id.neighborhood);
            neighborhoodView.updateItem(jsonObject, type);
        }
    }

    @Override
    public void getHeadImg() {
//        SignificanceData significanceData = BaseApplication.baseApplication.getSignificanceData();
//        if (significanceData != null && iView != null) {
//            PersonInfo personInfo = PersonInfoManamager.getInstance().getPersonInfo(BaseApplication.baseApplication.getSignificanceData().padAccid);
//            if (personInfo == null) {
//                personInfo = new PersonInfo();
//                personInfo.accId = significanceData.padAccid;
//                personInfo.name = significanceData.familyNickName;
//                personInfo.headImag = significanceData.familyHeadImg;
//                PersonInfoManamager.getInstance().savePersonInfo(personInfo);
//            }
//            iView.setHeadImg(personInfo.headImag);
//        }
    }
}
