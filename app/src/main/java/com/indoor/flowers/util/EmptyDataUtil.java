package com.indoor.flowers.util;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.view.View;

import java.security.InvalidParameterException;

public class EmptyDataUtil extends AdapterDataObserver {

    private RecyclerView recyclerView;
    private View emptyTextLayout;

    @Override
    public void onChanged() {
        super.onChanged();
        invalidateAdapterEmpty();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        super.onItemRangeChanged(positionStart, itemCount);
        invalidateAdapterEmpty();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        super.onItemRangeChanged(positionStart, itemCount, payload);
        invalidateAdapterEmpty();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        super.onItemRangeInserted(positionStart, itemCount);
        invalidateAdapterEmpty();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        super.onItemRangeRemoved(positionStart, itemCount);
        invalidateAdapterEmpty();
    }

    public void setEmptyTextLayout(View emptyTextLayout) {
        this.emptyTextLayout = emptyTextLayout;
    }

    public void attachToList(RecyclerView targetList) {
        this.recyclerView = targetList;
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            throw new InvalidParameterException("Need first set adapter into RecyclerView");
        }

        adapter.registerAdapterDataObserver(this);
        invalidateAdapterEmpty();
    }

    private void invalidateAdapterEmpty() {
        if (emptyTextLayout == null) {
            return;
        }

        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null || adapter.getItemCount() == 0) {
            emptyTextLayout.setVisibility(View.VISIBLE);
        } else {
            emptyTextLayout.setVisibility(View.GONE);
        }
    }
}
