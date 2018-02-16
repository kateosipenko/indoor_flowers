package com.indoor.flowers.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.indoor.flowers.R;
import com.indoor.flowers.model.Event;
import com.indoor.flowers.model.EventType;
import com.indoor.flowers.util.CalendarUtils;
import com.indoor.flowers.util.OnItemClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalendarDaysAdapter extends RecyclerView.Adapter<CalendarDaysAdapter.ViewHolder> {

    private static final int DAYS_COUNT = 7 * 5; // max displaying weeks is 5

    private int selectedPosition = -1;

    private Calendar monthDate;
    private Calendar startDate;
    private OnItemClickListener<Calendar> dayClickListener;
    private int itemHeight;

    private HashMap<Integer, List<Event>> eventsByDays = new HashMap<>();

    public void setDayClickListener(OnItemClickListener<Calendar> listener) {
        this.dayClickListener = listener;
    }

    public void setMonthDate(Calendar monthDate) {
        this.monthDate = monthDate;
        this.monthDate.set(Calendar.DAY_OF_MONTH, 1);

        startDate = (Calendar) monthDate.clone();
        startDate.add(Calendar.DAY_OF_MONTH, -1 * startDate.get(Calendar.DAY_OF_WEEK));
        notifyDataSetChanged();
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public Calendar getEndDate() {
        return getItemByPosition(DAYS_COUNT - 1);
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
        notifyDataSetChanged();
    }

    public void setEvents(List<Event> events) {
        eventsByDays.clear();
        for (int i = 0; i < getItemCount(); i++) {
            eventsByDays.put(getItemByPosition(i).get(Calendar.DAY_OF_YEAR), new ArrayList<Event>());
        }

        for (Event event : events) {
            if (event.getFrequency() != null) {
                int startDay = event.getEventDate().get(Calendar.DAY_OF_YEAR);
                List<Event> eventsPerDay = eventsByDays.get(startDay);
                if (eventsPerDay == null) {
                    if (event.getEventDate().get(Calendar.YEAR) == startDate.get(Calendar.YEAR)) {
                        int daysDiff = startDate.get(Calendar.DAY_OF_YEAR) - event.getEventDate().get(Calendar.DAY_OF_YEAR);
                        startDay = startDate.get(Calendar.DAY_OF_YEAR) + event.getFrequency() - daysDiff;
                        eventsPerDay = eventsByDays.get(startDay);
                    }
                }

                if (eventsPerDay != null) {
                    do {
                        eventsPerDay.add(event);
                        startDay += event.getFrequency();
                        eventsPerDay = eventsByDays.get(startDay);
                    } while (eventsPerDay != null);
                }
            } else {
                List<Event> eventsPerDay = eventsByDays.get(event.getEventDate().get(Calendar.DAY_OF_YEAR));
                eventsPerDay.add(event);
            }
        }

        notifyItemRangeChanged(0, getItemCount(), eventsByDays);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_calendar_day, parent, false);
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
            holder.updateNotifications(getItemByPosition(position));
        }
    }

    @Override
    public int getItemCount() {
        return monthDate == null ? 0 : DAYS_COUNT;
    }

    private Calendar getItemByPosition(int position) {
        Calendar result = (Calendar) startDate.clone();
        result.add(Calendar.DAY_OF_YEAR, position);
        return result;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rcd_root)
        ViewGroup rootView;
        @BindView(R.id.rcd_background)
        View backgroundView;
        @BindView(R.id.rcd_day_text)
        TextView daysTextView;
        @BindView(R.id.rcd_notification_created)
        View createView;
        @BindView(R.id.rcd_notification_watering)
        View wateringView;
        @BindView(R.id.rcd_notification_nutrition)
        View nutritionView;
        @BindView(R.id.rcd_notification_transplantation)
        View transplantationView;

        private CalendarDaysAdapter adapter;

        public ViewHolder(View itemView, CalendarDaysAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.adapter = adapter;
        }

        @OnClick({R.id.rcd_background, R.id.rcd_day_text})
        void onDayClicked() {
            if (adapter.dayClickListener != null) {
                adapter.dayClickListener.onItemClicked(adapter.getItemByPosition(getAdapterPosition()));
            }
        }

        private void update(Calendar calendar) {
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            params.height = adapter.itemHeight;
            itemView.setLayoutParams(params);
            backgroundView.setSelected(getAdapterPosition() == adapter.selectedPosition);
            backgroundView.setEnabled(calendar.get(Calendar.MONTH) == adapter.monthDate.get(Calendar.MONTH));
            rootView.setAlpha(backgroundView.isEnabled() ? 1f : 0.5f);
            daysTextView.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            daysTextView.setSelected(CalendarUtils.isToday(calendar));
            updateNotifications(calendar);
        }

        private void updateNotifications(Calendar calendar) {
            List<Event> eventsForDay = adapter.eventsByDays.get(calendar.get(Calendar.DAY_OF_YEAR));
            int wateringCount = 0;
            int createdCount = 0;
            int nutritionCount = 0;
            int transplantationCount = 0;
            if (eventsForDay != null) {
                for (Event event : eventsForDay) {
                    switch (event.getEventType()) {
                        case EventType.CREATED:
                            ++createdCount;
                            break;
                        case EventType.NUTRITION:
                            ++nutritionCount;
                            break;
                        case EventType.TRANSPLANTING:
                            ++transplantationCount;
                            break;
                        case EventType.WATERING:
                            ++wateringCount;
                            break;
                    }
                }
            }

            refreshEventView(createdCount, createView);
            refreshEventView(nutritionCount, nutritionView);
            refreshEventView(transplantationCount, transplantationView);
            refreshEventView(wateringCount, wateringView);
        }

        private void refreshEventView(int eventsCount, View targetView) {
            targetView.setVisibility(eventsCount > 0 ? View.VISIBLE : View.GONE);
        }
    }
}