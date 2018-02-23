package com.indoor.flowers.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.evgeniysharafan.utils.Fragments;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.CalendarDaysAdapter.OnDayClickedListener;
import com.indoor.flowers.adapter.EventsPerDayAdapter;
import com.indoor.flowers.database.provider.NotificationsProvider;
import com.indoor.flowers.model.CalendarFilter;
import com.indoor.flowers.model.Notification;
import com.indoor.flowers.model.NotificationWithTarget;
import com.indoor.flowers.util.FilesUtils;
import com.indoor.flowers.view.CalendarView;
import com.indoor.flowers.view.CalendarView.OnMonthChangedListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EventsCalendarFragment extends Fragment implements OnDayClickedListener,
        OnMonthChangedListener {

    private static final int REQUEST_CODE_FILTER = 5645;

    @BindView(R.id.fec_events_calendar)
    CalendarView calendarView;
    @BindView(R.id.fec_events_per_day_list)
    RecyclerView eventsPerDayList;

    private Unbinder unbinder;
    private NotificationsProvider provider;
    private EventsPerDayAdapter eventsPerDayAdapter;
    private CalendarFilter filter;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public static EventsCalendarFragment newInstance() {
        return new EventsCalendarFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        provider = new NotificationsProvider(getActivity());
        filter = FilesUtils.getCalendarFilter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_calendar, container, false);
        unbinder = ButterKnife.bind(this, view);

        initEventsList();

        calendarView.setDayClickListener(this);
        calendarView.setMonthChangedListener(this);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FILTER && data != null && data.hasExtra(EventFilterFragment.EXTRA_FILTER)) {
            filter = (CalendarFilter) data.getSerializableExtra(EventFilterFragment.EXTRA_FILTER);
            onMonthChanged(calendarView.getStartDate(), calendarView.getEndDate());
        }
    }

    @Override
    public void onDayClicked(Calendar item, List<Notification> eventsPerDay) {
        List<NotificationWithTarget> eventWithTargets = provider.getNotificationsTarget(eventsPerDay);
        eventsPerDayAdapter.setItems(eventWithTargets);
    }

    @Override
    public void onMonthChanged(final Calendar startDate, final Calendar endDate) {
        eventsPerDayAdapter.clear();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final HashMap<Integer, List<Notification>> events = provider.getEventsForPeriod(startDate,
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_calendar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mc_filter) {
            Fragment fragment = EventFilterFragment.newInstance();
            fragment.setTargetFragment(this, REQUEST_CODE_FILTER);
            Fragments.replace(getFragmentManager(), android.R.id.content,
                    fragment, null, true);
        }

        return super.onOptionsItemSelected(item);
    }

    private void initEventsList() {
        if (eventsPerDayAdapter == null) {
            eventsPerDayAdapter = new EventsPerDayAdapter();
        }

        eventsPerDayList.setLayoutManager(new LinearLayoutManager(getActivity()));
        eventsPerDayList.setAdapter(eventsPerDayAdapter);
    }
}
