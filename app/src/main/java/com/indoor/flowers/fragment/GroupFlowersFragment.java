package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.indoor.flowers.R;
import com.indoor.flowers.adapter.FlowersAdapter;
import com.indoor.flowers.adapter.FlowersAdapter.FlowersSelectionListener;
import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Flower;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GroupFlowersFragment extends Fragment implements FlowersSelectionListener {

    private static final String KEY_GROUP_ID = "key_group_id";

    @BindView(R.id.fl_list)
    RecyclerView listView;

    private Unbinder unbinder;
    private FlowersAdapter flowersAdapter;

    private FlowersProvider provider;

    public static GroupFlowersFragment newInstance(long groupId) {
        Bundle args = new Bundle();
        args.putLong(KEY_GROUP_ID, groupId);

        GroupFlowersFragment fragment = new GroupFlowersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provider = new FlowersProvider(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (flowersAdapter == null) {
            flowersAdapter = new FlowersAdapter();
        }

        flowersAdapter.setSelectionMode(true);
        flowersAdapter.setSelectionListener(this);
        listView.setAdapter(flowersAdapter);
        reloadFlowers();
        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onSelectedFlowersChanged(List<Flower> selectedFlowers) {
        provider.refreshGroupFlowers(getGroupId(), selectedFlowers);
    }

    private void reloadFlowers() {
        List<Flower> flowers = provider.getAllFlowers();
        List<Flower> selectedFlowers = provider.getFlowersForGroup(getGroupId());
        flowersAdapter.setItems(flowers);
        flowersAdapter.setSelectedFlowers(selectedFlowers);
    }

    private long getGroupId() {
        Bundle args = getArguments();
        return args != null && args.containsKey(KEY_GROUP_ID)
                ? args.getLong(KEY_GROUP_ID, DatabaseProvider.DEFAULT_ID)
                : DatabaseProvider.DEFAULT_ID;
    }
}
