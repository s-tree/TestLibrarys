package com.jingxi.smartlife.pad.sdk.neighbor.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.adapter.LeftNeighborhoodAdapter;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborDetailDo;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborInfoBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.PersonalInfo;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs.LibTipDialog;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.fragments.NeighborDetialFragment;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.ipresenters.ILeftNeighborBoardPresenter;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.iviews.ILeftNeighborView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.IJump;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.presenters.LeftNeighborBoardpresenter;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.LibAppUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.NeighborhoodUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.StringUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.ToastUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.RoundImageView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.ninegrid.PicassoImageLoader;
import com.pk.base.BaseQuickAdapter;

import java.util.List;

/**
 * Created by lb on 2017/11/10.
 */

public class NeighborLeftView extends RelativeLayout implements ILeftNeighborView, View.OnClickListener, IJump, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    private RoundImageView headImage;
    private TextView showName;
    private TextView statuCount;
    private TextView favourCount;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private LeftNeighborhoodAdapter adapter;
    private ILeftNeighborBoardPresenter neighborBoadDetailPresenter;
    private View noMoreView;
    private View loadMoreFaildView;
    private NeighborDetialFragment neighBorDetialFragment;
    private TextView tv_loadHttpFailed;
    private LinearLayoutManager linearLayoutManager;
    private NeighborInfoBean selectBean;
    private View noPublishView;
    private LibTipDialog deleteNeighborDialog;

    public NeighborLeftView(Context context) {
        this(context, null);
    }

    public NeighborLeftView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NeighborLeftView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initView(NeighborDetialFragment neighBorDetialFragment) {
        this.neighBorDetialFragment = neighBorDetialFragment;
        if (neighborBoadDetailPresenter == null) {
            neighborBoadDetailPresenter = new LeftNeighborBoardpresenter(this);
        }
        tv_loadHttpFailed = (TextView) findViewById(R.id.tv_loadHttpFailed);
        tv_loadHttpFailed.setOnClickListener(this);
        headImage = (RoundImageView) findViewById(R.id.headImage);
        showName = (TextView) findViewById(R.id.showName);
        statuCount = (TextView) findViewById(R.id.tv_movementCount);
        favourCount = (TextView) findViewById(R.id.tv_favourCount);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.neighbor_detail_swipeRefreshLayout);
        refreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        recyclerView = (RecyclerView) findViewById(R.id.neighbor_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void setHeadData(PersonalInfo personalInfo) {
        PicassoImageLoader.getMyPicasso()
                .load(LibAppUtils.getImg(personalInfo.getHeadImage()))
                .error(R.mipmap.mrtx)
                .config(Bitmap.Config.RGB_565)
                .resize(150, 150)
                .into(headImage);
        showName.setText(personalInfo.getNickName());
        statuCount.setText(String.valueOf(personalInfo.getNeighborBoardCount()));
        favourCount.setText(String.valueOf(personalInfo.getNeighborBoardFavouredCount()));
    }

    @Override
    public void setLeftListData(List<NeighborInfoBean> infoBeanList, String bz, int detailtype) {
        tv_loadHttpFailed.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);
        boolean showArrow = detailtype != 1;
        if (adapter == null) {
            adapter = new LeftNeighborhoodAdapter(R.layout.item_left_statu, infoBeanList, this, this);
            adapter.openLoadAnimation();
            adapter.openLoadMore(NeighborhoodUtil.PAGESIZE);
            adapter.setOnLoadMoreListener(this);
            adapter.setShowArrow(showArrow);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.removeAllFooterView();
            adapter.setShowArrow(showArrow);
            adapter.setNewData(infoBeanList);
            adapter.openLoadMore(NeighborhoodUtil.PAGESIZE);
        }
        if (infoBeanList.size() == 0) {
            if (noPublishView == null) {
                noPublishView = inflate(getContext(), R.layout.neighbor_no_publish, null);
            }
            if (detailtype == -1 || detailtype == 0 || detailtype == 1) {
                ((TextView) noPublishView.findViewById(R.id.tv_description)).setText(R.string.no_published);
            } else if (detailtype == 2) {
                ((TextView) noPublishView.findViewById(R.id.tv_description)).setText(R.string.no_favoured_other);
            } else if (detailtype == 3) {
                ((TextView) noPublishView.findViewById(R.id.tv_description)).setText(R.string.no_joined_activity);
            }
            adapter.loadComplete();
            adapter.addFooterView(noPublishView);
        } else if (infoBeanList.size() < NeighborhoodUtil.PAGESIZE) {
            if (noMoreView == null) {
                noMoreView = inflate(getContext(), R.layout.def_load_more_failed, null);
            }
            adapter.loadComplete();
            noMoreView.setOnClickListener(null);
            ((TextView) noMoreView.findViewById(R.id.tv_prompt)).setText(StringUtils.getString(R.string.no_more_data));
            adapter.addFooterView(noMoreView);
        }

        showFirstItemDetail(infoBeanList, detailtype);
    }

    /**
     * 我的社区新鲜事需要右侧进入后需要手动显示第一个条目
     *
     * @param infoBeanList
     * @param detailtype
     */
    private void showFirstItemDetail(List<NeighborInfoBean> infoBeanList, int detailtype) {
        if (detailtype != -1 && detailtype != 1 && infoBeanList != null) {
            if (neighborBoadDetailPresenter != null && neighborBoadDetailPresenter.isNeedShowFirstItem()) {
                neighborBoadDetailPresenter.setNeedShowFirstItem(false);
                NeighborDetailDo neighborDetailDo = new NeighborDetailDo();
                neighborDetailDo.type = detailtype == 1 ? NeighborDetailDo.TYPE_MY_MESSAGE : NeighborDetailDo.TYPE_DETAIL;
                neighborDetailDo.neighborInfoBean = infoBeanList.size() > 0 ? infoBeanList.get(0) : null;
                send2DetailFragment(neighborDetailDo);
                if (neighborDetailDo.neighborInfoBean != null) {
                    adapter.setSelectItemId(neighborDetailDo.neighborInfoBean.neighborBoardId);
                }
                if (infoBeanList.size() > 0) {
                    recyclerView.scrollToPosition(0);
                }
            }
        }
    }

    @Override
    public void updateList(List<NeighborInfoBean> infoBeanList) {
        if (adapter != null) {
            adapter.removeAllFooterView();
            adapter.setNewData(infoBeanList);
        }
    }

    /**
     * @param index
     * @param type  6:刷新
     */
    @Override
    public void notifyItemChanged(int index, int type) {
        if (adapter != null) {
            if (type != 0) {
                adapter.notifyItemChanged(index);
            } else if (type != 6) {
                adapter.notifyItemRemoved(index);
                if (adapter.getData().size() == 0) {
                    onRefresh();
                }
            }
        }
    }


    @Override
    public void loadMore(List<NeighborInfoBean> content, boolean hasMore) {
        if (adapter != null) {
            adapter.addData(content);
            if (hasMore) {

            } else {
                if (noMoreView == null) {
                    noMoreView = inflate(getContext(), R.layout.def_load_more_failed, null);
                }
                adapter.loadComplete();
                ((TextView) noMoreView.findViewById(R.id.tv_prompt)).setText(StringUtils.getString(R.string.no_more_data));
                noMoreView.setOnClickListener(null);
                adapter.addFooterView(noMoreView);
            }
        }
    }

    @Override
    public void onRefreshErro(String errorNo) {
        refreshLayout.setRefreshing(false);
        if (!TextUtils.equals(errorNo, String.valueOf(LeftNeighborBoardpresenter.IS_LOADING))) {
            tv_loadHttpFailed.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            ToastUtil.showToast(errorNo);
        }
    }

    @Override
    public void showDeleteDialog(boolean isShow) {
        if (isShow) {
            if (deleteNeighborDialog == null) {
                deleteNeighborDialog = neighborBoadDetailPresenter.createDeleteDialog();
            }
            if (!deleteNeighborDialog.isShowing()) {
                deleteNeighborDialog.show();
            }
        } else if (deleteNeighborDialog != null && deleteNeighborDialog.isShowing()) {
            deleteNeighborDialog.dismiss();
        }
    }

    @Override
    public Context getTheContext() {
        return getContext();
    }

    @Override
    public void showLoadingDialog(boolean isShow) {
    }

    @Override
    public void send2DetailFragment(NeighborDetailDo neighborDetailDo) {
        if (neighBorDetialFragment != null) {
            neighBorDetialFragment.handleEvent(neighborDetailDo);
        }
    }

    @Override
    public void setFrefreshing() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.left_favourite) {
            NeighborInfoBean neighborInfoBean = (NeighborInfoBean) v.getTag();
            NeighborDetailDo neighborDetailDo = new NeighborDetailDo();
            neighborDetailDo.type = NeighborDetailDo.TYPE_FAVOUR;
            neighborDetailDo.neighborInfoBean = neighborInfoBean;
            send2DetailFragment(neighborDetailDo);
        } else if (v.getId() == R.id.details_cv) {
            /**
             * 点击社区新鲜事条目
             */
            NeighborInfoBean neighborInfoBean = (NeighborInfoBean) v.getTag();
            NeighborDetailDo neighborDetailDo = new NeighborDetailDo();
            neighborDetailDo.neighborInfoBean = neighborInfoBean;
            if (neighborBoadDetailPresenter != null && neighborBoadDetailPresenter.getDetailType() == 1) {
                neighborDetailDo.type = NeighborDetailDo.TYPE_MY_MESSAGE;
                selectBean = neighborInfoBean;
            } else {
                neighborDetailDo.type = NeighborDetailDo.TYPE_DETAIL;
            }
            adapter.setSelectItemId(neighborInfoBean.neighborBoardId);
            send2DetailFragment(neighborDetailDo);
        } else if (v.getId() == R.id.delete_item) {
            /**
             * 删除社区新鲜事条目
             */
            neighborBoadDetailPresenter.deleteItem((NeighborInfoBean) v.getTag());
        } else if (v.getId() == R.id.tv_loadHttpFailed) {
            onRefresh();
        }
    }

    @Override
    public String jump(Activity context, Uri uri, Bundle bundle) {
//        return JumpCenter.jump(context, uri, bundle);
        return "";
    }

    public ILeftNeighborBoardPresenter getleftPresenter() {
        return neighborBoadDetailPresenter;
    }

    @Override
    public void onRefresh() {
        if (adapter != null) {
            adapter.removeAllFooterView();
            adapter.loadComplete();
        }
        if (neighborBoadDetailPresenter != null) {
            neighborBoadDetailPresenter.OnRefresh();
        }

    }

    @Override
    public void onLoadMoreRequested() {
        if (neighborBoadDetailPresenter != null) {
            neighborBoadDetailPresenter.loadMore();
        }
    }

    public void onDestroy() {
        if (neighBorDetialFragment != null) {
            neighBorDetialFragment = null;
        }
        if (neighborBoadDetailPresenter != null) {
            neighborBoadDetailPresenter.onDestroy();
            neighborBoadDetailPresenter = null;
        }
    }

    @Override
    public void loadMoreFaild(String erro) {
        if (adapter != null) {
            if (loadMoreFaildView == null) {
                loadMoreFaildView = inflate(getContext(), R.layout.def_load_more_failed, null);
                ((TextView) loadMoreFaildView.findViewById(R.id.tv_prompt)).setText(StringUtils.getString(R.string.loadFaildwithCode,erro));
                adapter.setLoadMoreFailedView(loadMoreFaildView);
            }
            adapter.showLoadMoreFailedView(this);
        }
    }

    @Override
    public void setItemArrow(NeighborInfoBean infoBean) {
        if (infoBean != null) {
            adapter.setSelectItemId(infoBean.neighborBoardId);
        }
    }

    @Override
    public void showArrow(boolean show) {
        if (adapter != null) {
            adapter.setShowArrow(show);
        }
    }

    @Override
    public void selectFirstVisibleItem() {
        /**
         * 从我的消息切换到我发布的社区新鲜事，刷新可见的第一条条目
         */
        if (adapter != null) {
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            NeighborInfoBean infoBean = adapter.getData().size() > firstVisibleItemPosition ? adapter.getData().get(firstVisibleItemPosition) : null;
            NeighborDetailDo neighborDetailDo = new NeighborDetailDo();
            neighborDetailDo.type = NeighborDetailDo.TYPE_DETAIL;
            if (infoBean != null) {
                /**
                 * 点击社区新鲜事条目
                 */
                if (selectBean == null) {
                    neighborDetailDo.neighborInfoBean = infoBean;
                } else {
                    neighborDetailDo.neighborInfoBean = selectBean;
                }
                adapter.setSelectItemId(neighborDetailDo.neighborInfoBean.neighborBoardId);
            }
            send2DetailFragment(neighborDetailDo);
        }
        selectBean = null;
    }

    @Override
    public void startListen(Runnable runnable, boolean add) {
        if (add) {
            postDelayed(runnable, 3000);
        } else {
            removeCallbacks(runnable);
        }
    }
}
