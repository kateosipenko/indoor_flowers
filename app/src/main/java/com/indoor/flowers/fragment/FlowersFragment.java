package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.evgeniysharafan.utils.Fragments;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.FlowersAdapter;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.util.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FlowersFragment extends Fragment implements OnItemClickListener<Flower> {

    @BindView(R.id.ff_flowers_list)
    RecyclerView flowersList;

    private FlowersAdapter adapter;
    private FlowersProvider provider;

    private Unbinder unbinder;

    private boolean showAllFlowers = false;

    public static FlowersFragment newInstance() {
        return new FlowersFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        provider = new FlowersProvider(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flowers, container, false);
        unbinder = ButterKnife.bind(this, view);
        initList();
        reloadItems();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadItems();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_flowers, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mf_filter_all_flowers:
                showAllFlowers = true;
                reloadItems();
                break;
            case R.id.mf_filter_without_group:
                showAllFlowers = false;
                reloadItems();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.ff_add_flower)
    public void onAddFlowerClicked() {
        Fragments.replace(getFragmentManager(), android.R.id.content,
                FlowerFragment.newInstance(), null, true);
    }

    @Override
    public void onItemClicked(Flower item) {
        Fragments.replace(getFragmentManager(), android.R.id.content,
                FlowerFragment.newInstance(item.getId()), null, true);
    }

    private void reloadItems() {
        List<Flower> flowers = null;
        if (showAllFlowers) {
            flowers = provider.getAllFlowers();
        } else {
            flowers = provider.getFlowersWithoutGroup();
        }

        adapter.setItems(flowers);
    }

    private void initList() {
        if (adapter == null) {
            adapter = new FlowersAdapter();
        }

        adapter.setListener(this);
        flowersList.setAdapter(adapter);
        flowersList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
