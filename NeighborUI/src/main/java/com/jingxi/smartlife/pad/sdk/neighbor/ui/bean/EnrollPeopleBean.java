package com.jingxi.smartlife.pad.sdk.neighbor.ui.bean;

import java.io.Serializable;

/**
 * Created by kxrt_android_03 on 2017/11/17.
 */

public class EnrollPeopleBean implements Serializable {
    /**
     * familyMemberName : 李敏手机
     * familyMemberNickName : 曼曼同学123
     * familyMemberMobile : 15050952519
     * familyMemberHeadImage : http://smartlife-test.oss-cn-shanghai.aliyuncs.com/headImage/2017-11-16/8305387C-746E-465F-8972-FA1DDA8697D1.jpg
     * applyTime : 1510910067000
     * address : 汤臣一品2号楼1单元4-401室
     */

    private String familyMemberName;
    private String familyMemberNickName;
    private String familyMemberMobile;
    private String familyMemberHeadImage;
    private long applyTime;
    private String address;
    public String accid;

    public String getFamilyMemberName() {
        return familyMemberName;
    }

    public void setFamilyMemberName(String familyMemberName) {
        this.familyMemberName = familyMemberName;
    }

    public String getFamilyMemberNickName() {
        return familyMemberNickName;
    }

    public void setFamilyMemberNickName(String familyMemberNickName) {
        this.familyMemberNickName = familyMemberNickName;
    }

    public String getFamilyMemberMobile() {
        return familyMemberMobile;
    }

    public void setFamilyMemberMobile(String familyMemberMobile) {
        this.familyMemberMobile = familyMemberMobile;
    }

    public String getFamilyMemberHeadImage() {
        return familyMemberHeadImage;
    }

    public void setFamilyMemberHeadImage(String familyMemberHeadImage) {
        this.familyMemberHeadImage = familyMemberHeadImage;
    }

    public long getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(long applyTime) {
        this.applyTime = applyTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
