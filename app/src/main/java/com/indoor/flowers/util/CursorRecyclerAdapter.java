package com.indoor.flowers.util;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

public abstract class CursorRecyclerAdapter<VH extends RecyclerView.ViewHolder> extends
        com.evgeniysharafan.utils.recycler.CursorRecyclerAdapter<VH> {

    protected LayoutInflater inflater;

    public CursorRecyclerAdapter() {
        super(null);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        inflater = LayoutInflater.from(recyclerView.getContext());
    }

    @Override
    @Nullable
    public Object getItem(int position) {
        return position >= 0 && position < getItemCount() ? super.getItem(position) : null;
    }
}
