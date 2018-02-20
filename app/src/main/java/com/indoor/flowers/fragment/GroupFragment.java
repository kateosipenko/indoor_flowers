package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.evgeniysharafan.utils.Fragments;
import com.evgeniysharafan.utils.Toasts;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.EventsAdapter;
import com.indoor.flowers.adapter.FlowersAdapter;
import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Event;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.util.FlowersAlarmsUtils;
import com.indoor.flowers.util.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

public class GroupFragment extends Fragment implements OnItemClickListener<Event> {

    private static final String KEY_GROUP_ID = "key_group_id";

    @BindView(R.id.fg_title)
    EditText nameView;
    @BindView(R.id.fg_events_list)
    RecyclerView eventsListView;
    @BindView(R.id.fg_add_event)
    Button addEventButton;
    @BindView(R.id.fg_flowers_list)
    RecyclerView flowersList;
    @BindView(R.id.fg_delete)
    Button deleteButton;

    private FlowersProvider flowersProvider;
    private Unbinder unbinder;

    private Group group;
    private FlowersAdapter flowersAdapter;
    private EventsAdapter eventsAdapter;

    public static GroupFragment newInstance() {
        return new GroupFragment();
    }

    public static GroupFragment newInstance(long groupId) {
        Bundle args = new Bundle();
        args.putLong(KEY_GROUP_ID, groupId);

        GroupFragment fragment = new GroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flowersProvider = new FlowersProvider(getActivity());
        long groupId = getArguments() != null && getArguments().containsKey(KEY_GROUP_ID)
                ? getArguments().getLong(KEY_GROUP_ID, DatabaseProvider.DEFAULT_ID)
                : DatabaseProvider.DEFAULT_ID;
        group = flowersProvider.getGroupById(groupId);
        if (group == null) {
            group = new Group();
            group.setId(DatabaseProvider.DEFAULT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        unbinder = ButterKnife.bind(this, view);
        initFlowersList();
        initEventsList();
        refreshViewWithGroup();
        reloadEvents();
        reloadFlowers();
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

    @OnTextChanged(R.id.fg_title)
    void onNameTextChanged(CharSequence s, int start, int before, int count) {
        group.setName(nameView.getText().toString());
    }

    @OnClick(R.id.fg_save)
    void onSaveClick() {
        if (TextUtils.isEmpty(group.getName())) {
            Toasts.showLong(R.string.fcr_name_group_error);
            return;
        }

        flowersProvider.createOrUpdateGroup(group, flowersAdapter.getSelectedFlowers());
        refreshDeleteButtonVisibility();
        refreshEventsVisilibty();
        reloadEvents();
    }

    @OnClick(R.id.fg_delete)
    void onDeleteClick() {
        FlowersAlarmsUtils.deleteAlarmsForEvents(getActivity(),
                flowersProvider.getEventsForTarget(group.getId(), Group.TABLE_NAME));
        flowersProvider.deleteGroup(group);
        getActivity().onBackPressed();
    }

    @OnClick(R.id.fg_add_event)
    void onAddEventClicked() {
        Fragments.replace(getFragmentManager(), android.R.id.content,
                EventFragment.newInstance(group.getId(), Group.TABLE_NAME), null, true);
    }

    @Override
    public void onItemClicked(Event item) {
        Fragments.replace(getFragmentManager(), android.R.id.content,
                EventFragment.newInstance(item.getId()), null, true);
    }

    private void refreshViewWithGroup() {
        if (nameView == null) {
            return;
        }

        nameView.setText(group.getName());
        refreshDeleteButtonVisibility();
        refreshEventsVisilibty();
    }

    private void refreshEventsVisilibty() {
        if (eventsListView == null) {
            return;
        }

        if (group.getId() == DatabaseProvider.DEFAULT_ID) {
            eventsListView.setVisibility(View.GONE);
            addEventButton.setVisibility(View.GONE);
        } else {
            eventsListView.setVisibility(View.VISIBLE);
            addEventButton.setVisibility(View.VISIBLE);
        }
    }

    private void refreshDeleteButtonVisibility() {
        if (deleteButton == null) {
            return;
        }

        deleteButton.setVisibility(group.getId() == DatabaseProvider.DEFAULT_ID ? View.GONE : View.VISIBLE);
    }

    private void initEventsList() {
        if (eventsAdapter == null) {
            eventsAdapter = new EventsAdapter();
        }

        eventsAdapter.setListener(this);
        eventsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        eventsListView.setAdapter(eventsAdapter);
    }

    private void initFlowersList() {
        if (flowersAdapter == null) {
            flowersAdapter = new FlowersAdapter();
        }

        flowersAdapter.setSelectionMode(true);
        flowersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        flowersList.setAdapter(flowersAdapter);
    }

    private void reloadEvents() {
        if (eventsListView == null) {
            return;
        }

        List<Event> events = flowersProvider.getEventsForTarget(group.getId(), Group.TABLE_NAME);
        eventsAdapter.setItems(events);
    }

    private void reloadFlowers() {
        if (flowersList == null) {
            return;
        }

        List<Flower> allFlowers = flowersProvider.getAllFlowers();
        flowersAdapter.setItems(allFlowers);

        List<Flower> flowers = flowersProvider.getFlowersForGroup(group.getId());
        flowersAdapter.setSelectedFlowers(flowers);
    }
}
