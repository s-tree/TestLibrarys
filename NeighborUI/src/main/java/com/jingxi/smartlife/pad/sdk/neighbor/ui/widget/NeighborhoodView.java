package com.jingxi.smartlife.pad.sdk.neighbor.ui.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.adapter.SocialAdapter;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborInfoBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborhoodDo;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.ResponseListObserver;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.LibAppUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.ToastUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.SimpleSwipeRefreshLayout;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.Bus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/10.
 */

public class NeighborhoodView extends RelativeLayout
        implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private RecyclerView recyclerView;
    private SocialAdapter socialAdapter;
    private List<NeighborInfoBean> beanList;
    private StaggeredGridLayoutManager layoutManager;
    private String typeName;
    private String typeId;
    private View noContent;
    private SimpleSwipeRefreshLayout swipe_refresh;

    public NeighborhoodView(Context context) {
        super(context);
    }

    public NeighborhoodView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init() {
        swipe_refresh = (SimpleSwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(this);
        swipe_refresh.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipe_refresh.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        noContent = findViewById(R.id.noContent);
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MyCustomItemDecoration((int) (getResources().getDimension(R.dimen.dp_10)),
                (int) (getResources().getDimension(R.dimen.dp_10)),
                (int) (getResources().getDimension(R.dimen.dp_8)),
                (int) (getResources().getDimension(R.dimen.dp_8))));
        ((DefaultItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        loadNeighborList();
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            int lastPosition = -1;
            if (newState == RecyclerView.SCROLL_STATE_IDLE && !swipe_refresh.isRefreshing() && swipe_refresh.isUp()) {
                int[] lastPositions = new int[layoutManager.getSpanCount()];
                layoutManager.findLastVisibleItemPositions(lastPositions);
                lastPosition = LibAppUtils.getMax(lastPositions);
                if (lastPosition == layoutManager.getItemCount() - 1) {
                    loadMore();
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    public void notifyChanged() {
        if (socialAdapter != null) {
            socialAdapter.notifyDataSetChanged();
        }
    }

    public SocialAdapter getAdapter() {
        return socialAdapter;
    }

    public void setType(String typeName, String typeId) {
        this.typeName = typeName;
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getTypeId() {
        return typeId;
    }

    @Override
    public void onClick(View v) {
        NeighborhoodDo neighborhoodDo = new NeighborhoodDo();
        neighborhoodDo.typeID = typeId;
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(v.getTag()));
        if (v.getId() == R.id.details_cv) {
            //点击单条社区新鲜事
            neighborhoodDo.jsonObject = jsonObject;
            neighborhoodDo.type = NeighborhoodDo.TYPE_DETAIL;
        } else if (v.getId() == R.id.favourite) {
            neighborhoodDo.jsonObject = jsonObject;
            neighborhoodDo.type = NeighborhoodDo.TYPE_FAVOUR;
        } else if (v.getId() == R.id.delete_item) {
            //删除社区新鲜事条目
            neighborhoodDo.jsonObject = jsonObject;
            neighborhoodDo.type = NeighborhoodDo.TYPE_DELETE;
        } else if (v.getId() == R.id.fab_addlinliq) {
            //发社区新鲜事
            neighborhoodDo.type = NeighborhoodDo.TYPE_REPORT;
        }
        Bus.getDefault().post(neighborhoodDo);
    }

    /**
     * @param jsonObject
     * @param type       0 : 删除 ; 1 :点赞 ; 2 : 评论数 ; 3 : 新增 ;4 : 评论和点赞
     */
    public void updateItem(JSONObject jsonObject, final int type) {
        if (type == 3) {
            onRefresh();
            return;
        }
        Observable.just(jsonObject)
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .map(new Function<JSONObject, Integer>() {
                    @Override
                    public Integer apply(JSONObject jsonObject) {
                        NeighborInfoBean temp = NeighborInfoBean.getEmpty(jsonObject.getString("neighborBoardId"));
                        int index = beanList.indexOf(temp);
                        if(index == -1){
                            return index;
                        }
                        NeighborInfoBean infoBean = beanList.get(index);
                        if (type == 0) {
                            beanList.remove(infoBean);
                        } else if (type == 1) {
                            infoBean.isFavour = Boolean.valueOf(jsonObject.getString("isFavour"));
                            infoBean.favourCounts = Integer.parseInt(jsonObject.getString("favourCounts"));
                        } else if (type == 2) {
                            infoBean.replyCounts = Integer.parseInt(jsonObject.getString("replyCounts"));
                        } else if (type == 4) {
                            infoBean.isFavour = Boolean.valueOf(jsonObject.getString("isFavour"));
                            infoBean.favourCounts = Integer.parseInt(jsonObject.getString("favourCounts"));
                            infoBean.replyCounts = Integer.parseInt(jsonObject.getString("replyCounts"));
                        }
                        return index;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        if (integer != -1) {
                            if (type == 0) {
                                socialAdapter.notifyItemRemoved(integer);
                                willLoadMore();
                            } else {
                                socialAdapter.notifyItemChanged(integer);
                            }
                        }
                    }
                });
    }

    //-------------------------------------------获取社区新鲜事 ----------------------------------------
    boolean isAfterDelete = false;
    boolean isRequest = false;
    private int pageIndex = 1;

    @Override
    public void onRefresh() {
        pageIndex = 1;
        if (socialAdapter != null || socialAdapter.getItemCount() >= 1) {
            recyclerView.scrollToPosition(0);
            swipe_refresh.setRefreshing(false);
        }
        loadNeighborListHttp();
    }

    public void willLoadMore() {

        if (!recyclerView.canScrollVertically(RecyclerView.VERTICAL)) {
            isAfterDelete = true;
            loadMore();
        }
    }

    public void loadMore() {
        if (isRequest) {
            return;
        }
        if (beanList.size() == 0) {
            pageIndex = 1;
        }
        socialAdapter.showFooter(true);
        loadNeighborListHttp();
    }

    private void loadNeighborList() {
//        if (TextUtils.equals(typeId, "all")) {
//            Object[] result = NeighborhoodUtil.getDefaultNeighborList();
//            if (result[0] == null) {
//                beanList = new ArrayList<>();
//            } else {
//                beanList = JSON.parseArray(((JSONArray) result[0]).toJSONString(),NeighborInfoBean.class);
//            }
//            socialAdapter = new SocialAdapter(beanList,
//                    this,
//                    false);
//        } else {
            beanList = new ArrayList<>();
            socialAdapter = new SocialAdapter(beanList,
                    this,
                    false);
//        }
        recyclerView.setAdapter(socialAdapter);
        swipe_refresh.setRefreshing(true);
        loadNeighborListHttp();
    }

    private void loadNeighborListHttp() {
        if (isRequest) {
            swipe_refresh.setRefreshing(false);
            socialAdapter.showFooter(false);
            return;
        }
        isRequest = true;
        JXPadSdk.getNeighborManager().getNeighborBoardList(
                TextUtils.equals(typeId, "all") ? "" : typeId, String.valueOf(pageIndex), "")
                .subscribe(mNeighborhoodAction);
    }

    private ResponseListObserver<NeighborInfoBean> mNeighborhoodAction = new ResponseListObserver<NeighborInfoBean>() {
        @Override
        public void onResponse(List<NeighborInfoBean> tempInfoBean) {
            isRequest = false;
            swipe_refresh.setRefreshing(false);
            socialAdapter.showFooter(false);
            if (tempInfoBean == null || tempInfoBean.isEmpty()) {
                if (pageIndex != 1) {
//                    if (((NeighborhoodActivity) getContext()).newNeighFragment.isVisible()) {
//                        if (!isAfterDelete) {
//                            ToastUtil.showToast(StringUtils.getString(R.string.no_more_data));
//                        }
//                    }
                    if (beanList.isEmpty()) {
                        /**
                         * 切换为缺省页
                         */
                        noContent.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    socialAdapter.setIsLoadOld(true);
                    if ((!TextUtils.equals(typeId, "all"))) {
                        beanList.clear();
                        socialAdapter.notifyDataSetChanged();
                        /**
                         * 切换为缺省页
                         */
                        noContent.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        beanList.clear();
                        socialAdapter.notifyDataSetChanged();
                        /**
                         * 显示预置数据
                         */
                        noContent.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
                isAfterDelete = false;
                isRequest = false;
                return;
            }
            Observable.just(tempInfoBean)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap(new Function<List<NeighborInfoBean>, ObservableSource<List<NeighborInfoBean>>>() {
                        @Override
                        public Observable<List<NeighborInfoBean>> apply(List<NeighborInfoBean> httpTempList) {
                            if (pageIndex == 1) {
                                return Observable.just(httpTempList);
                            }
                            List<NeighborInfoBean> tempList = new ArrayList<NeighborInfoBean>();
                            for (NeighborInfoBean httpBean : httpTempList) {
                                for (NeighborInfoBean bean2 : beanList) {
                                    if (!TextUtils.equals(httpBean.neighborBoardId, bean2.neighborBoardId)) {
                                        tempList.add(httpBean);
                                        break;
                                    }
                                }
                            }
                            return Observable.just(tempList);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NotifySubscriber());
        }

        @Override
        public void onFaild(String message) {
            isRequest = false;
            swipe_refresh.setRefreshing(false);
            socialAdapter.showFooter(false);
            ToastUtil.showToast(message);
        }
    };

    private class NotifySubscriber implements Observer<List<NeighborInfoBean>> {

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(List<NeighborInfoBean> beans) {
            if (pageIndex == 1) {
                beanList.clear();
            }
            if (pageIndex == 1 && beans.isEmpty()) {
                //切换为缺省页
//                noContent.setVisibility(View.VISIBLE);
//                recyclerView.setVisibility(View.GONE);
            } else {
                noContent.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            beanList.addAll(beans);
            socialAdapter.setIsLoadOld(false);
            socialAdapter.notifyDataSetChanged();
            if (beans.size() > 0) {
                pageIndex += 1;
            }
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    }

    //----------------------------------------------------------------------------------------------
}
