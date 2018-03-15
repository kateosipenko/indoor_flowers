package com.indoor.flowers.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.indoor.flowers.R;
import com.indoor.flowers.model.NotificationType;
import com.indoor.flowers.model.NotificationWithTarget;
import com.indoor.flowers.util.CalendarUtils;
import com.indoor.flowers.util.RecyclerListAdapter;
import com.indoor.flowers.view.StatusView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatusDataAdapter extends RecyclerListAdapter<NotificationWithTarget, StatusDataAdapter.ViewHolder> {

    @Override
    public int getRowLayoutRes() {
        return R.layout.row_status;
    }

    @Override
    public ViewHolder onCreateViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.update(getItemByPosition(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rs_status_view)
        StatusView statusView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void update(NotificationWithTarget notification) {
            float daysDiff = CalendarUtils.getDaysDiff(notification.getEventDate(), Calendar.getInstance());
            float currentLevel = 1f - daysDiff / notification.getNotification().getFrequency();
            switch (notification.getNotification().getType()) {
                case NotificationType.FERTILIZER:
                    statusView.setFertilizerLevel(currentLevel);
                    break;
                case NotificationType.WATERING:
                    statusView.setWaterLevel(currentLevel);
                    break;
            }
        }
    }
}
