package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evgeniysharafan.utils.Fragments;
import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.GroupsAdapter;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.util.OnItemClickListener;
import com.indoor.flowers.util.SpaceItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GroupsFragment extends Fragment implements OnItemClickListener<Group> {

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

    @Override
    public void onItemClicked(Group group) {
        FragmentManager manager = getParentFragment() != null
                ? getParentFragment().getFragmentManager() : getFragmentManager();
        if (manager != null) {
            Fragments.replace(manager, android.R.id.content,
                    GroupFragment.newInstance(group.getId()), null, true);
        }
    }

    private void reloadGroups() {
        List<Group> allGroups = provider.getAllGroups();
        adapter.setItems(allGroups);
    }

    private void initList() {
        if (adapter == null) {
            adapter = new GroupsAdapter();
        }

        adapter.setListener(this);
        groupsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupsList.setAdapter(adapter);
        groupsList.addItemDecoration(new SpaceItemDecoration(Res.getDimensionPixelSize(R.dimen.margin_normal)));
    }
}
