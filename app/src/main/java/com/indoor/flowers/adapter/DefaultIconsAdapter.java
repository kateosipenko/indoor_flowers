package com.indoor.flowers.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.indoor.flowers.R;
import com.indoor.flowers.util.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DefaultIconsAdapter extends RecyclerView.Adapter<DefaultIconsAdapter.ViewHolder> {

    private OnItemClickListener<Integer> callback;
    private List<Integer> iconsRes = new ArrayList<>();

    public void setCallback(OnItemClickListener<Integer> callback) {
        this.callback = callback;
    }

    public void setIcons(List<Integer> icons) {
        this.iconsRes.clear();
        this.iconsRes.addAll(icons);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_group_icon, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.update(this.iconsRes.get(position));
    }

    @Override
    public int getItemCount() {
        return iconsRes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rri_icon)
        ImageView iconView;

        private DefaultIconsAdapter adapter;

        public ViewHolder(View itemView, DefaultIconsAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.adapter = adapter;
        }

        @OnClick(R.id.rri_icon)
        void onIconClicked() {
            if (adapter.callback != null) {
                adapter.callback.onItemClicked(adapter.iconsRes.get(getAdapterPosition()));
            }
        }

        private void update(int resId) {
            iconView.setImageResource(resId);
        }
    }
}
