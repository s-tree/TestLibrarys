package com.jingxi.smartlife.pad.sdk.neighbor.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborNoticeBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.LibAppUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.StringUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.MultiStyleTextView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.ninegrid.PicassoImageLoader;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;
import com.pk.base.BaseQuickAdapter;
import com.pk.base.BaseViewHolder;

import java.util.List;

/**
 * 我的消息
 *
 * @author HJk
 */
public class NeighBorhoodNoticeAdapter extends BaseQuickAdapter<NeighborNoticeBean, BaseViewHolder> {

    private View.OnClickListener onClickListener;

    public NeighBorhoodNoticeAdapter(List<NeighborNoticeBean> data, View.OnClickListener onClickListener) {
        super(R.layout.neighbor_notice_item, data);
        this.onClickListener = onClickListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, NeighborNoticeBean item) {
        /**
         * 回复人信息
         */
        MultiStyleTextView replyContent = helper.getView(R.id.replyContent);
        if (TextUtils.equals(NeighborNoticeBean.TYPE_REPLY, item.type)) {
            String replyParentName = item.replyParentMemberName;
            if (TextUtils.isEmpty(replyParentName) || TextUtils.equals(item.replyParentMemberAccId, JXContextWrapper.accid)) {
                replyParentName = StringUtils.getString(R.string.YOU);
            }
            helper.setVisible(R.id.reply, true);
            helper.setText(R.id.replyTime, LibAppUtils.getTimeDataToString(item.replyCreateDate, "MM-dd HH:mm"));
            replyContent.setTextMulti(TextUtils.concat(StringUtils.getString(R.string.has_replyed), MultiStyleTextView.FORMAT_START, replyParentName,
                    MultiStyleTextView.FORMAT_END, ":", item.replyContent).toString());
        } else if (TextUtils.equals(NeighborNoticeBean.TYPE_FAVOUR, item.type)) {
            helper.setVisible(R.id.reply, false);
            helper.setText(R.id.replyTime, LibAppUtils.getTimeDataToString(item.favourCreateDate, "MM-dd HH:mm"));
            replyContent.setTextMulti(TextUtils.concat(StringUtils.getString(R.string.has_favoured),
                    MultiStyleTextView.FORMAT_START,
                    StringUtils.getString(R.string.YOU),
                    MultiStyleTextView.FORMAT_END).toString());
        }

        helper.setText(R.id.replyName, item.replyMemberName);
        PicassoImageLoader.getMyPicasso()
                .load(LibAppUtils.getImg(item.replyMemberHeadImage))
                .placeholder(R.mipmap.mrtx).error(R.mipmap.mrtx)
                .into(helper.<ImageView>getView(R.id.replyHeadImage));
        /**
         * 版主信息
         */
        helper.setText(R.id.boardTime, LibAppUtils.getTimeDataToString(item.neighborBoardCreateTime, "MM-dd HH:mm"));
        helper.setText(R.id.boardContent, item.neighborBoardContent);

        helper.setText(R.id.boardName, item.neighborBoardMemberName);
        PicassoImageLoader.getMyPicasso()
                .load(LibAppUtils.getImg(item.neighborBoardMemberHeadImage))
                .placeholder(R.mipmap.mrtx).error(R.mipmap.mrtx)
                .into(helper.<ImageView>getView(R.id.boardHeadImage));
        String boardImage = item.neighborBoardImages;
        ImageView boardImageView = helper.getView(R.id.boardImage);
        if (TextUtils.isEmpty(boardImage)) {
            boardImageView.setVisibility(View.GONE);
        } else {
            String[] img = boardImage.split(",");
            PicassoImageLoader.getMyPicasso().load(img[0]).into(boardImageView);
            boardImageView.setVisibility(View.VISIBLE);
        }

        /**
         * 回复
         */
        helper.setTag(R.id.reply, item);
        helper.setOnClickListener(R.id.reply, onClickListener);

        helper.setTag(R.id.neighborNotice, item);
        helper.setOnClickListener(R.id.neighborNotice, onClickListener);

    }
}
