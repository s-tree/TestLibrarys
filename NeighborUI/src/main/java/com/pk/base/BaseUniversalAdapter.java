package com.pk.base;

import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.pk.base.entity.MultiItemEntity;
import com.pk.base.entity.UniversalEntity;

import java.util.List;

/**
 * Base万能适配器
 *
 * @author HJK
 */
public abstract class BaseUniversalAdapter<T extends UniversalEntity, K extends BaseViewHolder> extends BaseQuickAdapter<T, K> {
    /**
     * layouts indexed with their types
     */
    private SparseArray<Integer> layouts;
    protected static final int SECTION_HEADER_VIEW = 0x00000444;
    private static final int DEFAULT_VIEW_TYPE = -0xff;
    protected int mSectionHeadResId;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public BaseUniversalAdapter(int sectionHeadResId, List<T> data) {
        super(data);
        this.mSectionHeadResId = sectionHeadResId;
    }

    @Override
    protected int getDefItemViewType(int position) {
        if (mData.get(position).isHeader) {
            return SECTION_HEADER_VIEW;
        } else {
            Object item = mData.get(position);
            if (item instanceof MultiItemEntity) {
                return ((MultiItemEntity) item).getItemType();
            }
            return DEFAULT_VIEW_TYPE;
        }
    }

    @Override
    protected K onCreateDefViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SECTION_HEADER_VIEW)
            return createBaseViewHolder(getItemView(mSectionHeadResId, parent));
        return createBaseViewHolder(parent, getLayoutId(viewType));
    }

    @Override
    public void onBindViewHolder(K holder, int positions) {
        switch (holder.getItemViewType()) {
            case SECTION_HEADER_VIEW:
                setFullSpan(holder);
                convertHead(holder, mData.get(holder.getLayoutPosition() - getHeaderLayoutCount()));
                break;
            default:
                super.onBindViewHolder(holder, positions);
                break;
        }
    }

    protected void addItemType(int type, @LayoutRes int layoutResId) {
        if (layouts == null) {
            layouts = new SparseArray<>();
        }
        layouts.put(type, layoutResId);
    }

    private int getLayoutId(int viewType) {
        return layouts.get(viewType);
    }

    /**
     * 头
     *
     * @param helper
     * @param item
     */
    protected abstract void convertHead(BaseViewHolder helper, T item);
}
