package com.indoor.flowers.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.evgeniysharafan.utils.Fragments;
import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.CalendarDaysAdapter.OnDayClickedListener;
import com.indoor.flowers.adapter.NotificationsByDaysAdapter;
import com.indoor.flowers.database.provider.NotificationsProvider;
import com.indoor.flowers.model.CalendarFilter;
import com.indoor.flowers.model.NotificationWithTarget;
import com.indoor.flowers.util.FilesUtils;
import com.indoor.flowers.util.SpaceItemDecoration;
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

    @BindView(R.id.fec_events_calendar)
    CalendarView calendarView;
    @BindView(R.id.fec_events_per_day_list)
    RecyclerView eventsPerDayList;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Unbinder unbinder;
    private NotificationsProvider provider;
    private NotificationsByDaysAdapter eventsAdapter;
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_calendar, container, false);
        unbinder = ButterKnife.bind(this, view);
        filter = FilesUtils.getCalendarFilter();
        setupActionBar();
        initEventsList();

        calendarView.setDayClickListener(this);
        calendarView.setMonthChangedListener(this);
        onMonthChanged(calendarView.getStartDate(), calendarView.getEndDate());
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
    public void onDayClicked(Calendar item, List<NotificationWithTarget> eventsPerDay) {
        eventsAdapter.setItems(eventsPerDay);
    }

    @Override
    public void onMonthChanged(final Calendar startDate, final Calendar endDate) {
        eventsAdapter.clear();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final HashMap<Integer, List<NotificationWithTarget>> events = provider.getEventsForPeriod(startDate,
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
            FragmentManager fragmentManager = getParentFragment() != null
                    ? getParentFragment().getFragmentManager() : getFragmentManager();
            if (fragmentManager != null) {
                Fragments.replace(fragmentManager, android.R.id.content,
                        fragment, null, true);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void initEventsList() {
        if (eventsAdapter == null) {
            eventsAdapter = new NotificationsByDaysAdapter();
        }

        eventsAdapter.setEditable(false);
        eventsPerDayList.setLayoutManager(new LinearLayoutManager(getActivity()));
        eventsPerDayList.setAdapter(eventsAdapter);
        eventsPerDayList.addItemDecoration(new SpaceItemDecoration(Res.getDimensionPixelSize(R.dimen.margin_normal)));
    }

    private void setupActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(R.string.action_calendar);
            }
        }
    }
}
