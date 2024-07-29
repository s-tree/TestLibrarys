package com.jingxi.lib.paging.source;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava2.RxPagingSource;

import com.jingxi.lib.paging.exception.PagingException;
import com.jingxi.lib.paging.rx.WithObjFunction;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public abstract class BasePagingSource<T> extends RxPagingSource<Integer,T> {
    public BasePagingSource() {
    }

    public abstract Observable<List<T>> getData(Integer pageIndex);

    @NonNull
    @Override
    public Single<LoadResult<Integer, T>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        Integer pageIndex = loadParams.getKey();
        if (pageIndex == null) {
            pageIndex = 1;
        }
        return Single.just(pageIndex)
                .flatMap(new Function<Integer, Single<List<T>>>() {
                    @Override
                    public Single<List<T>> apply(@NonNull Integer integer) throws Exception {
                        return getData(integer)
                                .first(new ArrayList<T>());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(new WithObjFunction<List<T>, LoadResult<Integer, T>,Integer>(pageIndex) {
                    @Override
                    public LoadResult<Integer, T> apply(@io.reactivex.annotations.NonNull List<T> list) throws Exception {
                        if(list.isEmpty() && obj != 1){
                            return new LoadResult.Error<>(new PagingException.NoMoreDataException());
                        }
                        return new LoadResult.Page<Integer, T>(
                                list,
                                null, // Only paging forward.
                                obj + 1,
                                LoadResult.Page.COUNT_UNDEFINED,
                                LoadResult.Page.COUNT_UNDEFINED
                        );
                    }
                });
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, T> pagingState) {
        return 1;
    }
}
