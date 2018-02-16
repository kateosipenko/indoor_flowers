package com.indoor.flowers.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.model.SettingData;
import com.indoor.flowers.util.Callback;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class SettingsDataView extends LinearLayout {

    @BindView(R.id.vsd_last_watering_value)
    TextView lastWateringView;
    @BindView(R.id.vsd_watering_value)
    TextView wateringView;
    @BindView(R.id.vsd_preferred_time_value)
    TextView preferredTimeValue;
    @BindView(R.id.vsd_last_nutrition_value)
    TextView lastNutritionValueView;
    @BindView(R.id.vsd_nutrition_value)
    TextView nutritionView;
    @BindView(R.id.vsd_last_transplanting_value)
    TextView lastTransplantingView;
    @BindView(R.id.vsd_next_transplanting_value)
    TextView nextTransplantingView;

    private SettingData settingData = new SettingData();

    public SettingsDataView(Context context) {
        super(context);
        initialize();
    }

    public SettingsDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public SettingsDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public SettingData getSettingData() {
        return settingData;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.setAlpha(enabled ? 1f : 0.5f);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setEnabled(enabled);
        }
    }

    @OnClick(R.id.vsd_last_nutrition_group)
    void onLastNutritionClicked() {
        showDatePicker(settingData.getLastNutritionDate(), new Callback<Calendar>() {
            @Override
            public void execute(Calendar parameter) {
                settingData.setLastNutritionDate(parameter);
                refreshDateText(lastNutritionValueView, settingData.getLastNutritionDate());
            }
        });
    }

    @OnClick(R.id.vsd_last_watering_group)
    void onLastWateringGroupClicked() {
        showDatePicker(settingData.getLastWateringDate(), new Callback<Calendar>() {
            @Override
            public void execute(Calendar parameter) {
                settingData.setLastWateringDate(parameter);
                refreshDateText(lastWateringView, settingData.getLastWateringDate());
            }
        });
    }

    @OnClick(R.id.vsd_last_transplanting)
    void onLastTransplantingClicked() {
        showDatePicker(settingData.getLastTransplanting(), new Callback<Calendar>() {
            @Override
            public void execute(Calendar parameter) {
                settingData.setLastTransplanting(parameter);
                refreshDateText(lastTransplantingView, settingData.getLastTransplanting());
            }
        });
    }

    @OnClick(R.id.vsd_next_transplanting)
    void onNextTransplantingClicked() {
        showDatePicker(settingData.getNextTransplantingDate(), new Callback<Calendar>() {
            @Override
            public void execute(Calendar parameter) {
                settingData.setNextTransplanting(parameter);
                refreshDateText(nextTransplantingView, parameter);
            }
        });
    }

    @OnClick(R.id.vsd_preferred_time)
    void onPreferredTimeClicked() {
        final Calendar preferredTime;
        if (settingData.getPreferredTime() == null) {
            preferredTime = Calendar.getInstance();
        } else {
            preferredTime = settingData.getPreferredTime();
        }

        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                preferredTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                preferredTime.set(Calendar.MINUTE, minute);

                settingData.setPreferredTime(preferredTime);
                refreshPreferredTime();
            }
        }, preferredTime.get(Calendar.HOUR_OF_DAY), preferredTime.get(Calendar.MINUTE), true)
                .show();
    }

    @OnTextChanged(R.id.vsd_watering_value)
    void onWateringTextChanged(CharSequence s, int start, int before, int count) {
        int wateringPeriod = 0;
        try {
            wateringPeriod = Integer.valueOf(wateringView.getText().toString());
        } catch (NumberFormatException ignore) {
        }

        settingData.setWateringFrequency(wateringPeriod);
    }

    @OnTextChanged(R.id.vsd_nutrition_value)
    void onNutritionTextChanged(CharSequence s, int start, int before, int count) {
        int nutritionFreq = 0;
        try {
            nutritionFreq = Integer.valueOf(nutritionView.getText().toString());
        } catch (NumberFormatException ignore) {
        }

        settingData.setNutritionFreq(nutritionFreq);
    }

    private void showDatePicker(Calendar startDate, final Callback<Calendar> callback) {
        final Calendar resultDate;
        if (startDate == null) {
            resultDate = Calendar.getInstance();
        } else {
            resultDate = (Calendar) startDate.clone();
        }

        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (settingData != null) {
                    resultDate.clear();
                    resultDate.set(Calendar.YEAR, year);
                    resultDate.set(Calendar.MONTH, month);
                    resultDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    if (callback != null) {
                        callback.execute(resultDate);
                    }
                }
            }
        }, resultDate.get(Calendar.YEAR), resultDate.get(Calendar.MONTH),
                resultDate.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void refreshDateText(TextView textView, Calendar date) {
        if (textView == null) {
            return;
        }

        if (date == null) {
            textView.setText(R.string.faf_not_set);
        } else {
            textView.setText(Res.getString(R.string.full_date_format, date));
        }
    }

    private void refreshPreferredTime() {
        if (preferredTimeValue == null) {
            return;
        }

        if (settingData.getPreferredTime() == null) {
            preferredTimeValue.setText(R.string.faf_not_set);
        } else {
            preferredTimeValue.setText(Res.getString(R.string.full_time_format, settingData.getPreferredTime()));
        }
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_settings_data, this, true);
        setOrientation(VERTICAL);
        ButterKnife.bind(this);

        refreshPreferredTime();
        refreshDateText(lastNutritionValueView, null);
        refreshDateText(lastTransplantingView, null);
        refreshDateText(lastWateringView, null);
    }
}
