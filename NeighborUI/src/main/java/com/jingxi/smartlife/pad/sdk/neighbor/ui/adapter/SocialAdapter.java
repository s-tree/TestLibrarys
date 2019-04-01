package com.jingxi.smartlife.pad.sdk.neighbor.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingxi.smartlife.pad.sdk.neighbor.NeighborManager;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborInfoBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.DisplayUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.StringUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.RoundImageView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.ninegrid.PicassoImageLoader;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;

import java.util.List;

public class SocialAdapter extends RecyclerView.Adapter<SocialAdapter.ViewHolder> {
    View.OnClickListener onClickListener;
    private final int bottomHeight;
    private final int bottomWidth;
    private final int screenHeight;
    public List<NeighborInfoBean> infoBeen;

    private boolean isLoadOld = false;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private View footerView;

    public SocialAdapter(List<NeighborInfoBean> infoBeen, View.OnClickListener onClickListener, boolean isLoadOld) {
        this.onClickListener = onClickListener;
        this.infoBeen = infoBeen;
        this.isLoadOld = isLoadOld;
        screenHeight = DisplayUtil.getScreanHeight();
        bottomWidth = (DisplayUtil.getScreanWidth() / 3) - DisplayUtil.dip2px(11f);
        bottomHeight = bottomWidth;
    }

    public void setIsLoadOld(boolean isLoadOld) {
//        this.isLoadOld = isLoadOld;
        this.isLoadOld = false;
    }

    public boolean isLoadOld() {
        return isLoadOld;
    }

