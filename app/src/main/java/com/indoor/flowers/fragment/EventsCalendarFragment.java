package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evgeniysharafan.utils.Res;
import com.evgeniysharafan.utils.Toasts;
import com.indoor.flowers.R;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Event;
import com.indoor.flowers.util.OnItemClickListener;
import com.indoor.flowers.view.CalendarView;
import com.indoor.flowers.view.CalendarView.OnMonthChangedListener;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EventsCalendarFragment extends Fragment implements OnItemClickListener<Calendar>,
        OnMonthChangedListener {

    @BindView(R.id.fec_events_calendar)
    CalendarView calendarView;

    private Unbinder unbinder;
    private FlowersProvider provider;

    public static EventsCalendarFragment newInstance() {
        return new EventsCalendarFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        provider = new FlowersProvider(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_calendar, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupActionBar();
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
    public void onItemClicked(Calendar item) {
        Toasts.showFuture();
    }

    @Override
    public void onMonthChanged(Calendar startDate, Calendar endDate) {
        List<Event> events = provider.getEventsForPeriod(startDate, endDate);
        calendarView.setEventsForMonth(events);
    }

    private void setupActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setTitle(Res.getString(R.string.mnd_calendar));
    }
}
