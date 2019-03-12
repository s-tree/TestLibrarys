package com.jingxi.smartlife.pad.sdk.neighbor.ui.bean;

/**
 * Created by lb on 2017/11/13.
 */

public class NeighborDetailDo {
    /**
     * 0点赞
     */
    public static final int TYPE_FAVOUR = 0;

    /**
     * 1条目点击显示详情
     */
    public static final int TYPE_DETAIL = 1;

    /**
     * 2条目点击完成跳转
     */
    public static final int TYPE_GO_DETAIL = 2;

    /**
     * 3.我的消息
     */
    public static final int TYPE_MY_MESSAGE = 3;

    /**
     * 4.我的消息，条目删除同时小红点减1
     */
    public static final int TYPE_MY_DELETE = 4;

    /**
     * 用于左右详情页详情页点赞后发送到NeighBorDetialFragment进行处理
     */
    public int type;
    public NeighborInfoBean neighborInfoBean;
}
