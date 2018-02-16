package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.evgeniysharafan.utils.Fragments;
import com.evgeniysharafan.utils.Res;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.FlowersAdapter;
import com.indoor.flowers.adapter.GroupsAdapter;
import com.indoor.flowers.adapter.GroupsAdapter.GroupClickListener;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.util.AnimationUtils;
import com.indoor.flowers.util.OnItemClickListener;
import com.indoor.flowers.util.SpaceItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainFragment extends Fragment implements GroupClickListener,
        OnItemClickListener<Flower> {

    @BindView(R.id.fm_root)
    ConstraintLayout rootLayout;

    @BindView(R.id.fm_flowers_list)
    RecyclerView flowersList;
    @BindView(R.id.fm_groups_list)
    RecyclerView groupsList;
    @BindView(R.id.fm_fab_menu)
    FloatingActionsMenu fabMenu;

    private Unbinder unbinder;

    private FlowersProvider provider;

    private GroupsAdapter groupsAdapter;
    private FlowersAdapter flowersAdapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupMainTransitions();
        provider = new FlowersProvider(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
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
        fabMenu.collapse();

        Group selected = groupsAdapter.getSelectedGroup();
        AddFlowerFragment fragment = selected == null ? AddFlowerFragment.newInstance()
                : AddFlowerFragment.newInstance(selected.getId());

        Fragments.replace(getFragmentManager(), android.R.id.content,
                fragment, null, true);
    }

    @OnClick(R.id.fm_add_group)
    void onAddGroupClicked() {
        fabMenu.collapse();

        Fragments.replace(getFragmentManager(), android.R.id.content,
                CreateGroupFragment.newInstance(), null, true);
    }

    @Override
    public void onGroupClicked(Group group) {
        reloadFlowers();
    }

    @Override
    public void onItemClicked(Flower item) {
        Fragments.replace(getFragmentManager(), android.R.id.content,
                FlowerDetailsFragment.newInstance(item.getId()), null, true);
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
        groupsList.addItemDecoration(new SpaceItemDecoration(0, Res.getDimensionPixelSize(R.dimen.margin_small)));
        groupsList.setAdapter(groupsAdapter);
    }

    private void initFlowersList() {
        if (flowersAdapter == null) {
            flowersAdapter = new FlowersAdapter();
        }

        flowersAdapter.setFlowerClickListener(this);

        int space = Res.getDimensionPixelSize(R.dimen.margin_normal);
        flowersList.addItemDecoration(new SpaceItemDecoration(space, space));
        flowersList.setAdapter(flowersAdapter);
    }

    private void setupMainTransitions() {
        TransitionSet set = new TransitionSet();
        set.addTransition(new Slide(AnimationUtils.getGravityDirection(Gravity.START))
                .addTarget(R.id.fm_flowers_list));
        set.addTransition(new Slide(Gravity.BOTTOM)
                .addTarget(R.id.fm_fab_menu));
        set.addTransition(new Slide(Gravity.TOP)
                .addTarget(R.id.fm_groups_list));
        set.setDuration(300);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        setEnterTransition(set);
        setReenterTransition(set);
        setReturnTransition(set);
        setExitTransition(set);
    }
}
