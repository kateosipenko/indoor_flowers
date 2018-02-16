package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.evgeniysharafan.utils.Toasts;
import com.indoor.flowers.R;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.model.SettingData;
import com.indoor.flowers.util.FlowersAlarmsUtils;
import com.indoor.flowers.view.MonthPeriodChooser;
import com.indoor.flowers.view.SettingsDataView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

public class CreateGroupFragment extends Fragment implements OnCheckedChangeListener {

    @BindView(R.id.fcr_name)
    EditText nameView;
    @BindView(R.id.fcr_settings_group)
    RadioGroup settingsGroup;
    @BindView(R.id.fcr_setting_data)
    SettingsDataView settingsDataView;
    @BindView(R.id.fcr_month_chooser)
    MonthPeriodChooser monthPeriodChooser;

    private FlowersProvider flowersProvider;
    private Unbinder unbinder;

    private Group group = new Group();

    public static CreateGroupFragment newInstance() {
        return new CreateGroupFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flowersProvider = new FlowersProvider(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_group, container, false);
        unbinder = ButterKnife.bind(this, view);
        settingsGroup.setOnCheckedChangeListener(this);
        settingsDataView.setMonthChooserView(monthPeriodChooser);
        if (savedInstanceState == null) {
            settingsGroup.check(R.id.fcr_use_flower_settings);
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
        flowersProvider.unbind();
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        group.setUseCommonSettings(checkedId == R.id.fcr_use_common_settings);
        settingsDataView.setEnabled(group.useCommonSettings());
    }

    @OnTextChanged(R.id.fcr_name)
    void onNameTextChanged(CharSequence s, int start, int before, int count) {
        group.setName(nameView.getText().toString());
    }

    @OnClick(R.id.fcr_save)
    void onSaveClick() {
        if (TextUtils.isEmpty(group.getName())) {
            Toasts.showLong(R.string.fcr_name_group_error);
            return;
        }

        SettingData data = settingsDataView.getSettingData();
        if (data.isEmpty() && group.useCommonSettings()) {
            Toasts.showLong(R.string.sdv_data_empty);
            return;
        }

        if (group.useCommonSettings()) {
            group.setSettingData(data);
        } else {
            group.setSettingData(null);
        }

        flowersProvider.createGroup(group);
        FlowersAlarmsUtils.refreshAlarmsForGroup(getActivity(), group);
        getActivity().onBackPressed();
    }
}
