package com.jingxi.smartlife.pad.sdk.neighbor.ui.bean;

import java.io.Serializable;

/**
 * Created by kxrt_android_03 on 2017/11/14.
 */

public class CommentBean implements Serializable {

    /**
     * neighborBoardReplyId : 650
     * content : 。我
     * isFavour : false
     * favour : 0
     * parentReplyMemberName : Wo
     * parentReplyMemberHeadImage : http://smartlife-test.oss-cn-shanghai.aliyuncs.com/headImage/2017-11-06/8372E2D7-6850-4B79-B47D-599FA6319ED7.jpg
     * familyMemberName : 美蓉123
     * familyMemberHeadImage : http://smartlife-test.oss-cn-shanghai.aliyuncs.com/headImage/2017-11-06/82D3FABF-8C08-4179-A8BB-E2D85721C653.jpg
     * accid : t_sl_m_15051280631
     * parentAccId : t_sl_m_13444444444
     * createDate : 1510639740000
     */

    private int neighborBoardReplyId;
    private String content;
    private boolean isFavour;
    private int favour;
    private String parentReplyMemberName;
    private String parentReplyMemberHeadImage;
    private String familyMemberName;
    private String familyMemberHeadImage;
    private String accId;
    private String parentAccId;
    private long createDate;

    public int getNeighborBoardReplyId() {
        return neighborBoardReplyId;
    }

    public void setNeighborBoardReplyId(int neighborBoardReplyId) {
        this.neighborBoardReplyId = neighborBoardReplyId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isIsFavour() {
        return isFavour;
    }

    public void setIsFavour(boolean isFavour) {
        this.isFavour = isFavour;
    }

    public int getFavour() {
        return favour;
    }

    public void setFavour(int favour) {
        this.favour = favour;
    }

    public String getParentReplyMemberName() {
        return parentReplyMemberName;
    }

    public void setParentReplyMemberName(String parentReplyMemberName) {
        this.parentReplyMemberName = parentReplyMemberName;
    }

    public String getParentReplyMemberHeadImage() {
        return parentReplyMemberHeadImage;
    }

    public void setParentReplyMemberHeadImage(String parentReplyMemberHeadImage) {
        this.parentReplyMemberHeadImage = parentReplyMemberHeadImage;
    }

    public String getFamilyMemberName() {
        return familyMemberName;
    }

    public void setFamilyMemberName(String familyMemberName) {
        this.familyMemberName = familyMemberName;
    }

    public String getFamilyMemberHeadImage() {
        return familyMemberHeadImage;
    }

    public void setFamilyMemberHeadImage(String familyMemberHeadImage) {
        this.familyMemberHeadImage = familyMemberHeadImage;
    }

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    public String getParentAccId() {
        return parentAccId;
    }

    public void setParentAccId(String parentAccId) {
        this.parentAccId = parentAccId;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
}
