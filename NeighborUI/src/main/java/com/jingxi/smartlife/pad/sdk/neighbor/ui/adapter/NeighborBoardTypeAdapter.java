package com.jingxi.smartlife.pad.sdk.neighbor.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingxi.smartlife.pad.sdk.neighbor.NeighborManager;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborBoardTypeBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.ninegrid.PicassoImageLoader;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;

import java.util.List;

/**
 * Created by kxrt_android_03 on 2017/11/8.
 */

public class NeighborBoardTypeAdapter extends RecyclerView.Adapter {
    private List<NeighborBoardTypeBean> neighborBoardTypeBeanList;
    private View.OnClickListener onClickListener;

    public NeighborBoardTypeAdapter(List<NeighborBoardTypeBean> neighborBoardTypeBeanList, View.OnClickListener onClickListener) {
        this.neighborBoardTypeBeanList = neighborBoardTypeBeanList;
        this.onClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_neighbor_type_item, parent, false);
        view.setLayoutParams(
                new RecyclerView.LayoutParams(
                        (int) JXContextWrapper.context.getResources().getDimension(R.dimen.dp_208),
                (int) JXContextWrapper.context.getResources().getDimension(R.dimen.dp_120)));
        return new NeighborBoardTypeAdapter.NeighborBoardTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NeighborBoardTypeBean bean = neighborBoardTypeBeanList.get(position);
        NeighborBoardTypeViewHolder viewHolder = (NeighborBoardTypeViewHolder) holder;
        PicassoImageLoader.getMyPicasso().load(getPicByType(bean.type))
                .error(R.mipmap.ic_placeholderimg)
                .resize(200, 200)
                .into(viewHolder.iv_boardType);
        viewHolder.iv_boardType.setTag(bean);
        viewHolder.iv_boardType.setOnClickListener(onClickListener);
        viewHolder.tv_boardType.setText(bean.getName());
    }

    @Override
    public int getItemCount() {
        return neighborBoardTypeBeanList.size();
    }

    private class NeighborBoardTypeViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_boardType;
        TextView tv_boardType;

        private NeighborBoardTypeViewHolder(View itemView) {
            super(itemView);
            iv_boardType = (ImageView) itemView.findViewById(R.id.iv_boardType);
            tv_boardType = (TextView) itemView.findViewById(R.id.tv_boardType);
        }
    }

    private int getPicByType(String type) {
        if (TextUtils.equals(type, NeighborManager.NEIGHBOR_TYPE_CHAT)) {
            return R.mipmap.neighbor_llxl;
        } else if (TextUtils.equals(type, NeighborManager.NEIGHBOR_TYPE_ACTIVITY)) {
            return R.mipmap.neighbor_llhd;
        }
        if (TextUtils.equals(type, NeighborManager.NEIGHBOR_TYPE_HELP)) {
            return R.mipmap.neighbor_llhz;
        }
        if (TextUtils.equals(type, NeighborManager.NEIGHBOR_TYPE_SELL)) {
            return R.mipmap.neighbor_llxz;
        }
        return R.mipmap.neighbor_llxl;
    }
}
