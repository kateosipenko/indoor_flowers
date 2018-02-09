package com.indoor.flowers.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.indoor.flowers.R;
import com.indoor.flowers.model.Flower;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FlowersAdapter extends RecyclerView.Adapter<FlowersAdapter.ViewHolder> {

    private List<Flower> flowers = new ArrayList<>();

    public void setFlowers(List<Flower> items) {
        this.flowers.clear();
        if (items != null) {
            this.flowers.addAll(items);
        }

        notifyDataSetChanged();
    }

    public void clear() {
        this.flowers.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_flower, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.update(getItemByPosition(position));
    }

    @Override
    public int getItemCount() {
        return flowers.size();
    }

    private Flower getItemByPosition(int position) {
        return position >= 0 && position < flowers.size() ? flowers.get(position) : null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rf_icon)
        ImageView iconView;
        @BindView(R.id.rf_title)
        TextView nameView;
        @BindView(R.id.rf_room)
        TextView roomView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void update(Flower flower) {
            if (flower == null) {
                iconView.setImageBitmap(null);
                nameView.setText(null);
                roomView.setText(null);
            } else {
                if (!TextUtils.isEmpty(flower.getImagePath())) {
                    Picasso.with(itemView.getContext())
                            .load(new File(flower.getImagePath()))
                            .into(iconView);
                } else {
                    iconView.setImageBitmap(null);
                }
                nameView.setText(flower.getName());
            }
        }
    }
}
