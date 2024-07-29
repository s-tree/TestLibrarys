package com.jingxi.lib.paging.source;

import java.util.List;

import io.reactivex.Observable;

public abstract class BaseCachePagingSource<T> extends BasePagingSource<T> {
    private final int STATE_INIT = -1;
    private final int STATE_NONE = 0;
    int sourceState = STATE_INIT;

    public BaseCachePagingSource() {
    }

    public abstract Observable<List<T>> getCacheData(Integer pageIndex);

    public abstract Observable<List<T>> getNetData(Integer pageIndex);

    @Override
    public Observable<List<T>> getData(Integer pageIndex) {
        if(sourceState == STATE_INIT){
            sourceState = STATE_NONE;
            return getCacheData(pageIndex);
        }else{
            return getNetData(pageIndex);
        }
    }
}
