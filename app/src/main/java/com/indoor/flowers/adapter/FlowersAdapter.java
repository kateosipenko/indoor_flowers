package com.indoor.flowers.adapter;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.indoor.flowers.R;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.util.RecyclerListAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class FlowersAdapter extends RecyclerListAdapter<Flower, FlowersAdapter.ViewHolder> {

    private boolean isSelectionMode = false;
    private FlowersSelectionListener selectionListener;
    private List<Flower> selectedFlowers = new ArrayList<>();

    public void setSelectionListener(FlowersSelectionListener listener) {
        this.selectionListener = listener;
    }

    public void setSelectionMode(boolean isSelectionMode) {
        this.isSelectionMode = isSelectionMode;
        notifyDataSetChanged();
    }

    public void setSelectedFlowers(List<Flower> selected) {
        this.selectedFlowers.clear();
        if (selected != null) {
            this.selectedFlowers.addAll(selected);
        }

        notifyDataSetChanged();
    }

    public List<Flower> getSelectedFlowers() {
        return selectedFlowers;
    }

    public void addSelected(long flowerId) {
        Flower selectedItem = null;
        for (Flower flower : items) {
            if (flower.getId() == flowerId) {
                selectedItem = flower;
                break;
            }
        }

        if (selectedItem != null && !selectedFlowers.contains(selectedItem)) {
            selectedFlowers.add(selectedItem);
            if (selectionListener != null) {
                selectionListener.onSelectedFlowersChanged(selectedFlowers);
            }
        }
    }

    @Override
    public int getRowLayoutRes() {
        return R.layout.row_flower;
    }

    @Override
    public ViewHolder onCreateViewHolder(View view) {
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.update(getItemByPosition(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rf_root)
        ConstraintLayout rootLayout;
        @BindView(R.id.rf_icon)
        ImageView iconView;
        @BindView(R.id.rf_title)
        TextView nameView;
        @BindView(R.id.rf_days_to_watering)
        TextView daysToWateringView;
        @BindView(R.id.rf_last_watering)
        TextView lastWateringView;
        @BindView(R.id.rf_watering_level)
        ProgressBar wateringLevel;
        @BindView(R.id.rf_check_box)
        CheckBox checkBox;

        ConstraintSet set = new ConstraintSet();

        private FlowersAdapter adapter;

        public ViewHolder(View itemView, FlowersAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.adapter = adapter;
        }

        @OnClick({R.id.rf_title, R.id.rf_icon, R.id.rf_days_to_watering, R.id.rf_last_watering,
                R.id.rf_root})
        void onFlowerClick(View view) {
            if (adapter.isSelectionMode) {
                checkBox.setChecked(!checkBox.isChecked());
            } else if (adapter.listener != null) {
                adapter.listener.onItemClicked(adapter.getItemByPosition(getAdapterPosition()));
            }
        }

        @OnCheckedChanged(R.id.rf_check_box)
        void onCheckChanged(CompoundButton button, boolean isChecked) {
            Flower flower = adapter.getItemByPosition(getAdapterPosition());
            if (isChecked && !adapter.selectedFlowers.contains(flower)) {
                adapter.selectedFlowers.add(flower);
                if (adapter.selectionListener != null) {
                    adapter.selectionListener.onSelectedFlowersChanged(adapter.selectedFlowers);
                }
            } else if (!isChecked && adapter.selectedFlowers.contains(flower)) {
                adapter.selectedFlowers.remove(flower);
                if (adapter.selectionListener != null) {
                    adapter.selectionListener.onSelectedFlowersChanged(adapter.selectedFlowers);
                }
            }
        }

        private void update(Flower flower) {
            checkBox.setVisibility(adapter.isSelectionMode ? View.VISIBLE : View.GONE);
            checkBox.setChecked(adapter.selectedFlowers.contains(flower));
            if (flower == null) {
                iconView.setImageBitmap(null);
                nameView.setText(null);
                daysToWateringView.setText(null);
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

    public interface FlowersSelectionListener {
        void onSelectedFlowersChanged(List<Flower> selectedFlowers);
    }
}
