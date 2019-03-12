package com.jingxi.smartlife.pad.sdk.neighbor.ui.bean;

public class GoDetailBusBean {

    public GoDetailBusBean(NeighborInfoBean infoBean, int detailType) {
        this.infoBean = infoBean;
        this.detailType = detailType;
    }

    public NeighborInfoBean infoBean;

    public int detailType;
}
