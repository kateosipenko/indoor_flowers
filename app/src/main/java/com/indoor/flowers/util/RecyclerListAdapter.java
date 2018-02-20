package com.indoor.flowers.util;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerListAdapter<T, E extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<E> {

    protected List<T> items = new ArrayList<>();
    protected OnItemClickListener<T> listener;

    public void clear() {
        this.items.clear();
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener<T> listener) {
        this.listener = listener;
    }

    public void setItems(List<T> items) {
        this.items.clear();
        if (items != null) {
            this.items.addAll(items);
        }

        notifyDataSetChanged();
    }

    public void addAll(List<T> items) {
        if (items != null) {
            this.items.addAll(items);
            notifyItemRangeInserted(this.items.size() - items.size(), items.size());
        }
    }

    public T getItemByPosition(int position) {
        return position >= 0 && position < items.size() ? items.get(position) : null;
    }

    @LayoutRes
    public abstract int getRowLayoutRes();

    public abstract E onCreateViewHolder(View view);

    @Override
    public E onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getRowLayoutRes(), parent, false);
        return onCreateViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

