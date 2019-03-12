package com.jingxi.smartlife.pad.sdk.neighbor.ui.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * 社区新鲜事更新和系统通知
 */
public class UpdateData {

    /**
     * 新增邻里墙
     */
    public static final int TYPE_ADD_NEIGHBOR = -1;
    public static final int TYPE_NOT_HOE = -2;
    public static final int TYPE_ADD_FAMILY = -3;
    /**
     * 更新家庭头像
     */
    public static final int TYPE_UPDATE_HEADIMG = -4;
    public static final int TYPE_UPDATE_FRIEND_BY_PHONE = -5;
    public static final int TYPE_UPDATE_FRIEND_BY_PAD = -6;
    /**
     * pad 登出
     */
    public static final int TYPE_LOGINOUT = -7;
    public static final int TYPE_PHONE_REGISTER_OK = -8;
    public static final int TYPE_GO_SOS = -9;
    /**
     * pad 登入
     */
    public static final int TYPE_PAD_LOGIN = -10;
    /**
     * 刷新联系人
     */
    public static final int TYPE_FRIEND_REFRESHED = -11;
    public static final int TYPE_GET_ADDRESS = -12;
    /**
     * 积分更新
     */
    public static final int TYPE_UPDATE_POINT = -13;
    public static final int TYPE_ADD_DEL_FRIEND = -100;
    public static final int TYPE_UPDATE_TOP_CONTACTS = -101;
    /**
     * 常用联系人更新
     */
    public static final int TYPE_TENTATIVE = -102;
    public static final int TYPE_UPDATE_MESSAGEBOARD = -400;
    /**
     * 销毁添加联系人页面
     */
    public static final int TYPE_DESTROY_SEARCHACTIVITY = -500;
    /**
     * 更新指定邻里墙的点赞和评论
     */
    public static final int TYPE_UPDATE_NEIGHBOR_COMMENTS_FAV = 10;
    /**
     * 删除指定邻里墙
     */
    public static final int TYPE_NEIGHBOR_DEL = 3;
    /**
     * 更新指定邻里墙的点赞
     */
    public static final int TYPE_NEIGHBOR_UPDATE_FAV = 2;
    /**
     * 更新指定邻里墙的评论
     */
    public static final int TYPE_NEIGHBOR_UPDATE_COMMENT = 1;

    /**
     * @deprecated
     * old tips
     * 大于等于0表示更新社区新鲜事;
     * -1表示发布社区新鲜事;
     * -2不在本家庭;
     * -3添加家庭成员(弃用);
     * -4更新家庭头像;
     * -5手机业主对pad联系人列表进行修改;
     * -6pad对联系人列表进行修改(修改昵称头像,pad添加联系人)
     * -7pad登出
     * -8手机端注册成功
     * -9跳转SOS界面
     * -10pad登陆
     *
     * -11更新所有好友验证完成
     * -12获取地址完成
     * -13更新积分
     *
     * -100平板添加删除联系人
     * -101更新常用联系人
     *
     * -102 tentative
     * -400  留言板更新
     *
     * -500  搜索界面进入聊天界面逻辑处理(搜索界面销毁)
     * 10.更新点赞，评论数，点赞数
     * 3.删除数据
     * 2.更新邻里点赞
     * 1.更新社区新鲜事评论
     */

    private int id;
    private boolean praise = false;
    private JSONObject jsonObject;

    public int getId() {
        return id;
    }

    public UpdateData setId(int id) {
        this.id = id;
        return this;
    }

    public boolean isPraise() {
        return praise;
    }

    public UpdateData setPraise(boolean praise) {
        this.praise = praise;
        return this;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public UpdateData setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        return this;
    }

}