    @Override
    public SocialAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_adapter_neighbor_item, parent, false);
            return new SocialAdapter.SearchViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_progressbar, parent, false);
            return new SocialAdapter.FooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(SocialAdapter.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof SearchViewHolder) {
            SearchViewHolder holder = (SearchViewHolder) viewHolder;
            NeighborInfoBean infoBean = infoBeen.get(position);
            if (null == infoBean) {
                return;
            }
            adjustResize(holder);
            infoBean.position = position;
            holder.iv_big.setMaxHeight(bottomHeight);
            holder.neighbor_tv_name.setText(infoBean.familyMemberName);
            holder.neighbor_tv_name.setTag(infoBean.neighborBoardTypeId);
            holder.statu_tv_time.setText(StringUtils.getFormatTime(infoBean.createDate));
            if (TextUtils.equals(infoBean.neighborBoardType, NeighborManager.NEIGHBOR_TYPE_SELL)) {
                /**
                 * 抗锯齿
                 */
                holder.originalPrice.getPaint().setAntiAlias(true);
                /**
                 * 中划线
                 */
                holder.originalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.statu_rl_price.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(infoBean.originalPrice)) {
                    holder.originalPrice.setVisibility(View.GONE);
                } else {
                    holder.originalPrice.setVisibility(View.VISIBLE);
                    holder.originalPrice.setText(StringUtils.getString(R.string.oldPrice_with_num, infoBean.originalPrice));
                }
                String nowPrice = StringUtils.getString(R.string.nowPrice_with_num, infoBean.price);
                SpannableString spannableString = new SpannableString(nowPrice);
                int start = nowPrice.indexOf("¥");
                /**
                 * 2.0f表示默认字体大小的两倍
                 */
                spannableString.setSpan(new RelativeSizeSpan(1.5f), start + 1, nowPrice.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.price.setText(spannableString);
            } else {
                holder.statu_rl_price.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(infoBean.content)) {
                holder.statu_tv_content.setVisibility(View.GONE);
                holder.statu_tv_content.setText(infoBean.content);
            } else {
                holder.statu_tv_content.setVisibility(View.VISIBLE);
                holder.statu_tv_content.setText(infoBean.content);
            }
            holder.praise_tv.setTag(infoBean);
            if (TextUtils.equals(JXContextWrapper.accid, infoBean.accid)) {
                holder.deleteItem.setVisibility(View.VISIBLE);
            } else {
                holder.deleteItem.setVisibility(View.GONE);
            }
            if (null != onClickListener && !isLoadOld) {
                infoBean.nowPosition = position;
                holder.deleteItem.setTag(infoBean);
                holder.praise_tv.setOnClickListener(onClickListener);
                holder.deleteItem.setOnClickListener(onClickListener);
            }
            Drawable drawable = ContextCompat.getDrawable(JXContextWrapper.context,
                    infoBean.isFavour ? R.mipmap.neighbor_icon_zan_red : R.mipmap.neighbor_icon_zan);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.praise_tv.setCompoundDrawables(drawable, null, null, null);
            holder.praise_tv.setTextColor(infoBean.isFavour ? Color.RED : Color.DKGRAY);
            holder.praise_tv.setText(String.valueOf(infoBean.favourCounts));
            holder.comment_tv.setTextColor(Color.DKGRAY);
            String replyCounts = String.valueOf(infoBean.replyCounts);
            holder.comment_tv.setText(TextUtils.isEmpty(replyCounts) ? "0" : replyCounts);
            holder.statu_iv_head.setBorderInsideColor(JXContextWrapper.context.getResources().getColor(R.color.white));
            holder.statu_iv_head.setImageResource(R.mipmap.mrtx);

            PicassoImageLoader.getMyPicasso()
                        .load(PicassoImageLoader.checkUrl(infoBean.familyMemberHeadImage))
                        .error(R.mipmap.mrtx)
                        .config(Bitmap.Config.RGB_565)
                        .resize(150, 150)
                        .into(holder.statu_iv_head);
            /**
             * 加载9宫格图片
             */
            List<String> simgList = StringUtils.getImageList(infoBean.images);
            holder.firstImg.setImageDrawable(null);
            holder.secondImg.setImageDrawable(null);
            holder.thirdImg.setImageDrawable(null);
            holder.iv_big.setImageDrawable(null);

            if (simgList != null && simgList.size() != 0) {
                holder.bottomBG.setVisibility(View.VISIBLE);
                if (simgList.size() > 1) {
                    holder.rl_imags.setVisibility(View.VISIBLE);
                    holder.iv_big.setVisibility(View.GONE);
                } else {
                    holder.rl_imags.setVisibility(View.GONE);
                    holder.iv_big.setVisibility(View.VISIBLE);
                }
                /**
                 * (int) formatHeight(bottomWidth, simgList.get(0))
                 */
                int calculateHeight = bottomWidth;
                int size = simgList.size();
                if (size == 1) {
                    PicassoImageLoader.getMyPicasso()
                            .load(Uri.parse(simgList.get(0)))
                            .resize(bottomWidth, (calculateHeight >= screenHeight) ? screenHeight : calculateHeight)
                            .centerCrop()
                            .placeholder(R.mipmap.ic_placeholderimg)
                            .error(R.mipmap.ico_failure)
                            .config(Bitmap.Config.RGB_565)
                            .into(holder.iv_big);
                }
                if (size > 1) {
                    PicassoImageLoader.getMyPicasso()
                            .load(Uri.parse(simgList.get(0)))
                            .resize(150, 150)
                            .centerCrop()
                            .placeholder(R.mipmap.ic_placeholderimg)
                            .error(R.mipmap.ico_failure)
                            .config(Bitmap.Config.RGB_565)
                            .into(holder.firstImg);
                    PicassoImageLoader.getMyPicasso()
                            .load(Uri.parse(simgList.get(1)))
                            .resize(150, 150)
                            .centerCrop()
                            .placeholder(R.mipmap.ic_placeholderimg)
                            .error(R.mipmap.ico_failure)
                            .config(Bitmap.Config.RGB_565)
                            .into(holder.secondImg);
                }
                if (size > 2) {
                    PicassoImageLoader.getMyPicasso()
                            .load(Uri.parse(simgList.get(2)))
                            .resize(150, 150)
                            .centerCrop()
                            .placeholder(R.mipmap.ic_placeholderimg)
                            .error(R.mipmap.ico_failure)
                            .config(Bitmap.Config.RGB_565)
                            .into(holder.thirdImg);
                }
                if (simgList.size() > 3) {
                    holder.moreImg.setVisibility(View.VISIBLE);
                    holder.moreImg.setText(JXContextWrapper.context.getString(R.string.plus).concat(String.valueOf(simgList.size() - 3)));
                } else {
                    holder.moreImg.setVisibility(View.GONE);
                }
            } else {
                holder.bottomBG.setVisibility(View.GONE);
                holder.moreImg.setVisibility(View.GONE);
            }
            if (!isLoadOld) {
                holder.details_cv.setOnClickListener(onClickListener);
            }
            holder.details_cv.setTag(infoBean);
            holder.status_tag.setText(infoBean.neighborBoardTypeName);
            if (!TextUtils.isEmpty(infoBean.neighborBoardTypeColor)) {
                holder.status_tag.setBackgroundColor(Color.parseColor(infoBean.neighborBoardTypeColor));
            }
        } else {
            FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
            footerView = footerViewHolder.itemView;
            if (infoBeen.isEmpty()) {
                footerView.setVisibility(View.GONE);
            } else {
                footerView.setVisibility(isShow ? View.VISIBLE : View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return infoBeen == null ? 1 : infoBeen.size() + 1;
    }

    private boolean isShow;

    public void showFooter(boolean isShow) {
        this.isShow = isShow;
        if (null != footerView) {
            footerView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }

    private void adjustResize(SearchViewHolder viewHolder) {
        ViewGroup.LayoutParams bottomLayoutParams = viewHolder.iv_big.getLayoutParams();
        bottomLayoutParams.width = bottomWidth;
        bottomLayoutParams.height = bottomHeight;
        viewHolder.iv_big.setLayoutParams(bottomLayoutParams);
    }

    @Override
    public int getItemViewType(int position) {
        /**
         * 最后一个item设置为footerView
         */
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class FooterViewHolder extends SocialAdapter.ViewHolder {

        FooterViewHolder(View view) {
            super(view);
        }

    }

    class SearchViewHolder extends SocialAdapter.ViewHolder {
        TextView neighbor_tv_name, statu_tv_time, statu_tv_content, status_tag, comment_tv, praise_tv, deleteItem, originalPrice, price;
        /**
         * 点赞,分享,留言
         */
        public ImageView guest_iv;
        /**
         * 头像
         */
        RoundImageView statu_iv_head;
        /**
         * 详情
         */
        RelativeLayout details_cv, statu_rl_price;
        ImageView firstImg;
        ImageView secondImg;
        ImageView thirdImg;
        RelativeLayout bottomBG;
        TextView moreImg;
        public RelativeLayout rl_imags;
        private final ImageView iv_big;

        SearchViewHolder(View itemView) {
            super(itemView);
            status_tag = (TextView) itemView.findViewById(R.id.status_tag);
            statu_iv_head = (RoundImageView) itemView.findViewById(R.id.statu_iv_head);
            details_cv = (RelativeLayout) itemView.findViewById(R.id.details_cv);
            neighbor_tv_name = (TextView) itemView.findViewById(R.id.neighbor_tv_name);
            statu_tv_time = (TextView) itemView.findViewById(R.id.statu_tv_time);
            statu_tv_content = (TextView) itemView.findViewById(R.id.statu_tv_content);
            deleteItem = (TextView) itemView.findViewById(R.id.delete_item);
            firstImg = (ImageView) itemView.findViewById(R.id.first_img);
            secondImg = (ImageView) itemView.findViewById(R.id.second_img);
            thirdImg = (ImageView) itemView.findViewById(R.id.third_img);
            bottomBG = (RelativeLayout) itemView.findViewById(R.id.bottom_img);
            moreImg = (TextView) itemView.findViewById(R.id.more_img);
            comment_tv = (TextView) itemView.findViewById(R.id.commentView);
            praise_tv = (TextView) itemView.findViewById(R.id.favourite);
            rl_imags = (RelativeLayout) itemView.findViewById(R.id.rl_imags);
            iv_big = (ImageView) itemView.findViewById(R.id.status_iv_big);
            statu_rl_price = (RelativeLayout) itemView.findViewById(R.id.statu_rl_price);
            originalPrice = (TextView) itemView.findViewById(R.id.statu_tv_originalPrice);
            price = (TextView) itemView.findViewById(R.id.statu_tv_price);
        }

    }
}

