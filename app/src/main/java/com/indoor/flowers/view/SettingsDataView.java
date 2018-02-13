package com.indoor.flowers.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.model.BrightnessLevel;
import com.indoor.flowers.model.HumidityLevel;
import com.indoor.flowers.model.SettingData;
import com.indoor.flowers.util.CalendarUtils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class SettingsDataView extends ConstraintLayout implements OnCheckedChangeListener {

    @BindView(R.id.vsd_brightness_group)
    RadioGroup brightnessGroup;
    @BindView(R.id.vsd_humidity_group)
    RadioGroup humidityGroup;
    @BindView(R.id.vsd_active_period_value)
    TextView activePeriodValue;
    @BindView(R.id.vsd_passive_period_value)
    TextView passivePeriodValue;
    @BindView(R.id.vsd_last_watering_value)
    TextView lastWateringView;
    @BindView(R.id.vsd_watering_value)
    TextView wateringView;
    @BindView(R.id.vsd_month_chooser)
    MonthPeriodChooser monthChooserView;

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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == brightnessGroup) {
            onBrightnessChanged();
        } else if (group == humidityGroup) {
            onHumidityChanged();
        }
    }

    @OnClick(R.id.vsd_active_group)
    void onActivePeriodClicked() {
        monthChooserView.setTitle(R.string.faf_active_period);
        monthChooserView.show(settingData.getActiveFrom(), settingData.getActiveTo(),
                settingData.getPassiveFrom(), settingData.getPassiveTo(), new MonthPeriodChooser.MonthChooserListener() {
                    @Override
                    public void onPeriodChosen(int from, int to) {
                        settingData.setActiveFrom(from);
                        settingData.setActiveTo(to);
                        refreshPeriod(activePeriodValue, from, to);
                    }
                });
    }

    @OnClick(R.id.vsd_passive_group)
    void onPassivePeriodClicked() {
        monthChooserView.setTitle(R.string.faf_passive_period);
        monthChooserView.show(settingData.getPassiveFrom(), settingData.getPassiveTo(),
                settingData.getActiveFrom(), settingData.getActiveTo(), new MonthPeriodChooser.MonthChooserListener() {
                    @Override
                    public void onPeriodChosen(int from, int to) {
                        settingData.setPassiveFrom(from);
                        settingData.setPassiveTo(to);
                        refreshPeriod(passivePeriodValue, from, to);
                    }
                });
    }

    @OnClick(R.id.vsd_last_watering_group)
    void onLastWateringGroupClicked() {
        final Calendar lastWatering;
        if (settingData.getLastWateringDate() == null) {
            lastWatering = Calendar.getInstance();
        } else {
            lastWatering = settingData.getLastWateringDate();
        }

        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (settingData != null) {
                    lastWatering.clear();
                    lastWatering.set(Calendar.YEAR, year);
                    lastWatering.set(Calendar.MONTH, month);
                    lastWatering.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    settingData.setLastWateringDate(lastWatering);
                    refreshLastWateringDate();
                }
            }
        }, lastWatering.get(Calendar.YEAR), lastWatering.get(Calendar.MONTH),
                lastWatering.get(Calendar.DAY_OF_MONTH))
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

    private void refreshLastWateringDate() {
        if (lastWateringView == null) {
            return;
        }

        if (settingData.getLastWateringDate() == null) {
            lastWateringView.setText(R.string.faf_not_set);
        } else {
            lastWateringView.setText(Res.getString(R.string.full_date_format, settingData.getLastWateringDate()));
        }
    }

    private void refreshPeriod(TextView periodValueText, int from, int to) {
        if (periodValueText == null) {
            return;
        }

        String fromName = CalendarUtils.getNameForMonth(from);
        String toName = CalendarUtils.getNameForMonth(to);

        if (TextUtils.isEmpty(fromName) && TextUtils.isEmpty(toName)) {
            periodValueText.setText(R.string.faf_not_set);
        } else {
            periodValueText.setText(fromName + "-" + toName);
        }
    }

    private void onBrightnessChanged() {
        switch (brightnessGroup.getCheckedRadioButtonId()) {
            case R.id.vsd_brightness_small:
                settingData.setBrightnessLevel(BrightnessLevel.SMALL);
                break;
            case R.id.vsd_brightness_medium:
                settingData.setBrightnessLevel(BrightnessLevel.MEDIUM);
                break;
            case R.id.vsd_brightness_full:
                settingData.setBrightnessLevel(BrightnessLevel.MAX);
                break;
        }
    }

    private void onHumidityChanged() {
        switch (humidityGroup.getCheckedRadioButtonId()) {
            case R.id.vsd_humidity_small:
                settingData.setHumidityLevel(HumidityLevel.SMALL);
                break;
            case R.id.vsd_humidity_medium:
                settingData.setHumidityLevel(HumidityLevel.MEDIUM);
                break;
            case R.id.vsd_humidity_full:
                settingData.setHumidityLevel(HumidityLevel.MAX);
                break;
        }
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_settings_data, this, true);
        ButterKnife.bind(this);

        brightnessGroup.setOnCheckedChangeListener(this);
        humidityGroup.setOnCheckedChangeListener(this);
    }
}
