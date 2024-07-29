package com.jingxi.lib.paging.rx;

import io.reactivex.functions.Function;

public abstract class WithObjFunction<T,K,R> implements Function<T, K> {
    public R obj;

    public WithObjFunction(R obj) {
        this.obj = obj;
    }
}
