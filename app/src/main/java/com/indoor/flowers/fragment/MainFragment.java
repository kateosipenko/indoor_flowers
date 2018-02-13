package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evgeniysharafan.utils.Fragments;
import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.FlowersAdapter;
import com.indoor.flowers.adapter.GroupsAdapter;
import com.indoor.flowers.adapter.GroupsAdapter.GroupClickListener;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.util.SpaceItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainFragment extends Fragment implements GroupClickListener {

    @BindView(R.id.fm_root)
    ConstraintLayout rootLayout;

    @BindView(R.id.fm_flowers_list)
    RecyclerView flowersList;
    @BindView(R.id.fm_groups_list)
    RecyclerView groupsList;

    private Unbinder unbinder;

    private FlowersProvider provider;

    private GroupsAdapter groupsAdapter;
    private FlowersAdapter flowersAdapter;

    private ConstraintSet openFabSet = new ConstraintSet();
    private ConstraintSet closeFabSet = new ConstraintSet();

    private boolean isFabMenuVisible = false;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provider = new FlowersProvider(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        createFabSets();
        initGroupsList();
        initFlowersList();
        reloadGroups();
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
        provider.unbind();
        super.onDestroy();
    }

    @OnClick(R.id.fm_add_flower)
    void onAddFlowerClicked() {
        Group selected = groupsAdapter.getSelectedGroup();
        AddFlowerFragment fragment = selected == null ? AddFlowerFragment.newInstance()
                : AddFlowerFragment.newInstance(selected.getId());

        Fragments.replace(getFragmentManager(), android.R.id.content,
                fragment, null, true);
    }

    @OnClick(R.id.fm_add_group)
    void onAddGroupClicked() {
        Fragments.replace(getFragmentManager(), android.R.id.content,
                CreateGroupFragment.newInstance(), null, true);
    }

    @Override
    public void onGroupClicked(Group group) {
        reloadFlowers();
    }

    @OnClick(R.id.fm_fab_menu)
    void onFabMenuClicked() {
        TransitionManager.beginDelayedTransition(rootLayout);
        if (isFabMenuVisible) {
            isFabMenuVisible = false;
            closeFabSet.applyTo(rootLayout);
        } else {
            isFabMenuVisible = true;
            openFabSet.applyTo(rootLayout);
        }
    }

    private void reloadFlowers() {
        Group selectedGroup = groupsAdapter.getSelectedGroup();
        List<Flower> flowers = null;
        if (selectedGroup != null) {
            flowers = provider.getFlowersForGroup(selectedGroup.getId());
        } else {
            flowers = provider.getFlowersWithoutGroup();
        }

        flowersAdapter.setFlowers(flowers);
    }

    private void reloadGroups() {
        List<Group> groups = provider.getAllGroups();
        groupsAdapter.setGroups(groups);
    }

    private void initGroupsList() {
        if (groupsAdapter == null) {
            groupsAdapter = new GroupsAdapter();
        }

        groupsAdapter.setListener(this);
        groupsList.addItemDecoration(new SpaceItemDecoration(0, Res.getDimensionPixelSize(R.dimen.padding_small)));
        groupsList.setAdapter(groupsAdapter);
    }

    private void initFlowersList() {
        if (flowersAdapter == null) {
            flowersAdapter = new FlowersAdapter();
        }

        int space = Res.getDimensionPixelSize(R.dimen.padding_normal);
        flowersList.addItemDecoration(new SpaceItemDecoration(space, space));
        flowersList.setAdapter(flowersAdapter);
    }

    private void createFabSets() {
        closeFabSet.clone(rootLayout);

        openFabSet.clone(rootLayout);
        openFabSet.setVisibility(R.id.fm_add_flower, ConstraintSet.VISIBLE);
        openFabSet.setVisibility(R.id.fm_add_group, ConstraintSet.VISIBLE);
        openFabSet.connect(R.id.fm_add_group, ConstraintSet.BOTTOM, R.id.fm_fab_menu, ConstraintSet.TOP);
        openFabSet.connect(R.id.fm_add_flower, ConstraintSet.BOTTOM, R.id.fm_add_group, ConstraintSet.TOP);
//        openFabSet.setRotationX(R.id.fm_fab_menu, 90);
    }
}
