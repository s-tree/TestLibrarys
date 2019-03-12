package com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONArray;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.adapter.NeighborBoardTypeAdapter;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborBoardTypeBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.DisplayUtil;

import java.util.List;

/**
 * Created by kxrt_android_03 on 2017/11/8.
 */

public class ReleaseTypeDialog extends BaseLibDialog {
    private RecyclerView rv_releaseType;
    private ImageView iv_closeType;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private NeighborBoardTypeAdapter neighborBoardTypeAdapter;
    private View.OnClickListener onClickListener;

    /**
     * 对话框
     *
     * @param context 上下文
     */
    public ReleaseTypeDialog(@NonNull Context context, View.OnClickListener onClickListener) {
        super(context);
        this.onClickListener = onClickListener;
        initDialog();
    }

    private void initDialog() {
        iv_closeType = (ImageView) findViewById(R.id.iv_closeType);
        rv_releaseType = (RecyclerView) findViewById(R.id.rv_releaseType);
        rv_releaseType.setMotionEventSplittingEnabled(false);
        rv_releaseType.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        rv_releaseType.setLayoutManager(staggeredGridLayoutManager);
        iv_closeType.setOnClickListener(onClickListener);
    }

    public void setTypes(List<NeighborBoardTypeBean> types){
        for(NeighborBoardTypeBean typeBean : types){
            if(TextUtils.equals(typeBean.neighborBoardTypeId,"all")){
                types.remove(typeBean);
                break;
            }
        }
        if (neighborBoardTypeAdapter == null) {
            neighborBoardTypeAdapter = new NeighborBoardTypeAdapter(types, onClickListener);
            rv_releaseType.setAdapter(neighborBoardTypeAdapter);
        }
    }

    @Override
    protected boolean getHideInput() {
        return false;
    }

    @Override
    protected float getWidth() {
        return DisplayUtil.getScreanWidth();
    }

    @Override
    protected float getHeight() {
        return DisplayUtil.getScreanHeight();
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.dialog_release_type;
    }

    @Override
    protected boolean getCanceledOnTouchOutside() {
        return false;
    }
}
