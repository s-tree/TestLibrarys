package com.jingxi.lib.paging;

import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.lifecycle.Lifecycle;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingDataAdapter;
import androidx.paging.PagingSource;
import androidx.paging.rxjava2.PagingRx;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jingxi.lib.paging.adapter.PagingLoadAdapter;
import com.jingxi.lib.paging.holder.PagingHolderConfig;
import com.jingxi.lib.paging.source.BaseCachePagingSource;
import com.jingxi.lib.paging.source.BasePagingSource;
import com.jingxi.lib.paging.util.FieldUtil;

import java.lang.ref.WeakReference;

import io.reactivex.functions.Consumer;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

/**
 * Paging3 分页加载封装
 * 仅需从同一个数据源加载数据使用 {@link com.jingxi.lib.paging.source.BasePagingSource}
 * 需要先从本地加载第一页数据使用 {@link com.jingxi.lib.paging.source.BaseCachePagingSource}
 */
public class PagingPlugin implements SwipeRefreshLayout.OnRefreshListener,Function1<CombinedLoadStates, Unit> {
    public static PagingHolderConfig holderConfig = new PagingHolderConfig();

    public static void setHolderConfig(PagingHolderConfig holderConfig) {
        PagingPlugin.holderConfig = holderConfig;
    }

    private View noDataView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private PagingDataAdapter adapter;
    private Lifecycle lifecycle;
    private int pageSize;
    private BasePagingSource basePagingSource;
    private Class<? extends PagingSource> sourceClass;
    boolean isCacheSource = false;
    boolean needLoadView = true;
    int loadViewRes = -1;

    private PagingPlugin() {
    }

