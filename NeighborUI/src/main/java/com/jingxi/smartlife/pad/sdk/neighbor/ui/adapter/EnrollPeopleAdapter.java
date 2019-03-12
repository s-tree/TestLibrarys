package com.jingxi.smartlife.pad.sdk.neighbor.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.EnrollPeopleBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.LibAppUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.RoundImageView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.ninegrid.PicassoImageLoader;

import java.util.List;


/**
 * Created by kxrt_android_03 on 2017/11/20.
 */

public class EnrollPeopleAdapter extends RecyclerView.Adapter {
    private List<EnrollPeopleBean> enrollPeopleBeanList;

    public EnrollPeopleAdapter(List<EnrollPeopleBean> enrollPeopleBeanList) {
        this.enrollPeopleBeanList = enrollPeopleBeanList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_enrollpeople, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new EnrollPeopleAdapter.EnrollPeopleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        EnrollPeopleViewHolder viewHolder = (EnrollPeopleViewHolder) holder;
        EnrollPeopleBean bean = enrollPeopleBeanList.get(position);
        viewHolder.tv_enrollName.setText(bean.getFamilyMemberNickName());
        String imgUrl = bean.getFamilyMemberHeadImage();
        PicassoImageLoader.getMyPicasso()
                .load(imgUrl)
                .placeholder(R.mipmap.mrtx)
                .into(viewHolder.riv_enrollPic);
        viewHolder.tv_enrollTime.setText(LibAppUtils.getTimeDataToString(bean.getApplyTime(), "MM-dd HH:mm"));
        viewHolder.tv_realName.setText(bean.getFamilyMemberName());
        viewHolder.tv_enrollTel.setText(bean.getFamilyMemberMobile());
    }

    @Override
    public int getItemCount() {
        return enrollPeopleBeanList.size();
    }

    public void setNewData(List<EnrollPeopleBean> enrollPeopleBeanList) {
        this.enrollPeopleBeanList.clear();
        this.enrollPeopleBeanList.addAll(enrollPeopleBeanList);
        notifyDataSetChanged();
    }

    public void clearDate() {
        if (enrollPeopleBeanList != null) {
            enrollPeopleBeanList.clear();
        }
    }

    class EnrollPeopleViewHolder extends RecyclerView.ViewHolder {
        RoundImageView riv_enrollPic;
        TextView tv_enrollName, tv_enrollTime, tv_realName, tv_enrollTel;

        public EnrollPeopleViewHolder(View itemView) {
            super(itemView);
            riv_enrollPic = (RoundImageView) itemView.findViewById(R.id.riv_enrollPic);
            tv_enrollName = (TextView) itemView.findViewById(R.id.tv_enrollName);
            tv_enrollTime = (TextView) itemView.findViewById(R.id.tv_enrollTime);
            tv_realName = (TextView) itemView.findViewById(R.id.tv_realName);
            tv_enrollTel = (TextView) itemView.findViewById(R.id.tv_enrollTel);
        }
    }
}
