package com.indoor.flowers.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.CalendarDaysAdapter.OnDayClickedListener;
import com.indoor.flowers.adapter.NotificationsByDaysAdapter;
import com.indoor.flowers.adapter.NotificationsByDaysAdapter.NotificationDoneListener;
import com.indoor.flowers.database.provider.NotificationsProvider;
import com.indoor.flowers.model.CalendarFilter;
import com.indoor.flowers.model.NotificationWithTarget;
import com.indoor.flowers.util.CalendarUtils;
import com.indoor.flowers.util.FilesUtils;
import com.indoor.flowers.util.FlowersAlarmsUtils;
import com.indoor.flowers.util.SpaceItemDecoration;
import com.indoor.flowers.view.CalendarView;
import com.indoor.flowers.view.CalendarView.OnMonthChangedListener;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@SuppressWarnings("ConstantConditions")
public class CalendarNearbyNotificationsFragment extends Fragment implements OnDayClickedListener,
        OnMonthChangedListener, NotificationDoneListener {

    @BindView(R.id.fcnn_events_calendar)
    CalendarView calendarView;
    @BindView(R.id.fcnn_notifications_list)
    RecyclerView notificationsListView;

    private Unbinder unbinder;
    private NotificationsProvider provider;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private CalendarFilter filter;

    private NotificationsByDaysAdapter adapter;

    public static CalendarNearbyNotificationsFragment newInstance() {
        return new CalendarNearbyNotificationsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provider = new NotificationsProvider(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_nearby_notifications, container, false);
        unbinder = ButterKnife.bind(this, view);
        filter = FilesUtils.getCalendarFilter();
        initList();
        onMonthChanged(calendarView.getStartDate(), calendarView.getEndDate());
        calendarView.setDayClickListener(this);
        calendarView.setMonthChangedListener(this);
        onDayClicked(calendarView.getCurrentDate(), calendarView.getNotificationsPerCurrentDay());
        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        provider.unbind();
        super.onDestroy();
    }

    @Override
    public void onDayClicked(final Calendar item, List<NotificationWithTarget> eventsPerDay) {
        adapter.setItems(eventsPerDay);
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                final List<NotificationWithTarget> events = provider.getNearbyEvents(item, 1, true);
//                Activity activity = getActivity();
//                if (activity != null && !activity.isDestroyed()) {
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            adapter.setItems(events);
//                        }
//                    });
//                }
//            }
//        });
    }

    @Override
    public void onMonthChanged(final Calendar startDate, final Calendar endDate) {
        adapter.clear();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final SparseArray<List<NotificationWithTarget>> events = provider.getEventsForPeriod(startDate,
                        endDate, filter);
                Activity activity = getActivity();
                if (activity != null && !activity.isDestroyed()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (calendarView != null) {
                                calendarView.setEventsForMonth(events);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onNotificationDone(final NotificationWithTarget notification) {
        if (CalendarUtils.isToday(notification.getEventDate())) {
            provider.markEventDone(notification.getNotification(), Calendar.getInstance());
            adapter.onNotificationDone(notification);
            FlowersAlarmsUtils.refreshAlarmsForEvent(getActivity(), notification.getNotification().getId());
        } else {
            final Calendar eventDoneDate = Calendar.getInstance();
            new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    eventDoneDate.set(Calendar.YEAR, year);
                    eventDoneDate.set(Calendar.MONTH, month);
                    eventDoneDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    provider.markEventDone(notification.getNotification(), eventDoneDate);
                    adapter.onNotificationDone(notification);
                    onDayClicked(calendarView.getCurrentDate(), calendarView.getNotificationsPerCurrentDay());
                    FlowersAlarmsUtils.refreshAlarmsForEvent(getActivity(), notification.getNotification().getId());
                }
            }, eventDoneDate.get(Calendar.YEAR), eventDoneDate.get(Calendar.MONTH),
                    eventDoneDate.get(Calendar.DAY_OF_MONTH))
                    .show();
        }
    }

    private void initList() {
        if (adapter == null) {
            adapter = new NotificationsByDaysAdapter();
            adapter.setEditable(true);
        }

        adapter.setListener(this);

        notificationsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        notificationsListView.addItemDecoration(new SpaceItemDecoration(
                Res.getDimensionPixelSize(R.dimen.margin_normal)));
        notificationsListView.setAdapter(adapter);
    }
}
