package com.indoor.flowers.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.indoor.flowers.R;
import com.indoor.flowers.util.CalendarUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MonthPeriodChooser extends ConstraintLayout {

    private static final int COLUMNS_COUNT = 4;

    @BindView(R.id.vmc_title)
    TextView titleView;
    @BindView(R.id.vmc_months_container)
    GridLayout monthsContainer;

    private int selectedFrom = -1;
    private int selectedTo = -1;

    private int disabledFrom = -1;
    private int disabledTo = -1;

    private MonthChooserListener listener;

    private List<Pair<TextView, Integer>> monthesViews = new ArrayList<>();

    public MonthPeriodChooser(Context context) {
        super(context);
        init(null);
    }

    public MonthPeriodChooser(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MonthPeriodChooser(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    @OnClick({R.id.vmc_shadow, R.id.vmc_cancel})
    void onCloseClicked() {
        setVisibility(GONE);
        clearAll();
    }

    @OnClick(R.id.vmc_ok)
    void onSaveClicked() {
        int selectedFrom = -1;
        int selectedTo = -1;
        for (Pair<TextView, Integer> pair : monthesViews) {
            if (pair.first.isSelected() && selectedFrom == -1) {
                selectedFrom = pair.second;
            } else if (!pair.first.isSelected() && selectedFrom != -1) {
                selectedTo = pair.second - 1;
                break;
            }
        }

        if (listener != null) {
            listener.onPeriodChosen(selectedFrom, selectedTo);
        }

        setVisibility(GONE);
    }

    @OnClick({R.id.vmc_one, R.id.vmc_two, R.id.vmc_three, R.id.vmc_four, R.id.vmc_five,
            R.id.vmc_six, R.id.vmc_seven, R.id.vmc_eight, R.id.vmc_nine, R.id.vmc_ten,
            R.id.vmc_eleven, R.id.vmc_twelve})
    void onMonthClicked(View view) {
        int month = getMonthFromTag(view);
        if (selectedFrom == -1) {
            selectedFrom = month;
        } else if (selectedTo != -1) {
            selectedTo = -1;
            selectedFrom = month;
        } else if (selectedFrom == month) {
            selectedFrom = -1;
        } else {
            if (disabledFrom == -1 || disabledTo == -1
                    || (selectedFrom < disabledFrom && month < disabledFrom
                    || selectedFrom > disabledTo && month > disabledTo)) {
                selectedTo = month;
            }
        }

        if (selectedTo < selectedFrom) {
            int temp = selectedFrom;
            selectedFrom = selectedTo;
            selectedTo = temp;
        }

        setSelection(selectedFrom, selectedTo);
    }

    public void setTitle(@StringRes int title) {
        this.titleView.setText(title);
    }

    public void show(MonthChooserListener listener) {
        this.listener = listener;
        clearAll();
        setVisibility(VISIBLE);
    }

    public void show(int selectedFrom, int selectedTo,
                     int disabledFrom, int disabledTo,
                     MonthChooserListener listener) {
        this.listener = listener;
        clearAll();
        setSelection(selectedFrom, selectedTo);
        setDisabled(disabledFrom, disabledTo);
        setVisibility(VISIBLE);
    }

    private void setSelection(int selectedFrom, int selectedTo) {
        this.selectedFrom = selectedFrom;
        this.selectedTo = selectedTo;
        for (Pair<TextView, Integer> pair : monthesViews) {
            boolean isSelected = false;
            if (selectedFrom != -1 && selectedTo != -1) {
                isSelected = pair.second >= selectedFrom && pair.second <= selectedTo;
            } else {
                isSelected = pair.second == selectedFrom || pair.second == selectedTo;
            }

            pair.first.setSelected(isSelected);
        }
    }

    private void setDisabled(int disabledFrom, int disabledTo) {
        this.disabledFrom = disabledFrom;
        this.disabledTo = disabledTo;
        for (Pair<TextView, Integer> pair : monthesViews) {
            pair.first.setEnabled(disabledFrom == -1 || disabledTo == -1
                    || pair.second < disabledFrom || pair.second > disabledTo);
        }
    }

    private void clearAll() {
        selectedFrom = -1;
        selectedTo = -1;
        disabledFrom = -1;
        disabledTo = -1;
        for (Pair<TextView, Integer> pair : monthesViews) {
            pair.first.setSelected(false);
            pair.first.setEnabled(true);
        }
    }

    private void init(AttributeSet set) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_month_chooser, this, true);
        ButterKnife.bind(this);
        initMonths();
    }

    private void initMonths() {
        monthsContainer.setColumnCount(COLUMNS_COUNT);
        monthesViews.clear();
        for (int i = 0; i < monthsContainer.getChildCount(); i++) {
            View view = monthsContainer.getChildAt(i);
            if (view != null && view.getTag() != null && view instanceof TextView) {
                int month = getMonthFromTag(view);
                Pair<TextView, Integer> pair = new Pair<>((TextView) view, month);
                monthesViews.add(pair);
                pair.first.setText(CalendarUtils.getNameForMonth(month));
            }
        }
    }

    private int getMonthFromTag(View view) {
        int month = -1;
        try {
            month = Integer.valueOf((String) view.getTag());
        } catch (NumberFormatException ignore) {
        }

        return month;
    }

    public interface MonthChooserListener {
        void onPeriodChosen(int from, int to);
    }
}
