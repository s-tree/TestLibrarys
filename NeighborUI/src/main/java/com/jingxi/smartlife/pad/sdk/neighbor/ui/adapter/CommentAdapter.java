package com.jingxi.smartlife.pad.sdk.neighbor.ui.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.CommentBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.LibAppUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.StringUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.RoundImageView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.ninegrid.PicassoImageLoader;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;

import java.text.SimpleDateFormat;
import java.util.List;


/**
 * Created by kxrt_android_03 on 2017/11/14.
 */

public class CommentAdapter extends RecyclerView.Adapter {
    /**
     * 普通Item View
     */
    private static final int TYPE_ITEM = 0;
    /**
     * 底部FootView
     */
    private static final int TYPE_FOOTER = 1;
    /**
     * 上拉加载更多
     */
    public static final int PULLUP_LOAD_MORE = 1;
    /**
     * 没有更多数据
     */
    public static final int NO_MORE_DATA = 2;
    /**
     * 上拉加载更多状态-默认为0
     */
    public static final int NO_FULL_PAGE = 3;
    public int load_more_status;
    private List<CommentBean> commentBeanList;
    private View.OnClickListener onClickListener;

    public CommentAdapter(List<CommentBean> commentBeanList, View.OnClickListener onClickListener) {
        this.commentBeanList = commentBeanList;
        this.onClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, null);
            view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new CommentAdapter.CommentViewHoler(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.def_load_more_failed, null);
            view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentViewHoler) {
            CommentViewHoler commentViewHoler = (CommentViewHoler) holder;
            CommentBean bean = commentBeanList.get(position);
            PicassoImageLoader.getMyPicasso()
                    .load(bean.getFamilyMemberHeadImage())
                    .placeholder(R.mipmap.mrtx).error(R.mipmap.mrtx)
                    .into(commentViewHoler.riv_commentPic);
            commentViewHoler.tv_commentName.setText(bean.getFamilyMemberName());
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
            commentViewHoler.tv_commentTime.setText(sdf.format(bean.getCreateDate()));
            if (TextUtils.isEmpty(bean.getParentReplyMemberName())) {
                commentViewHoler.tv_commentContent.setText(bean.getContent());
            } else {
                String nickName = bean.getParentReplyMemberName();
                commentViewHoler.tv_commentContent.setText(LibAppUtils.setDifTvColoc(JXContextWrapper.context, Color.parseColor("#4483f8"),
                        TextUtils.concat("回复@",nickName,":",bean.getContent()).toString(), 2, 3 + nickName.length()));
            }
            commentViewHoler.rl_content.setOnClickListener(onClickListener);
            commentViewHoler.rl_content.setTag(bean);
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            switch (load_more_status) {
                case PULLUP_LOAD_MORE:
                    footerViewHolder.tv_footerView.setText(StringUtils.getString(R.string.loading));
                    break;
                case NO_MORE_DATA:
                    footerViewHolder.tv_footerView.setText(StringUtils.getString(R.string.no_more_data));
                    break;
                case NO_FULL_PAGE:
                    footerViewHolder.tv_footerView.setText("");
                    break;
                default:
            }
        }
    }

    public void changeMoreStatus(int i) {
        load_more_status = i;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (commentBeanList != null) {
            return commentBeanList.size() + 1;
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public void setNewData(List<CommentBean> commentBeanList) {
        this.commentBeanList.clear();
        this.commentBeanList.addAll(commentBeanList);
        notifyDataSetChanged();
    }

    public void clearData() {
        if (this.commentBeanList != null) {
            this.commentBeanList.clear();
        }
    }

    public void addData(List<CommentBean> commentBeanList) {
        this.commentBeanList.addAll(commentBeanList);
        notifyDataSetChanged();
    }

    public List<CommentBean> getData() {
        return commentBeanList;
    }

    class CommentViewHoler extends RecyclerView.ViewHolder {
        RoundImageView riv_commentPic;
        TextView tv_commentName, tv_commentTime, tv_commentContent;
        RelativeLayout rl_content;

        public CommentViewHoler(View itemView) {
            super(itemView);
            riv_commentPic = (RoundImageView) itemView.findViewById(R.id.riv_commentPic);
            tv_commentName = (TextView) itemView.findViewById(R.id.tv_commentName);
            tv_commentTime = (TextView) itemView.findViewById(R.id.tv_commentTime);
            tv_commentContent = (TextView) itemView.findViewById(R.id.tv_commentContent);
            rl_content = (RelativeLayout) itemView.findViewById(R.id.rl_content);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView tv_footerView;

        public FooterViewHolder(View view) {
            super(view);
            tv_footerView = (TextView) view.findViewById(R.id.tv_prompt);
        }
    }
}
