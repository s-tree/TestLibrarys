package com.jingxi.smartlife.pad.sdk.neighbor.ui.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingxi.smartlife.pad.sdk.neighbor.NeighborManager;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborInfoBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.IJump;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.AliyunUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.LibAppUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.StringUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.ninegrid.ImageInfo;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.ninegrid.MyNineGridViewAdper;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.ninegrid.NineGridView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.ninegrid.PicassoImageLoader;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;
import com.pk.base.BaseQuickAdapter;
import com.pk.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * 右侧社区新鲜事适配器
 */
public class LeftNeighborhoodAdapter extends BaseQuickAdapter<NeighborInfoBean, BaseViewHolder> {
    private View.OnClickListener onClickListener;
    private IJump iJump;
    private String lastSelectItemId = "";
    private String currentSelectItemId = "";
    private boolean showArrow;

    public LeftNeighborhoodAdapter(int layoutResId, List<NeighborInfoBean> data, IJump iJump, View.OnClickListener onClickListener) {
        super(layoutResId, data);
        this.onClickListener = onClickListener;
        this.iJump = iJump;
    }

    @Override
    protected void convert(BaseViewHolder helper, NeighborInfoBean item) {
        /**
         * 头像
         */
//        PersonInfo personInfo = PersonInfoManamager.getInstance().getPersonInfo(item.accid);
//        if (personInfo == null) {
//            personInfo = new PersonInfo();
//            personInfo.accId = item.accid;
//            personInfo.headImag = item.familyMemberHeadImage;
//            personInfo.name = TextUtils.isEmpty(PersonUtil.getInstance().getName(item.accid)) ? item.familyMemberName : PersonUtil.getInstance().getName(item.accid);
//            PersonInfoManamager.getInstance().savePersonInfo(personInfo);
//        }
        PicassoImageLoader.getMyPicasso()
                .load(LibAppUtils.getImg(item.familyMemberHeadImage))
                .placeholder(R.mipmap.mrtx)
                .error(R.mipmap.mrtx)
                .into(helper.<ImageView>getView(R.id.nickname_img));
        /**
         * 昵称
         */
        helper.setText(R.id.nickname_tv, item.familyMemberName);

        /**
         * 创建时间
         */
        helper.setText(R.id.create_time_tv, LibAppUtils.getFormatTime(item.createDate));
        /**
         * 内容
         */
        if (TextUtils.isEmpty(item.content)) {
            helper.getView(R.id.content_tv).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.content_tv).setVisibility(View.VISIBLE);
            helper.setText(R.id.content_tv, item.content);
        }
        /**
         * 评论数
         */
        helper.setText(R.id.comment_counts_tv, String.valueOf(item.replyCounts));
        helper.setTag(R.id.comment_counts_tv, item);
        /**
         * 点赞
         */
        helper.setText(R.id.left_favourite, String.valueOf(item.favourCounts));
        helper.setTag(R.id.left_favourite, item);
        if (!item.isFavour) {
            Drawable drawable = ContextCompat.getDrawable(JXContextWrapper.context, R.mipmap.neighbor_icon_zan);
            /**
             * 这一步必须要做,否则不会显示
             */
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            helper.<TextView>getView(R.id.left_favourite).setCompoundDrawables(drawable, null, null, null);
        } else {
            Drawable drawable = ContextCompat.getDrawable(JXContextWrapper.context, R.mipmap.neighbor_icon_zan_red);
            /**
             * 这一步必须要做,否则不会显示
             */
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            helper.<TextView>getView(R.id.left_favourite).setCompoundDrawables(drawable, null, null, null);
        }
        helper.setOnClickListener(R.id.left_favourite, onClickListener);
        helper.setTag(R.id.left_favourite, item);
        /**
         * 加载9宫格图片
         */
        List<String> simgList = LibAppUtils.getImageList(item.images);
        ArrayList<ImageInfo> imageInfo = new ArrayList<>();
        for (int i = 0; null != simgList && i < simgList.size(); i++) {
            ImageInfo info = new ImageInfo();
            info.setThumbnailUrl(AliyunUtils.getAliPic(simgList.get(i),200,200));
            info.setBigImageUrl(simgList.get(i));
            imageInfo.add(info);
        }
        MyNineGridViewAdper nineGridViewAdapter = new MyNineGridViewAdper(JXContextWrapper.context, imageInfo,iJump);
        nineGridViewAdapter.canClick = (onClickListener != null);
        helper.<NineGridView>getView(R.id.ninegridview_img).setAdapter(nineGridViewAdapter);
        //整个条目的点击事件
        helper.setTag(R.id.details_cv, item);
        helper.setOnClickListener(R.id.details_cv, onClickListener);
        /**
         * 红点先注释，说不定哪天又让改回去
         */
//        if (!item.noticeIsRead) {
//            ((BGARelativeLayout) helper.getView(R.id.details_cv)).showCirclePointBadge();
//        } else {
//            ((BGARelativeLayout) helper.getView(R.id.details_cv)).hiddenBadge();
//        }

