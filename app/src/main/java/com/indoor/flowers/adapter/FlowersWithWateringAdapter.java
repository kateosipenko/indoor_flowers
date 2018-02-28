package com.indoor.flowers.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.evgeniysharafan.utils.picasso.CircleTransformation;
import com.indoor.flowers.R;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.FlowerWithWatering;
import com.indoor.flowers.util.CalendarUtils;
import com.indoor.flowers.util.RecyclerListAdapter;
import com.indoor.flowers.view.NotificationStatus;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FlowersWithWateringAdapter extends RecyclerListAdapter<FlowerWithWatering, FlowersWithWateringAdapter.ViewHolder> {

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

        @BindView(R.id.rf_icon)
        ImageView iconView;
        @BindView(R.id.rf_title)
        TextView nameView;
        @BindView(R.id.rf_watering_status)
        NotificationStatus wateringStatus;

        private FlowersWithWateringAdapter adapter;

        public ViewHolder(View itemView, FlowersWithWateringAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.adapter = adapter;
            this.wateringStatus.setVisibility(View.VISIBLE);
        }

        @OnClick({R.id.rf_title, R.id.rf_icon, R.id.rf_root})
        void onFlowerClick(View view) {
            if (adapter.listener != null) {
                adapter.listener.onItemClicked(adapter.getItemByPosition(getAdapterPosition()));
            }
        }

        private void update(FlowerWithWatering flowerWithWatering) {
            Flower flower = flowerWithWatering.getFlower();

            Calendar nextWatering = (Calendar) flowerWithWatering.getDate().clone();
            nextWatering.add(Calendar.DAY_OF_YEAR, flowerWithWatering.getFrequency());

            int daysDiff = CalendarUtils.getDaysDiff(Calendar.getInstance(), nextWatering);
            wateringStatus.setProgress(flowerWithWatering.getFrequency(), daysDiff);
            if (flower == null) {
                iconView.setImageBitmap(null);
                nameView.setText(null);
            } else {
                if (!TextUtils.isEmpty(flower.getImagePath())) {
                    Picasso.with(itemView.getContext())
                            .load(new File(flower.getImagePath()))
                            .transform(new CircleTransformation(0, 0))
                            .into(iconView);
                } else {
                    iconView.setImageBitmap(null);
                }
                nameView.setText(flower.getName());
            }
        }
    }
}
