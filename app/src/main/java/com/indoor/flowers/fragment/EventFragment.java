package com.indoor.flowers.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Event;
import com.indoor.flowers.model.EventType;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.util.FlowersAlarmsUtils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;

public class EventFragment extends Fragment implements OnItemSelectedListener {

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
    @BindView(R.id.fe_check_period)
    CheckBox periodicallyCheck;
    @BindView(R.id.fe_event_type)
    Spinner eventTypeSpinner;
    @BindView(R.id.fe_event_date)
    TextView eventDateView;
    @BindView(R.id.fe_event_time)
    TextView eventTimeView;
    @BindView(R.id.fe_delete)
    Button deleteButton;

    private Unbinder unbinder;
    private FlowersProvider provider;
    private Event event;
    private Object target;

    public static EventFragment newInstance(long eventId) {
        Bundle args = new Bundle();
        args.putLong(KEY_EVENT_ID, eventId);

        EventFragment fragment = new EventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static EventFragment newInstance(long targetId, String targetTable) {
        Bundle args = new Bundle();
        args.putLong(KEY_TARGET_ID, targetId);
        args.putString(KEY_TARGET_TABLE, targetTable);

        EventFragment fragment = new EventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provider = new FlowersProvider(getActivity());

        long eventId = getEventIdFromArgs();
        if (eventId != DatabaseProvider.DEFAULT_ID) {
            event = provider.getEventById(eventId);
        }

        if (event == null) {
            event = new Event();
            event.setId(DatabaseProvider.DEFAULT_ID);
            event.setTargetTable(getTargetTableFromArgs());
            event.setTargetId(getTargetIdFromArgs());
            event.setCreationDate(Calendar.getInstance());
            event.setEventDate(Calendar.getInstance());
        }

        if (!TextUtils.isEmpty(event.getTargetTable())) {
            target = Group.TABLE_NAME.equals(event.getTargetTable())
                    ? provider.getGroupById(event.getTargetId())
                    : provider.getFlowerById(event.getTargetId());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupSpinner();
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String chosenEventTypeName = (String) parent.getItemAtPosition(position);
        if (Res.getString(R.string.event_watering).equalsIgnoreCase(chosenEventTypeName)) {
            event.setEventType(EventType.WATERING);
        } else if (Res.getString(R.string.event_nutrition).equalsIgnoreCase(chosenEventTypeName)) {
            event.setEventType(EventType.NUTRITION);
        } else if (Res.getString(R.string.event_transplanting).equalsIgnoreCase(chosenEventTypeName)) {
            event.setEventType(EventType.TRANSPLANTING);
        }

        refreshTitle();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @OnCheckedChanged(R.id.fe_check_period)
    void onPeriodCheckChanged(CompoundButton button, boolean isChecked) {
        frequencyView.setEnabled(isChecked);
    }

    @OnClick(R.id.fe_event_date_group)
    void onDateGroupClicked() {
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar eventDate = event.getEventDate();
                eventDate.set(Calendar.YEAR, year);
                eventDate.set(Calendar.MONTH, month);
                eventDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                refreshDate();
            }
        }, event.getEventDate().get(Calendar.YEAR),
                event.getEventDate().get(Calendar.MONTH),
                event.getEventDate().get(Calendar.DAY_OF_MONTH))
                .show();
    }

    @OnClick(R.id.fe_event_time_group)
    void onTimeClicked() {
        new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar eventDate = event.getEventDate();
                eventDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                eventDate.set(Calendar.MINUTE, minute);
                refreshTime();
            }
        }, event.getEventDate().get(Calendar.HOUR_OF_DAY),
                event.getEventDate().get(Calendar.MINUTE), true).show();
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

        event.setFrequency(periodicallyCheck.isChecked() ? frequency : null);
        provider.createOrUpdateEvent(event);
        FlowersAlarmsUtils.refreshAlarmsForEvent(getActivity(), event.getId());
        getActivity().onBackPressed();
    }

    @OnClick(R.id.fe_delete)
    void onDeleteClicked() {
        FlowersAlarmsUtils.deleteEventAlarms(getActivity(), event);
        provider.deleteEvent(event);
        getActivity().onBackPressed();
    }

    private void setupViewForEvent() {
        refreshTitle();
        refreshDate();
        refreshTime();
        eventTypeSpinner.setSelection(getPositionForEventType(event.getEventType()));
        periodicallyCheck.setChecked(event.getFrequency() != null);
        if (event.getFrequency() != null) {
            frequencyView.setText(String.valueOf(event.getFrequency()));
        } else {
            frequencyView.setText(null);
        }

        refreshDeleteButton();
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
            titleView.setText(String.format(FORMAT_TITLE, eventTypeSpinner.getSelectedItem(),
                    targetName));
        } else {
            titleView.setText(event.getTitle());
        }
    }

    private void refreshDate() {
        if (eventDateView == null) {
            return;
        }

        eventDateView.setText(Res.getString(R.string.full_date_format, event.getEventDate()));
    }

    private void refreshTime() {
        if (eventTimeView == null) {
            return;
        }

        eventTimeView.setText(Res.getString(R.string.full_time_format, event.getEventDate()));
    }

    private int getPositionForEventType(@EventType int type) {
        int result = 0;
        switch (type) {
            case EventType.NUTRITION:
                result = 1;
                break;
            case EventType.TRANSPLANTING:
                result = 2;
                break;
            case EventType.WATERING:
                result = 0;
                break;
        }
        return result;
    }

    private void setupSpinner() {
        String[] eventsTypesText = new String[]{
                Res.getString(R.string.event_watering),
                Res.getString(R.string.event_nutrition),
                Res.getString(R.string.event_transplanting)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, eventsTypesText);
        eventTypeSpinner.setAdapter(adapter);
        eventTypeSpinner.setOnItemSelectedListener(this);
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
