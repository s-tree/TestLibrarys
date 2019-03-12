package com.jingxi.smartlife.pad.sdk.neighbor.ui.bean;

import com.litesuits.orm.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/17.
 */
@Table("neighbor")
public class NeighborBean implements Serializable {

    /**
     * accid : t_sl_p_58_13160381628
     * activityInfo : ttt5
     * activityStatus : true
     * content : ttt5
     * createDate : 1530509877000
     * familyMemberHeadImage : http://smartlife-test.oss-cn-shanghai.aliyuncs.com/headimage/2018-07-02/11455077-628d-4c98-a2f2-5bf3640f69c1.jpg
     * familyMemberName : jj
     * favourCounts : 0
     * images : http://smartlife-test.oss-cn-shanghai.aliyuncs.com/neighbor/2018-07-02/7ff61956-d8ee-4906-b011-64e3bf7a2ace.jpg
     * isFavour : false
     * isJoinedActivity : false
     * joinedCount : 0
     * neighborBoardId : 4099
     * neighborBoardTypeColor : #4ac89a
     * neighborBoardTypeId : 5
     * neighborBoardTypeName : 邻里闲置
     * noticeIsRead : true
     * originalPrice : 10.00
     * price : 5.00
     * replyCounts : 0
     */

    private String accid;
    private String activityInfo;
    private boolean activityStatus;
    private String content;
    private long createDate;
    private String familyMemberHeadImage;
    private String familyMemberName;
    private int favourCounts;
    private String images;
    private boolean isFavour;
    private boolean isJoinedActivity;
    private int joinedCount;
    private int neighborBoardId;
    private String neighborBoardTypeColor;
    private int neighborBoardTypeId;
    private String neighborBoardTypeName;
    private boolean noticeIsRead;
    private String originalPrice;
    private String price;
    private int replyCounts;
    /**
     * activityDeadline : 1533188220000
     */

    private long activityDeadline;

    public String getAccid() {
        return accid;
    }

    public void setAccid(String accid) {
        this.accid = accid;
    }

    public String getActivityInfo() {
        return activityInfo;
    }

    public void setActivityInfo(String activityInfo) {
        this.activityInfo = activityInfo;
    }

    public boolean isActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(boolean activityStatus) {
        this.activityStatus = activityStatus;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getFamilyMemberHeadImage() {
        return familyMemberHeadImage;
    }

    public void setFamilyMemberHeadImage(String familyMemberHeadImage) {
        this.familyMemberHeadImage = familyMemberHeadImage;
    }

    public String getFamilyMemberName() {
        return familyMemberName;
    }

    public void setFamilyMemberName(String familyMemberName) {
        this.familyMemberName = familyMemberName;
    }

    public int getFavourCounts() {
        return favourCounts;
    }

    public void setFavourCounts(int favourCounts) {
        this.favourCounts = favourCounts;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public boolean isIsFavour() {
        return isFavour;
    }

    public void setIsFavour(boolean isFavour) {
        this.isFavour = isFavour;
    }

    public boolean isIsJoinedActivity() {
        return isJoinedActivity;
    }

    public void setIsJoinedActivity(boolean isJoinedActivity) {
        this.isJoinedActivity = isJoinedActivity;
    }

    public int getJoinedCount() {
        return joinedCount;
    }

    public void setJoinedCount(int joinedCount) {
        this.joinedCount = joinedCount;
    }

    public int getNeighborBoardId() {
        return neighborBoardId;
    }

    public void setNeighborBoardId(int neighborBoardId) {
        this.neighborBoardId = neighborBoardId;
    }

    public String getNeighborBoardTypeColor() {
        return neighborBoardTypeColor;
    }

    public void setNeighborBoardTypeColor(String neighborBoardTypeColor) {
        this.neighborBoardTypeColor = neighborBoardTypeColor;
    }

    public int getNeighborBoardTypeId() {
        return neighborBoardTypeId;
    }

    public void setNeighborBoardTypeId(int neighborBoardTypeId) {
        this.neighborBoardTypeId = neighborBoardTypeId;
    }

    public String getNeighborBoardTypeName() {
        return neighborBoardTypeName;
    }

    public void setNeighborBoardTypeName(String neighborBoardTypeName) {
        this.neighborBoardTypeName = neighborBoardTypeName;
    }

    public boolean isNoticeIsRead() {
        return noticeIsRead;
    }

    public void setNoticeIsRead(boolean noticeIsRead) {
        this.noticeIsRead = noticeIsRead;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getReplyCounts() {
        return replyCounts;
    }

    public void setReplyCounts(int replyCounts) {
        this.replyCounts = replyCounts;
    }

    public long getActivityDeadline() {
        return activityDeadline;
    }

    public void setActivityDeadline(long activityDeadline) {
        this.activityDeadline = activityDeadline;
    }
}
