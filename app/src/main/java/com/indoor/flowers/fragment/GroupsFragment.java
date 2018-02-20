package com.indoor.flowers.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evgeniysharafan.utils.Fragments;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.GroupsAdapter;
import com.indoor.flowers.adapter.GroupsAdapter.GroupClickListener;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Group;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class GroupsFragment extends Fragment implements GroupClickListener {

    public static final String KEY_SELECTED_GROUP = "key_selected_group";

    @BindView(R.id.fr_groups_list)
    RecyclerView groupsList;

    private Unbinder unbinder;

    private GroupsAdapter adapter;
    private FlowersProvider provider;

    public static GroupsFragment newInstance() {
        return new GroupsFragment();
    }

    public static GroupsFragment newInstance(Fragment targetFragment, int requestCode) {
        GroupsFragment fragment = new GroupsFragment();
        fragment.setTargetFragment(targetFragment, requestCode);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provider = new FlowersProvider(getActivity());
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupActionBar();
        initList();
        reloadGroups();
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

    @OnClick(R.id.fr_create_group)
    void onCreateGroupClicked() {
        Fragments.replace(getFragmentManager(), android.R.id.content, GroupFragment.newInstance(),
                null, true);
    }

    @Override
    public void onGroupClicked(Group group) {
        Fragments.replace(getFragmentManager(), android.R.id.content,
                GroupFragment.newInstance(group.getId()), null, true);
    }

    private void setupActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setTitle(R.string.mnd_groups);
    }

    private void reloadGroups() {
        List<Group> allGroups = provider.getAllGroups();
        adapter.setGroups(allGroups);
    }

    private void setActivityResult(Group group) {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment == null) {
            return;
        }

        Intent data = new Intent();
        data.putExtra(KEY_SELECTED_GROUP, group.getId());
        targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
    }

    private void initList() {
        if (adapter == null) {
            adapter = new GroupsAdapter();
        }

        adapter.setListener(this);
        groupsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupsList.setAdapter(adapter);
    }
}
