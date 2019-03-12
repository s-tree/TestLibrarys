package com.jingxi.smartlife.pad.sdk.neighbor.ui.utils;

import com.alibaba.fastjson.JSONObject;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborInfoBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborNoticeBean;
import com.jingxi.smartlife.pad.util.PadHttpParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by kxrt_android_03 on 2017/11/7.
 */

public class NeighborhoodUtil {
    public final static int PAGESIZE = 20;

    /**
     * 对社区新鲜事加载数据进行去重
     *
     * @param oldList
     * @param newList
     * @return
     */
    public static List<NeighborInfoBean> getDuplicatedList(List<NeighborInfoBean> oldList, List<NeighborInfoBean> newList) {
        HashMap<String, NeighborInfoBean> hashMap = new HashMap<>();
        for (NeighborInfoBean infoBean : oldList) {
            hashMap.put(infoBean.neighborBoardId, infoBean);
        }
        List<NeighborInfoBean> neighborInfoBeanList = new ArrayList<>();
        for (NeighborInfoBean infoBean : newList) {
            if (!hashMap.containsKey(infoBean.neighborBoardId)) {
                neighborInfoBeanList.add(infoBean);
            }
        }
        return neighborInfoBeanList;
    }

    /**
     * 社区新鲜事有些地方没有使用NeighborInfoBean，需要使用该方法对JSONObject去重
     *
     * @param oldList
     * @param newList
     * @return
     */
    public static List<JSONObject> getDuplicatedJsonList(List<JSONObject> oldList, List<JSONObject> newList) {
        HashMap<String, JSONObject> hashMap = new HashMap<>();
        for (JSONObject jsonObject : oldList) {
            hashMap.put(jsonObject.getString(PadHttpParams.NeighborHood.Key.NEIGHBORBOARDID), jsonObject);
        }
        List<JSONObject> neighborInfoBeanList = new ArrayList<>();
        for (JSONObject jsonObject : newList) {
            if (!hashMap.containsKey(jsonObject.getString(PadHttpParams.NeighborHood.Key.NEIGHBORBOARDID))) {
                neighborInfoBeanList.add(jsonObject);
            }
        }
        return neighborInfoBeanList;
    }

    public static List<NeighborNoticeBean> getDuplicatedNoticeList(List<NeighborNoticeBean> oldList, List<NeighborNoticeBean> newList) {
        HashMap<Integer, NeighborNoticeBean> hashMap = new HashMap<>();
        for (NeighborNoticeBean neighborNoticeBean : oldList) {
            hashMap.put(neighborNoticeBean.neighborBoardNoticeId, neighborNoticeBean);
        }
        ArrayList<NeighborNoticeBean> noticeBeanList = new ArrayList<>();
        for (NeighborNoticeBean neighborNoticeBean : newList) {
            if (!hashMap.containsKey(neighborNoticeBean.neighborBoardNoticeId)) {
                noticeBeanList.add(neighborNoticeBean);
            }
        }
        return noticeBeanList;
    }
}
