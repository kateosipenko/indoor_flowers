package com.indoor.flowers.adapter;

import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.model.Notification;
import com.indoor.flowers.util.CalendarUtils;
import com.indoor.flowers.util.EventsUtils;
import com.indoor.flowers.util.RecyclerListAdapter;

import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationsByDaysAdapter extends RecyclerListAdapter<Notification, NotificationsByDaysAdapter.ViewHolder> {

    private Calendar today = Calendar.getInstance();

    private NotificationDoneListener listener;

    public void setListener(NotificationDoneListener listener) {
        this.listener = listener;
    }

    public void addEvents(List<Notification> events) {
        if (events != null) {
            items.addAll(events);
            notifyItemRangeInserted(items.size() - events.size(), events.size());
        }
    }

    public void onNotificationDone(Notification event) {
        int position = -1;
        for (int i = 0; i < items.size(); i++) {
            Notification current = items.get(i);
            if (current.getId() == event.getId() && current.getDate().equals(event.getDate())) {
                position = i;
                break;
            }
        }

        items.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position > 0 ? --position : ++position, "RefreshDay");
    }

    @Nullable
    public Calendar getLastItemDate() {
        Notification lastItem = getItemByPosition(getItemCount() - 1);
        return lastItem != null ? lastItem.getDate() : null;
    }

    @Override
    public int getRowLayoutRes() {
        return R.layout.row_notification;
    }

    @Override
    public ViewHolder onCreateViewHolder(View view) {
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.update(getItemByPosition(position));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (payloads == null || payloads.size() == 0) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            holder.refreshDateView(getItemByPosition(position));
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rn_day_title)
        TextView dayTitle;
        @BindView(R.id.rn_comment)
        TextView commentView;
        @BindView(R.id.rn_title)
        TextView titleView;
        @BindView(R.id.rn_notification_color)
        View colorView;
        @BindView(R.id.rn_done)
        Button doneButton;
        @BindView(R.id.rn_event_container)
        ViewGroup eventsContainer;

        private NotificationsByDaysAdapter adapter;

        public ViewHolder(View itemView, NotificationsByDaysAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.adapter = adapter;
        }

        @OnClick(R.id.rn_done)
        void onDoneClicked() {
            if (adapter.listener != null) {
                adapter.listener.onNotificationDone(adapter.getItemByPosition(getAdapterPosition()));
            }
        }

        private void update(Notification event) {
            colorView.setBackgroundColor(EventsUtils.getColorForEventType(event.getType()));
            titleView.setText(event.getTitle());
            commentView.setText(event.getComment());

            boolean isToday = CalendarUtils.isToday(event.getDate());

            doneButton.setVisibility(isToday || event.getDate().before(adapter.today)
                    ? View.VISIBLE : View.GONE);
            if (isToday) {
                eventsContainer.setBackgroundTintList(null);
            } else if (event.getDate().before(adapter.today)) {
                eventsContainer.setBackgroundTintList(ColorStateList.valueOf(Res.getColor(R.color.material_red_primary50)));
            } else if (event.getDate().after(adapter.today)) {
                eventsContainer.setBackgroundTintList(ColorStateList.valueOf(Res.getColor(R.color.material_grey_primary100)));
            }

            refreshDateView(event);
        }

        private void refreshDateView(Notification event) {
            Notification previous = adapter.getItemByPosition(getAdapterPosition() - 1);
            dayTitle.setText(Res.getString(R.string.day_title_format, event.getDate()));
            dayTitle.setVisibility(previous == null || CalendarUtils.getDaysDiff(previous.getDate(), event.getDate()) > 0
                    ? View.VISIBLE : View.GONE);
        }
    }

    public interface NotificationDoneListener {
        void onNotificationDone(Notification event);
    }
}
