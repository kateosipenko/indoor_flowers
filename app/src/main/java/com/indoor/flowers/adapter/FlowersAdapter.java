package com.indoor.flowers.adapter;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.util.CalendarUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FlowersAdapter extends RecyclerView.Adapter<FlowersAdapter.ViewHolder> {

    private List<Flower> flowers = new ArrayList<>();
    private Calendar today = Calendar.getInstance();

    public void setFlowers(List<Flower> items) {
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

    private Flower getItemByPosition(int position) {
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

        ConstraintSet set = new ConstraintSet();

        private FlowersAdapter adapter;

        public ViewHolder(View itemView, FlowersAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.adapter = adapter;
        }

        private void update(Flower flower) {
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
                lastWateringView.setText(Res.getString(R.string.full_date_format,
                        flower.getSettings().getLastWateringDate()));
                long daysToWatering = CalendarUtils.getDaysDiff(flower.getSettings().getLastWateringDate(),
                        adapter.today);
                daysToWateringView.setText(Res.getString(R.string.days_to_watering_format, daysToWatering));
                refreshWateringLevel(flower);
            }
        }

        private void refreshWateringLevel(Flower flower) {
            int marginEnd = 0;
            if (flower != null && flower.getSettings() != null) {
                Calendar lastWatering = flower.getSettings().getLastWateringDate();
                long daysToWatering = CalendarUtils.getDaysDiff(lastWatering, adapter.today);
                double ratio = (double) daysToWatering / (double) flower.getSettings().getWateringFrequency();
                marginEnd = (int) (itemView.getMeasuredWidth() - itemView.getMeasuredWidth() * ratio);
            }

            set.clone(rootLayout);
            set.setMargin(R.id.rf_watering_level, ConstraintSet.END, marginEnd);
            set.applyTo(rootLayout);
        }
    }
}
