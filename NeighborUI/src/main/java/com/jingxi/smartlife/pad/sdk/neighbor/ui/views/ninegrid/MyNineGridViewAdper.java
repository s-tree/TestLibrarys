package com.jingxi.smartlife.pad.sdk.neighbor.ui.views.ninegrid;

import android.content.Context;
import android.view.View;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.IJump;

import java.util.ArrayList;
import java.util.List;

public class MyNineGridViewAdper extends NineGridViewAdapter {

    public boolean canClick = true;
    private IJump iJump;
    private OnItemClick onItemClick;

    public MyNineGridViewAdper(Context context, List<ImageInfo> imageInfo, IJump iJump) {
        super(context, imageInfo);
        this.iJump = iJump;
    }

    public MyNineGridViewAdper(Context context, List<ImageInfo> imageInfo, IJump iJump, OnItemClick onItemClick) {
        super(context, imageInfo);
        this.iJump = iJump;
        this.onItemClick = onItemClick;
    }

    @Override
    protected void onImageItemClick(Context context, NineGridView nineGridView, int index, List<ImageInfo> imageInfo) {
        if (!canClick) {
            return;
        }
        ArrayList<String> imaggeList = new ArrayList<>();
        for (ImageInfo image : imageInfo) {
            imaggeList.add(image.getBigImageUrl());
        }

        if(onItemClick != null){
            onItemClick.onClick(nineGridView.getChildAt(index),index,imaggeList);
            return;
        }
//        ValidateJumpResult.validateJumpResult(iJump.jump((Activity) context, RouterConfig.getPreviewUri(), RouterBundlesFactory.getPreviewBundle(index,imaggeList,0,0)));
    }

    public interface OnItemClick{
        void onClick(View v, int index, ArrayList<String> imageInfo);
    }
}