        /**
         * 给布局设置一个tag
         */
        helper.setTag(R.id.nickname_tv, item.neighborBoardTypeId);
        helper.setText(R.id.sort_tv, item.neighborBoardTypeName);
        if (item.neighborBoardTypeColor != null) {
            helper.setBackgroundColor(R.id.sort_tv, Color.parseColor(item.neighborBoardTypeColor));
        }
        helper.setTag(R.id.delete_item, item);
        if (TextUtils.equals(JXContextWrapper.accid, item.accid)) {
            helper.setVisible(R.id.delete_item, true);
            helper.setOnClickListener(R.id.delete_item, onClickListener);
        } else {
            helper.setVisible(R.id.delete_item, false);
        }
        /**
         * 设置右侧箭头
         */
        helper.setVisible(R.id.neighbor_arrow, TextUtils.equals(item.neighborBoardId, currentSelectItemId) && showArrow);
        /**
         * 价格
         */
        if (TextUtils.equals(item.neighborBoardType, NeighborManager.NEIGHBOR_TYPE_SELL)) {
            /**
             * 抗锯齿
             */
            ((TextView) helper.getView(R.id.statu_tv_originalPrice)).getPaint().setAntiAlias(true);
            /**
             * 中划线
             */
            ((TextView) helper.getView(R.id.statu_tv_originalPrice)).getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
            helper.setVisible(R.id.statu_rl_price, true);
            if (TextUtils.isEmpty(item.originalPrice)) {
                helper.setVisible(R.id.statu_tv_originalPrice, false);
            } else {
                helper.setVisible(R.id.statu_tv_originalPrice, true);
                helper.setText(R.id.statu_tv_originalPrice, StringUtils.getString(R.string.oldPrice_with_num,item.originalPrice));
            }
            String nowPrice = StringUtils.getString(R.string.nowPrice_with_num,item.price);
            SpannableString spannableString = new SpannableString(nowPrice);
            int start = nowPrice.indexOf("¥");
            /**
             * 2.0f表示默认字体大小的两倍
             */
            spannableString.setSpan(new RelativeSizeSpan(1.5f), start+1, nowPrice.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            helper.setText(R.id.statu_tv_price, spannableString);
        } else {
            helper.setVisible(R.id.statu_rl_price, false);
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setShowArrow(boolean showArrow) {
        this.showArrow = showArrow;
        if (!showArrow && !TextUtils.isEmpty(lastSelectItemId)) {
            setSelectItemId("-1");
        }
    }

    public void setSelectItemId(String selectId) {
//        currentSelectItemId = selectId;
//        if (!TextUtils.isEmpty(this.lastSelectItemId)) {
//            getIndex(lastSelectItemId).subscribe(new Action1<Integer>() {
//                @Override
//                public void call(Integer integer) {
//                    if (integer != -1) {
//                        notifyItemChanged(integer);
//                    }
//                }
//            });
//        }
//        lastSelectItemId = selectId;
//        getIndex(selectId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<Integer>() {
//            @Override
//            public void call(Integer integer) {
//                if (integer != -1) {
//                    notifyItemChanged(integer);
//                }
//            }
//        });
    }

//    public Observable<Integer> getIndex(String neighborId) {
//        return Observable.just(neighborId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .map(new Func1<String, Integer>() {
//                    @Override
//                    public Integer call(String s) {
//                        for (int i = 0; i < LeftNeighborhoodAdapter.this.getData().size(); i++) {
//                            if (TextUtils.equals(s, LeftNeighborhoodAdapter.this.getData().get(i).neighborBoardId)) {
//                                return i;
//                            }
//                        }
//                        return -1;
//                    }
//                });
//    }
}