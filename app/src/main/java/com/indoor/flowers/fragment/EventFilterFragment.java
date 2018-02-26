package com.indoor.flowers.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.FilterSelectionAdapter;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.CalendarFilter;
import com.indoor.flowers.model.CalendarFilter.FilterElements;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.model.NotificationType;
import com.indoor.flowers.util.FilesUtils;
import com.indoor.flowers.util.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;

public class EventFilterFragment extends Fragment implements OnCheckedChangeListener {

    public static final String EXTRA_FILTER = "extra_filter";

    @BindView(R.id.fef_filter_elements_group)
    RadioGroup elementsGroup;
    @BindView(R.id.fef_selected_list)
    RecyclerView selectedItemsList;
    @BindView(R.id.fef_events_created)
    CheckBox createdBox;
    @BindView(R.id.fef_events_watering)
    CheckBox wateringBox;
    @BindView(R.id.fef_events_fertilizer)
    CheckBox fertilizerBox;
    @BindView(R.id.fef_events_transplantation)
    CheckBox transplantationBox;
    @BindView(R.id.fef_events_all)
    CheckBox eventsAllBox;

    private Unbinder unbinder;
    private FilterSelectionAdapter adapter;
    private CalendarFilter filter;

    public static EventFilterFragment newInstance() {
        return new EventFilterFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filter = FilesUtils.getCalendarFilter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_filter, container, false);
        unbinder = ButterKnife.bind(this, view);
        initList();
        reloadItems();
        elementsGroup.setOnCheckedChangeListener(this);
        if (savedInstanceState == null) {
            setupViewWithFilter();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.fef_selected_only) {
            selectedItemsList.setVisibility(View.VISIBLE);
        } else {
            selectedItemsList.setVisibility(View.INVISIBLE);
        }
    }

    @OnCheckedChanged(R.id.fef_events_all)
    void onEventsAllCheckChanged(CompoundButton button, boolean isChecked) {
        if (isChecked) {
            createdBox.setChecked(true);
            wateringBox.setChecked(true);
            fertilizerBox.setChecked(true);
            transplantationBox.setChecked(true);
        }
    }

    @OnCheckedChanged({R.id.fef_events_fertilizer, R.id.fef_events_transplantation,
            R.id.fef_events_watering, R.id.fef_events_created})
    void onEventsCheckChanged(CompoundButton button, boolean isChecked) {
        if (!isChecked) {
            eventsAllBox.setChecked(false);
        }
    }

    @OnClick(R.id.fef_filter)
    void onFilterClicked() {
        saveFilterData();
        setResult();
        getActivity().onBackPressed();
    }

    private void setupViewWithFilter() {
        int checkedId = R.id.fef_elements_all;
        switch (filter.getElementsFilterType()) {
            case FilterElements.FLOWERS:
                checkedId = R.id.fef_flowers_only;
                break;
            case FilterElements.GROUPS:
                checkedId = R.id.fef_groups_only;
                break;
            case FilterElements.SELECTED:
                checkedId = R.id.fef_selected_only;
                break;
        }

        elementsGroup.check(checkedId);

        if (filter.getSelectedEventTypes() != null && filter.getSelectedEventTypes().size() > 0) {
            for (int eventType : filter.getSelectedEventTypes()) {
                switch (eventType) {
                    case NotificationType.CREATED:
                        createdBox.setChecked(true);
                        break;
                    case NotificationType.WATERING:
                        wateringBox.setChecked(true);
                        break;
                    case NotificationType.FERTILIZER:
                        fertilizerBox.setChecked(true);
                        break;
                    case NotificationType.TRANSPLANTING:
                        transplantationBox.setChecked(true);
                        break;
                }
            }
        } else {
            eventsAllBox.setChecked(true);
        }

        adapter.setSelection(filter.getSelectedElements());
    }

    private void setResult() {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment == null) {
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_FILTER, filter);
        targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
    }

    private void reloadItems() {
        FlowersProvider provider = new FlowersProvider(getActivity());

        List<Object> allItems = new ArrayList<>();
        List<Group> groups = provider.getAllGroups();
        if (groups != null) {
            allItems.addAll(groups);
        }

        List<Flower> flowers = provider.getAllFlowers();
        if (flowers != null) {
            allItems.addAll(flowers);
        }

        provider.unbind();

        adapter.setItems(allItems);
    }

    private void saveFilterData() {
        setElementsFilterResult();
        setEventsFilterResult();
        if (filter.getElementsFilterType() == FilterElements.SELECTED) {
            filter.setSelectedFlowers(adapter.getSelectedFlowers());
            filter.setSelectedGroups(adapter.getSelectedGroups());
        } else {
            filter.clearSelectedItems();
        }

        FilesUtils.saveCalendarFilter(filter);
    }

    private void setEventsFilterResult() {
        if (!eventsAllBox.isChecked()) {
            ArrayList<Integer> filteredTypes = new ArrayList<>();
            if (wateringBox.isChecked()) {
                filteredTypes.add(NotificationType.WATERING);
            }
            if (createdBox.isChecked()) {
                filteredTypes.add(NotificationType.CREATED);
            }
            if (fertilizerBox.isChecked()) {
                filteredTypes.add(NotificationType.FERTILIZER);
            }
            if (transplantationBox.isChecked()) {
                filteredTypes.add(NotificationType.TRANSPLANTING);
            }

            filter.setSelectedEventTypes(filteredTypes);
        } else {
            filter.setSelectedEventTypes(new ArrayList<Integer>());
        }
    }

    private void setElementsFilterResult() {
        switch (elementsGroup.getCheckedRadioButtonId()) {
            case R.id.fef_elements_all:
                filter.setElementsFilterType(FilterElements.NONE);
                break;
            case R.id.fef_flowers_only:
                filter.setElementsFilterType(FilterElements.FLOWERS);
                break;
            case R.id.fef_groups_only:
                filter.setElementsFilterType(FilterElements.GROUPS);
                break;
            case R.id.fef_selected_only:
                filter.setElementsFilterType(FilterElements.SELECTED);
                break;
        }
    }

    private void initList() {
        if (adapter == null) {
            adapter = new FilterSelectionAdapter();
        }

        selectedItemsList.setAdapter(adapter);
        selectedItemsList.addItemDecoration(new SpaceItemDecoration(Res.getDimensionPixelSize(R.dimen.margin_normal)));
    }
}
