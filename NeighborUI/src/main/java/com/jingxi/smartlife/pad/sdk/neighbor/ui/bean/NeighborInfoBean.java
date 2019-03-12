package com.jingxi.smartlife.pad.sdk.neighbor.ui.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by lb on 2017/11/13.
 */

public class NeighborInfoBean implements Serializable {

    public static NeighborInfoBean Empty = null;
    /**
     * neighborBoardId : 762
     * isFavour : false
     * content : 谢谢
     * replyCounts : 8
     * images : null
     * createDate : 1510299840000
     * accid : t_sl_p_74_13656216143
     * favourCounts : 2
     * familyMemberName : hy
     * familyMemberHeadImage : http://smartlife-test.oss-cn-shanghai.aliyuncs.com/headImage/2017-10-26/48D54388-DE5A-4CEB-B461-5B27C8EFF7C7.jpg
     * neighborBoardTypeColor : #129cf6
     * neighborBoardTypeId : 1
     * neighborBoardTypeName : 邻里闲聊
     * originalPrice : null
     * price : null
     * activityTime : null
     * activityLocation : null
     * activityInfo : 谢谢
     * activityDeadline : null
     * isJoinedActivity : false
     * joinedCount : 0
     */
    public int _num;
    public String neighborBoardId;
    public boolean isFavour;
    public String content;
    public int replyCounts;
    public String images;
    public long createDate;
    public String accid;
    public int favourCounts;
    public String familyMemberName;
    public String familyMemberHeadImage;
    public String neighborBoardTypeColor;
    public int neighborBoardTypeId;
    public String neighborBoardTypeName;
    public String neighborBoardType;
    public String originalPrice;
    public String price;
    public String activityTime;
    public String activityLocation;
    public String activityInfo;
    public String activityDeadline;
    public boolean isJoinedActivity;
    public int joinedCount;
    public boolean noticeIsRead;
    public int position = -1;
    public int nowPosition = -1;

    public static NeighborInfoBean getEmpty(String neighborBoardId) {
        if (Empty == null) {
            Empty = new NeighborInfoBean();
        }
        Empty.neighborBoardId = neighborBoardId;
        return Empty;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NeighborInfoBean) {
            return TextUtils.equals(((NeighborInfoBean) o).neighborBoardId, neighborBoardId);
        }
        if (o instanceof String) {
            return TextUtils.equals((String) o, neighborBoardId);
        }
        return false;
    }
}
