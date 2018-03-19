package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evgeniysharafan.utils.Fragments;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.GroupsAdapter;
import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.util.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FlowerGroupsFragment extends Fragment implements OnItemClickListener<Group> {

    private static final String KEY_FLOWER_ID = "key_flower_id";

    @BindView(R.id.fl_list)
    RecyclerView listView;

    private Unbinder unbinder;
    private GroupsAdapter groupsAdapter;

    private FlowersProvider flowerProvider;

    public static FlowerGroupsFragment newInstance(long flowerId) {
        Bundle args = new Bundle();
        args.putLong(KEY_FLOWER_ID, flowerId);

        FlowerGroupsFragment fragment = new FlowerGroupsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flowerProvider = new FlowersProvider(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (groupsAdapter == null) {
            groupsAdapter = new GroupsAdapter();
        }

        groupsAdapter.setListener(this);
        listView.setAdapter(groupsAdapter);
        reloadGroups();
        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onItemClicked(Group item) {
        Fragments.replace(getNavigationFragmentManager(), android.R.id.content,
                GroupFragment.newInstance(item.getId()), null, true);
    }

    @OnClick(R.id.fl_action_add)
    void onAddToGroupClicked() {
        Fragments.replace(getNavigationFragmentManager(), android.R.id.content,
                GroupFragment.newInstanceForFlower(getFlowerId()),
                null, true);
    }

    private FragmentManager getNavigationFragmentManager() {
        return getParentFragment() != null ? getParentFragment().getFragmentManager()
                : getFragmentManager();
    }

    private void reloadGroups() {
        List<Group> groups = flowerProvider.getGroupsForFlower(getFlowerId());
        groupsAdapter.setItems(groups);
    }

    private long getFlowerId() {
        Bundle args = getArguments();
        return args != null && args.containsKey(KEY_FLOWER_ID)
                ? args.getLong(KEY_FLOWER_ID, DatabaseProvider.DEFAULT_ID)
                : DatabaseProvider.DEFAULT_ID;
    }
}
