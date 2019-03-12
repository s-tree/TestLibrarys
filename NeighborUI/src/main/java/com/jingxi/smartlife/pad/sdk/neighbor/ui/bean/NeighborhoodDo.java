package com.jingxi.smartlife.pad.sdk.neighbor.ui.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2017/5/10.
 */

public class NeighborhoodDo {

    /**
     * 查看详情
     */
    public static final int TYPE_DETAIL = 0;
    /**
     * 点赞
     */
    public static final int TYPE_FAVOUR = 1;
    /**
     * 删除
     */
    public static final int TYPE_DELETE = 2;
    /**
     * 发布
     */
    public static final int TYPE_REPORT = 3;

    public int type ;
    public JSONObject jsonObject;
    public String typeID;
}
