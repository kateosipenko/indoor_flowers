package com.indoor.flowers.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.util.RecyclerListAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class FilterSelectionAdapter extends RecyclerListAdapter<Object, FilterSelectionAdapter.ViewHolder> {

    private List<Object> selectedItems = new ArrayList<>();

    public ArrayList<Long> getSelectedGroups() {
        ArrayList<Long> selectedGroups = new ArrayList<>();
        for (Object item : selectedItems) {
            if (item instanceof Group) {
                selectedGroups.add(((Group) item).getId());
            }
        }

        return selectedGroups;
    }

    public ArrayList<Long> getSelectedFlowers() {
        ArrayList<Long> selectedFlowers = new ArrayList<>();
        for (Object item : selectedItems) {
            if (item instanceof Flower) {
                selectedFlowers.add(((Flower) item).getId());
            }
        }

        return selectedFlowers;
    }

    @Override
    public int getRowLayoutRes() {
        return R.layout.row_filter_selection;
    }

    @Override
    public ViewHolder onCreateViewHolder(View view) {
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.update(getItemByPosition(position));
    }

    public void setSelection(List<Long> selectedElements) {
        this.selectedItems.clear();
        if (selectedElements != null) {
            for (Object item : items) {
                if (item instanceof Flower && selectedElements.contains(((Flower) item).getId())) {
                    selectedItems.add(item);
                } else if (item instanceof Group && selectedElements.contains(((Group) item).getId())) {
                    selectedItems.add(item);
                }
            }
        }

        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rfs_check_box)
        CheckBox checkBox;
        @BindView(R.id.rfs_icon)
        ImageView iconView;
        @BindView(R.id.rfs_title)
        TextView titleView;

        private FilterSelectionAdapter adapter;

        public ViewHolder(View itemView, FilterSelectionAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.adapter = adapter;
        }

        @OnCheckedChanged(R.id.rfs_check_box)
        void onCheckChanged(CompoundButton buttonView, boolean isChecked) {
            Object item = adapter.getItemByPosition(getAdapterPosition());
            if (isChecked && !adapter.selectedItems.contains(item)) {
                adapter.selectedItems.add(item);
            } else if (!isChecked && adapter.selectedItems.contains(item)) {
                adapter.selectedItems.remove(item);
            }
        }

        @OnClick({R.id.rfs_root, R.id.rfs_icon, R.id.rfs_title})
        void onItemClick(View view) {
            checkBox.setChecked(!checkBox.isChecked());
        }

        private void update(Object object) {
            checkBox.setChecked(adapter.selectedItems.contains(object));
            if (object == null) {
                iconView.setImageBitmap(null);
                titleView.setText(null);
                iconView.setBackground(null);
            } else if (object instanceof Flower) {
                iconView.setBackgroundColor(Res.getColor(R.color.primary50));
                Flower flower = (Flower) object;
                if (!TextUtils.isEmpty(flower.getImagePath())) {
                    Picasso.with(itemView.getContext())
                            .load(new File(flower.getImagePath()))
                            .into(iconView);
                } else {
                    iconView.setImageResource(R.drawable.ic_flower);
                }

                titleView.setText(flower.getName());
            } else if (object instanceof Group) {
                iconView.setBackgroundColor(Res.getColor(R.color.accent50));
                Group group = (Group) object;
                iconView.setImageResource(R.drawable.ic_grouping);
                titleView.setText(group.getName());
            }
        }
    }
}
