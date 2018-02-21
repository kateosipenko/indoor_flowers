package com.indoor.flowers.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.NotificationsByDaysAdapter;
import com.indoor.flowers.adapter.NotificationsByDaysAdapter.NotificationDoneListener;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Event;
import com.indoor.flowers.util.CalendarUtils;
import com.indoor.flowers.util.EndlessRecyclerOnScrollListener;
import com.indoor.flowers.util.EndlessRecyclerOnScrollListener.LoadMoreScrollListener;
import com.indoor.flowers.util.FlowersAlarmsUtils;
import com.indoor.flowers.util.SpaceItemDecoration;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NotificationsFragment extends Fragment implements NotificationDoneListener,
        LoadMoreScrollListener {

    private static final int LOAD_DAYS_COUNT = 10;
    private static final int LOAD_ITEMS_COUNT = 10;

    @BindView(R.id.fn_notifications_list)
    RecyclerView notificationsList;

    private Unbinder unbinder;
    private FlowersProvider provider;
    private NotificationsByDaysAdapter adapter;
    private EndlessRecyclerOnScrollListener loadMoreListener;

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provider = new FlowersProvider(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        unbinder = ButterKnife.bind(this, view);
        initList();
        initialLoadEvents();
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
    public void onNotificationDone(final Event event) {
        if (CalendarUtils.isToday(event.getEventDate())) {
            provider.markEventDone(event, Calendar.getInstance());
            adapter.onNotificationDone(event);
            FlowersAlarmsUtils.refreshAlarmsForEvent(getActivity(), event.getId());
        } else {
            final Calendar eventDoneDate = Calendar.getInstance();
            new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    eventDoneDate.set(Calendar.YEAR, year);
                    eventDoneDate.set(Calendar.MONTH, month);
                    eventDoneDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    provider.markEventDone(event, eventDoneDate);
                    adapter.onNotificationDone(event);
                    FlowersAlarmsUtils.refreshAlarmsForEvent(getActivity(), event.getId());
                }
            }, eventDoneDate.get(Calendar.YEAR), eventDoneDate.get(Calendar.MONTH),
                    eventDoneDate.get(Calendar.DAY_OF_MONTH))
                    .show();
        }
    }

    @Override
    public void onLoadMore() {
        Calendar startDate = adapter.getLastItemDate();
        if (startDate != null) {
            startDate = (Calendar) startDate.clone();
            startDate.add(Calendar.DAY_OF_YEAR, 1);
            List<Event> events = provider.getNearbyEvents(startDate, LOAD_DAYS_COUNT,
                    LOAD_ITEMS_COUNT, false);
            adapter.addEvents(events);
            loadMoreListener.onLoadingCompleted();
        }
    }

    private void initialLoadEvents() {
        List<Event> events = provider.getNearbyEvents(Calendar.getInstance(), LOAD_DAYS_COUNT,
                LOAD_ITEMS_COUNT, true);
        adapter.setItems(events);
        loadMoreListener.onLoadingCompleted();
    }

    private void initList() {
        if (adapter == null) {
            adapter = new NotificationsByDaysAdapter();
        }

        adapter.setListener(this);

        notificationsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        notificationsList.addItemDecoration(new SpaceItemDecoration(
                Res.getDimensionPixelSize(R.dimen.margin_normal)));
        notificationsList.setAdapter(adapter);

        loadMoreListener = new EndlessRecyclerOnScrollListener(notificationsList, this);
        notificationsList.addOnScrollListener(loadMoreListener);
    }
}