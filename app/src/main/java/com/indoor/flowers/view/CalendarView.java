package com.indoor.flowers.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evgeniysharafan.utils.Res;
import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.CalendarDaysAdapter;
import com.indoor.flowers.adapter.CalendarDaysAdapter.OnDayClickedListener;
import com.indoor.flowers.model.NotificationWithTarget;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalendarView extends LinearLayout {

    @BindView(R.id.vc_month_title)
    TextView currentMonthView;
    @BindView(R.id.vc_week_days)
    LinearLayout weekDaysContainer;
    @BindView(R.id.vc_days_list)
    RecyclerView daysList;

    private Calendar currentDate;
    private CalendarDaysAdapter daysAdapter;
    private OnMonthChangedListener monthChangedListener;

    public CalendarView(Context context) {
        super(context);
        initialize();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public Calendar getStartDate() {
        return daysAdapter.getStartDate();
    }

    public Calendar getEndDate() {
        return daysAdapter.getEndDate();
    }

    @OnClick(R.id.vc_previous_month)
    void onPreviousMonthClicked() {
        currentDate.add(Calendar.MONTH, -1);
        daysAdapter.setMonthDate(currentDate);
        onMonthUpdated();
    }

    @OnClick(R.id.vc_next_month)
    void onNextMonthClicked() {
        currentDate.add(Calendar.MONTH, 1);
        daysAdapter.setMonthDate(currentDate);
        onMonthUpdated();
    }

    public void setMonthChangedListener(OnMonthChangedListener listener) {
        this.monthChangedListener = listener;
        if (listener != null) {
            listener.onMonthChanged(daysAdapter.getStartDate(), daysAdapter.getEndDate());
        }
    }

    public void setDayClickListener(OnDayClickedListener listener) {
        this.daysAdapter.setDayClickListener(listener);
    }

    public void setEventsForMonth(HashMap<Integer, List<NotificationWithTarget>> events) {
        daysAdapter.setEvents(events);
    }

    private void initialize() {
        Locale locale = Locale.forLanguageTag("ru-RU");
        Locale.setDefault(locale);
        LayoutInflater.from(getContext()).inflate(R.layout.view_calendar, this, true);
        ButterKnife.bind(this);
        setOrientation(VERTICAL);

        currentDate = Calendar.getInstance();
        setupWeekDays();
        setupDaysList();
        onMonthUpdated();
    }

    private void setupDaysList() {
        daysAdapter = new CalendarDaysAdapter();
        daysList.setLayoutManager(new GridLayoutManager(getContext(), 7));
        daysList.setAdapter(daysAdapter);
        daysAdapter.setMonthDate(currentDate);
    }

    private void setupWeekDays() {
        Calendar temp = Calendar.getInstance();
        temp.set(Calendar.DAY_OF_WEEK, temp.getActualMinimum(Calendar.DAY_OF_WEEK));
        for (int i = 0; i < weekDaysContainer.getChildCount(); i++) {
            TextView textView = (TextView) weekDaysContainer.getChildAt(i);
            textView.setText(temp.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
            temp.add(Calendar.DAY_OF_WEEK, 1);
        }
    }

    private void onMonthUpdated() {
        currentMonthView.setText(Res.getString(R.string.month_title_format,
                getTextForMonth(currentDate.get(Calendar.MONTH)),
                currentDate));
        if (monthChangedListener != null) {
            monthChangedListener.onMonthChanged(daysAdapter.getStartDate(), daysAdapter.getEndDate());
        }
    }

    private String getTextForMonth(int monthNumber) {
        int id = Res.get().getIdentifier("month_" + monthNumber, "string", Utils.getPackageName());
        return Res.getString(id);
    }


    public interface OnMonthChangedListener {
        void onMonthChanged(Calendar startDate, Calendar endDate);
    }
}
