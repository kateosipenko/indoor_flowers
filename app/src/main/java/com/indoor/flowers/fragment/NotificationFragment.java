package com.indoor.flowers.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.database.provider.NotificationsProvider;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.model.Notification;
import com.indoor.flowers.model.NotificationType;
import com.indoor.flowers.util.EventsUtils;
import com.indoor.flowers.util.FlowersAlarmsUtils;
import com.indoor.flowers.util.Prefs;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

public class NotificationFragment extends Fragment implements OnNavigationItemSelectedListener {

    private static final String KEY_TARGET_ID = "key_target_id";
    private static final String KEY_TARGET_TABLE = "key_target_table";
    private static final String KEY_EVENT_ID = "key_event_id";

    private static final String FORMAT_TITLE = "%1$s %2$s";

    @BindView(R.id.fe_title)
    EditText titleView;
    @BindView(R.id.fe_comment)
    EditText commentView;
    @BindView(R.id.fe_frequency)
    EditText frequencyView;
    @BindView(R.id.fe_event_date)
    TextView eventDateView;
    @BindView(R.id.fe_event_end_date)
    TextView endDateView;
    @BindView(R.id.fe_event_time)
    TextView eventTimeView;
    @BindView(R.id.fe_delete)
    Button deleteButton;
    @BindView(R.id.fe_event_type_group)
    BottomNavigationView eventTypeGroup;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fe_end_date_clear)
    View clearEndDateView;

    private Unbinder unbinder;
    private NotificationsProvider provider;
    private Notification event;
    private Object target;

    public static NotificationFragment newInstance(long eventId) {
        Bundle args = new Bundle();
        args.putLong(KEY_EVENT_ID, eventId);

        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static NotificationFragment newInstance(long targetId, String targetTable) {
        Bundle args = new Bundle();
        args.putLong(KEY_TARGET_ID, targetId);
        args.putString(KEY_TARGET_TABLE, targetTable);

        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provider = new NotificationsProvider(getActivity());

        long eventId = getEventIdFromArgs();
        if (eventId != DatabaseProvider.DEFAULT_ID) {
            event = provider.getNotificationById(eventId);
        }

        if (event == null) {
            event = new Notification();
            event.setId(DatabaseProvider.DEFAULT_ID);
            event.setTargetTable(getTargetTableFromArgs());
            event.setTargetId(getTargetIdFromArgs());
            Calendar eventDate = Calendar.getInstance();
            Calendar preferredTime = Prefs.getPreferredNotificationTime();
            eventDate.set(Calendar.HOUR_OF_DAY, preferredTime.get(Calendar.HOUR_OF_DAY));
            eventDate.set(Calendar.MINUTE, preferredTime.get(Calendar.MINUTE));
            event.setDate(eventDate);
        }

        if (!TextUtils.isEmpty(event.getTargetTable())) {
            FlowersProvider provider = new FlowersProvider(getActivity());
            target = Group.TABLE_NAME.equals(event.getTargetTable())
                    ? provider.getGroupById(event.getTargetId())
                    : provider.getFlowerById(event.getTargetId());
            provider.unbind();
        }

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupActionBar();
        eventTypeGroup.setOnNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            setupViewForEvent();
        }
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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
    }

    @OnClick(R.id.fe_event_date_group)
    void onDateGroupClicked() {
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar eventDate = event.getDate();
                eventDate.set(Calendar.YEAR, year);
                eventDate.set(Calendar.MONTH, month);
                eventDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                refreshDate(eventDateView, eventDate);
            }
        }, event.getDate().get(Calendar.YEAR),
                event.getDate().get(Calendar.MONTH),
                event.getDate().get(Calendar.DAY_OF_MONTH))
                .show();
    }

    @OnClick(R.id.fe_end_date_clear)
    void onClearEndDateClick() {
        event.setEndDate(null);
        refreshDate(endDateView, null);
    }

    @OnClick(R.id.fe_event_end_date_group)
    void onEndDateGroupClicked() {
        final Calendar endDate = event.getEndDate() != null
                ? event.getEndDate() : Calendar.getInstance();

        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endDate.set(Calendar.YEAR, year);
                endDate.set(Calendar.MONTH, month);
                endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                event.setEndDate(endDate);
                refreshDate(endDateView, endDate);
            }
        }, endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH),
                endDate.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    @OnTextChanged(R.id.fe_event_end_date)
    void onEndDateTextChanged(CharSequence s, int start, int before, int count) {
        if (Res.getString(R.string.faf_date_not_set).equals(endDateView.getText().toString())) {
            clearEndDateView.setVisibility(View.INVISIBLE);
        } else {
            clearEndDateView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.fe_event_time_group)
    void onTimeClicked() {
        Calendar preferredTime = Prefs.getPreferredNotificationTime();
        new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar eventDate = event.getDate();
                eventDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                eventDate.set(Calendar.MINUTE, minute);
                Prefs.setPreferredNotificationTime(eventDate);
                refreshTime();
            }
        }, preferredTime.get(Calendar.HOUR_OF_DAY),
                preferredTime.get(Calendar.MINUTE), true).show();
    }

    @OnClick(R.id.fe_save)
    void onSaveClicked() {
        event.setTitle(titleView.getText().toString().trim());
        event.setComment(commentView.getText().toString().trim());
        Integer frequency = null;
        try {
            frequency = Integer.valueOf(frequencyView.getText().toString());
        } catch (NumberFormatException ignore) {
        }

        event.setFrequency(frequency);
        if (frequency == null) {
            event.setEndDate(event.getDate());
        }

        provider.createOrUpdateNotification(event);
        FlowersAlarmsUtils.refreshAlarmsForEvent(getActivity(), event.getId());
        getActivity().onBackPressed();
    }

    @OnClick(R.id.fe_delete)
    void onDeleteClicked() {
        FlowersAlarmsUtils.deleteEventAlarms(getActivity(), event);
        provider.deleteNotification(event);
        getActivity().onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        @NotificationType int eventType = getSelectedEventType(item.getItemId());
        event.setType(eventType);
        refreshTitle();
        return true;
    }

    private void setupActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.fe_screen_title);
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void setupViewForEvent() {
        refreshDate(eventDateView, event.getDate());
        refreshDate(endDateView, event.getEndDate());
        refreshTime();
        setSelectedEventType(event.getType());
        if (event.getFrequency() != null) {
            frequencyView.setText(String.valueOf(event.getFrequency()));
        } else {
            frequencyView.setText(null);
        }

        refreshDeleteButton();
        refreshTitle();
    }

    private void refreshDeleteButton() {
        if (deleteButton == null) {
            return;
        }

        deleteButton.setVisibility(event.getId() == DatabaseProvider.DEFAULT_ID ? View.GONE : View.VISIBLE);
    }

    private void refreshTitle() {
        if (titleView == null) {
            return;
        }

        String targetName = target != null && target instanceof Flower
                ? ((Flower) target).getName()
                : (target != null && target instanceof Group
                ? ((Group) target).getName() : "");
        if (TextUtils.isEmpty(event.getTitle())) {
            titleView.setText(String.format(FORMAT_TITLE,
                    EventsUtils.getTitleForEvent(event.getType()),
                    targetName));
        } else {
            titleView.setText(event.getTitle());
        }
    }

    @NotificationType
    private int getSelectedEventType(int itemId) {
        @NotificationType int result = NotificationType.WATERING;
        if (eventTypeGroup != null) {
            switch (itemId) {
                case R.id.met_watering:
                    result = NotificationType.WATERING;
                    break;
                case R.id.met_fertilizer:
                    result = NotificationType.FERTILIZER;
                    break;
                case R.id.met_transplant:
                    result = NotificationType.TRANSPLANTING;
                    break;
            }
        }

        return result;
    }

    private void setSelectedEventType(@NotificationType int eventType) {
        if (eventTypeGroup == null) {
            return;
        }

        switch (eventType) {
            case NotificationType.FERTILIZER:
                eventTypeGroup.setSelectedItemId(R.id.met_fertilizer);
                break;
            case NotificationType.TRANSPLANTING:
                eventTypeGroup.setSelectedItemId(R.id.met_transplant);
                break;
            case NotificationType.WATERING:
            case NotificationType.CREATED:
                eventTypeGroup.setSelectedItemId(R.id.met_watering);
                break;
        }
    }

    private void refreshDate(TextView view, Calendar date) {
        if (view == null) {
            return;
        }

        if (date == null) {
            view.setText(Res.getString(R.string.faf_date_not_set));
        } else {
            view.setText(Res.getString(R.string.full_date_format, date));
        }
    }

    private void refreshTime() {
        if (eventTimeView == null) {
            return;
        }

        eventTimeView.setText(Res.getString(R.string.full_time_format, event.getDate()));
    }

    // region ARGUMENTS

    private long getEventIdFromArgs() {
        return getArguments() != null && getArguments().containsKey(KEY_EVENT_ID)
                ? getArguments().getLong(KEY_EVENT_ID, DatabaseProvider.DEFAULT_ID)
                : DatabaseProvider.DEFAULT_ID;
    }

    private long getTargetIdFromArgs() {
        return getArguments() != null && getArguments().containsKey(KEY_TARGET_ID)
                ? getArguments().getLong(KEY_TARGET_ID, DatabaseProvider.DEFAULT_ID)
                : DatabaseProvider.DEFAULT_ID;
    }

    private String getTargetTableFromArgs() {
        return getArguments() != null && getArguments().containsKey(KEY_TARGET_TABLE)
                ? getArguments().getString(KEY_TARGET_TABLE)
                : null;
    }

    // endregion ARGUMENTS
}
