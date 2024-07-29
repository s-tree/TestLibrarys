package com.jingxi.lib.paging.holder;

import com.jingxi.lib.paging.R;

public class PagingHolderConfig {

    public int loadStateLayoutRes = R.layout.layout_adapter_item_text;
    public Class<? extends BasePagingHolder> loadStateHolder = PagingLoadHolder.class;
}
