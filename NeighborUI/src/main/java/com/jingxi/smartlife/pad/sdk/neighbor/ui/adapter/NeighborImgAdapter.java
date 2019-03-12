package com.jingxi.smartlife.pad.sdk.neighbor.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.ninegrid.PicassoImageLoader;

import java.util.List;

/**
 * Created by kxrt_android_03 on 2017/11/14.
 */

public class NeighborImgAdapter extends Adapter {
    private List<String> imgList;
    private View.OnClickListener onClickListener;

    public NeighborImgAdapter(List<String> imgList, View.OnClickListener onClickListener) {
        this.imgList = imgList;
        this.onClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_neighbor_img, null);
        return new NeighborImgAdapter.NeighborImgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NeighborImgViewHolder viewHolder = (NeighborImgViewHolder) holder;
        Object tag = viewHolder.llItem.getTag();
        String imgUrl = imgList.get(position);
        if (tag == null || !TextUtils.equals(imgUrl, (String)tag)) {
            PicassoImageLoader.getMyPicasso()
                    .load(imgUrl)
                    .placeholder(R.mipmap.ic_placeholderimg)
                    .error(R.mipmap.ic_placeholderimg)
                    .into(viewHolder.iv_neighborImg);
        }
        viewHolder.iv_neighborImg.setOnClickListener(onClickListener);
        viewHolder.iv_neighborImg.setTag(position);
        viewHolder.llItem.setTag(imgUrl);
    }

    @Override
    public int getItemCount() {
        return imgList.size();
    }

    public void setNewData(List<String> imageList) {
        this.imgList = imageList;
        notifyDataSetChanged();
    }

    private class NeighborImgViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_neighborImg;
        View llItem;
        public NeighborImgViewHolder(View itemView) {
            super(itemView);
            iv_neighborImg = (ImageView) itemView.findViewById(R.id.iv_neighborImg);
            llItem = itemView.findViewById(R.id.ll_item_neighbor);
        }
    }
}
