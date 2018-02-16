package com.indoor.flowers.adapter;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.model.FlowerWithSetting;
import com.indoor.flowers.util.CalendarUtils;
import com.indoor.flowers.util.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FlowersAdapter extends RecyclerView.Adapter<FlowersAdapter.ViewHolder> {

    private List<FlowerWithSetting> flowers = new ArrayList<>();
    private Calendar today = Calendar.getInstance();

    private OnItemClickListener<FlowerWithSetting> flowerClickListener;

    public void setFlowerClickListener(OnItemClickListener<FlowerWithSetting> flowerClickListener) {
        this.flowerClickListener = flowerClickListener;
    }

    public void setFlowers(List<FlowerWithSetting> items) {
        this.flowers.clear();
        if (items != null) {
            this.flowers.addAll(items);
        }

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_flower, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.update(getItemByPosition(position));
    }

    @Override
    public int getItemCount() {
        return flowers.size();
    }

    private FlowerWithSetting getItemByPosition(int position) {
        return position >= 0 && position < flowers.size() ? flowers.get(position) : null;
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
            if (adapter.flowerClickListener != null) {
                adapter.flowerClickListener.onItemClicked(adapter.getItemByPosition(getAdapterPosition()));
            }
        }

        private void update(FlowerWithSetting flower) {
            if (flower == null) {
                iconView.setImageBitmap(null);
                nameView.setText(null);
                daysToWateringView.setText(null);
            } else {
                if (!TextUtils.isEmpty(flower.getFlower().getImagePath())) {
                    Picasso.with(itemView.getContext())
                            .load(new File(flower.getFlower().getImagePath()))
                            .into(iconView);
                } else {
                    iconView.setImageBitmap(null);
                }
                nameView.setText(flower.getFlower().getName());
                lastWateringView.setText(Res.getString(R.string.full_date_format,
                        flower.getSettingData().getLastWateringDate()));
                long daysToWatering = CalendarUtils.getDaysDiff(flower.getSettingData().getLastWateringDate(),
                        adapter.today);
                daysToWateringView.setText(Res.getString(R.string.days_to_watering_format, daysToWatering));
                refreshWateringLevel(flower);
            }
        }

        private void refreshWateringLevel(FlowerWithSetting flower) {
            int progress = 100;
            if (flower != null && flower.getSettingData() != null) {
                Calendar lastWatering = flower.getSettingData().getLastWateringDate();
                long daysToWatering = CalendarUtils.getDaysDiff(lastWatering, adapter.today);
                double ratio = (double) daysToWatering / (double) flower.getSettingData().getWateringFrequency();
                progress = (int) (ratio * 100);
            }

            wateringLevel.setProgress(progress);
        }
    }
}