    public <T> void bind(){
        if(recyclerView == null){
            return;
        }

        if(needLoadView){
            recyclerView.setAdapter(
                    adapter.withLoadStateFooter(
                            new PagingLoadAdapter(loadViewRes)
                    )
            );
        }else{
            recyclerView.setAdapter(adapter);
        }

        if(basePagingSource != null && basePagingSource instanceof BaseCachePagingSource){
            isCacheSource = true;
        }
        else if(sourceClass != null && BaseCachePagingSource.class.isAssignableFrom(sourceClass)){
            try {
                basePagingSource = (BasePagingSource) sourceClass.newInstance();
                isCacheSource = true;
                sourceClass = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Pager<Integer,T> pager = new Pager<Integer,T>(
                new PagingConfig(pageSize),
                new Function0<PagingSource<Integer, T>>() {
                    @Override
                    public PagingSource<Integer, T> invoke() {
                        if(sourceClass != null){
                            try{
                                return sourceClass.newInstance();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        return basePagingSource = FieldUtil.cloneNew(basePagingSource);
                    }
                }
        );

        PagingRx.getFlowable(pager)
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                })
                .subscribe(new Consumer<PagingData<T>>() {
                    @Override
                    public void accept(PagingData<T> tPagingData) throws Exception {
                        PagingPlugin.this.adapter.submitData(PagingPlugin.this.lifecycle,tPagingData);
                    }
                });
        if(refreshLayout != null || noDataView != null){
            adapter.addLoadStateListener(this);
        }
        if(refreshLayout != null){
            refreshLayout.setOnRefreshListener(this);
        }
    }

    public void updateSource(BasePagingSource source){
        this.basePagingSource = source;
        onRefresh();
    }

    public void destroy(){
        if(refreshLayout != null){
            refreshLayout.setOnRefreshListener(null);
        }
        adapter.removeLoadStateListener(this);
        noDataView = null;
        recyclerView = null;
        refreshLayout = null;
        adapter = null;
        basePagingSource = null;
        sourceClass = null;
    }

    @Override
    public void onRefresh() {
        adapter.refresh();
    }

    boolean isRefreshing = false;
    @Override
    public Unit invoke(CombinedLoadStates states) {
        if(isCacheSource && !(states.getRefresh() instanceof LoadState.Loading)){
            isCacheSource = false;
            onRefresh();
            return null;
        }
        if(refreshLayout != null){
            if(states.getRefresh() instanceof LoadState.NotLoading || states.getRefresh() instanceof LoadState.Error){
                refreshLayout.setRefreshing(false);
            }
        }
        if(noDataView != null){
            boolean isLoadComplete = states.getPrepend() instanceof LoadState.NotLoading;
            isLoadComplete &= states.getRefresh() instanceof LoadState.NotLoading;
            isLoadComplete &= states.getAppend() instanceof LoadState.NotLoading;

            if(isLoadComplete){
                checkNoData();
            }
        }

        if(adapter.getItemCount() == 0 && states.getRefresh() instanceof LoadState.Loading){
            /**
             * 有一个bug，adapter 当前无数据，刷新后有数据，recyclerView 不显示在第一列，会显示在尾部,需要单独处理
             */
            isRefreshing = true;
        }
        if(isRefreshing && states.getRefresh() instanceof LoadState.NotLoading){
            isRefreshing = false;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if(adapter.getItemCount() != 0 && layoutManager != null){
                if(layoutManager instanceof LinearLayoutManager){
                    ((LinearLayoutManager)layoutManager).scrollToPositionWithOffset(0,0);
                }
                else if(layoutManager instanceof StaggeredGridLayoutManager){
                    ((StaggeredGridLayoutManager)layoutManager).scrollToPositionWithOffset(0,0);
                }
            }
        }

        return null;
    }

    private void checkNoData(){
        if(adapter.getItemCount() == 0){
            noDataView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }
        noDataView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Source 中有需要用到的成员变量，则使用 source 传递完整对象
     * 没有成员变量，则使用 sourceClass 传递source class
     *
     * sample:
     * pagingPlugin = PagingPlugin.bind(
     *                 getLifecycle(),
     *                 PAGE_SIZE,
     *                 Paging3Source.class,
     *                 adapter,
     *                 recyclerView,
     *                 refreshLayout,
     *                 null
     * );
     * @param <T>
     */
    public static class Builder<T>{
        protected WeakReference<Lifecycle> lifecycle;
        protected int pageSize;
        protected WeakReference<BasePagingSource<T>> source;
        protected WeakReference<Class<? extends BasePagingSource<T>>> sourceClass;
        protected WeakReference<PagingDataAdapter<T,?>> adapter;
        protected WeakReference<RecyclerView> recyclerView;
        protected WeakReference<SwipeRefreshLayout> refreshLayout;
        protected WeakReference<View> noDataView;
        protected boolean needLoadView = true;
        protected WeakReference<Integer> loadViewRes;

        public Builder() {
        }

        public Builder<T> lifecycle(Lifecycle lifecycle) {
            this.lifecycle = new WeakReference<>(lifecycle);
            return this;
        }

        public Builder<T> pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder<T> source(BasePagingSource<T> source) {
            this.source = new WeakReference<>(source);
            return this;
        }

        public Builder<T> sourceClass(Class<? extends BasePagingSource<T>> sourceClass) {
            this.sourceClass = new WeakReference<Class<? extends BasePagingSource<T>>>(sourceClass);
            return this;
        }

        public Builder<T> adapter(PagingDataAdapter<T, ?> adapter) {
            this.adapter = new WeakReference<PagingDataAdapter<T, ?>>(adapter);
            return this;
        }

        public Builder<T> recyclerView(RecyclerView recyclerView) {
            this.recyclerView = new WeakReference<>(recyclerView);
            return this;
        }

        public Builder<T> refreshLayout(SwipeRefreshLayout refreshLayout) {
            this.refreshLayout = new WeakReference<>(refreshLayout);
            return this;
        }

        public Builder<T> noDataView(View noDataView) {
            this.noDataView = new WeakReference<>(noDataView);
            return this;
        }

        public Builder<T> needLoadView(boolean needLoadView) {
            this.needLoadView = needLoadView;
            return this;
        }

        public Builder<T> loadViewRes(@LayoutRes int loadViewRes) {
            this.loadViewRes = new WeakReference<>(loadViewRes);
            return this;
        }

        public PagingPlugin build(){
            PagingPlugin paging = new PagingPlugin();
            paging.recyclerView = recyclerView.get();
            paging.adapter = adapter.get();
            if(source != null){
                paging.basePagingSource = source.get();
            }
            if(sourceClass != null){
                paging.sourceClass = sourceClass.get();
            }
            if(refreshLayout != null){
                paging.refreshLayout = refreshLayout.get();
            }
            if(noDataView != null){
                paging.noDataView = noDataView.get();
            }
            if(lifecycle != null){
                paging.lifecycle = lifecycle.get();
            }
            if(loadViewRes != null){
                paging.loadViewRes = loadViewRes.get();
            }
            paging.needLoadView = needLoadView;
            paging.pageSize = pageSize;
            paging.bind();
            return paging;
        }
    }
}