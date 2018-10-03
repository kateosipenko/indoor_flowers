package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.evgeniysharafan.utils.Fragments;
import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.FlowersWithWateringAdapter;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.FlowerWithWatering;
import com.indoor.flowers.util.OnItemClickListener;
import com.indoor.flowers.util.Prefs;
import com.indoor.flowers.util.SpaceItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FlowersFragment extends Fragment implements OnItemClickListener<FlowerWithWatering> {

    @BindView(R.id.ff_flowers_list)
    RecyclerView flowersList;

    private FlowersWithWateringAdapter adapter;
    private FlowersProvider provider;

    private Unbinder unbinder;

    private boolean hideGroupFlowers;

    public static FlowersFragment newInstance() {
        return new FlowersFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        provider = new FlowersProvider(getActivity());
        hideGroupFlowers = Prefs.getHideFlowersWithGroups();
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
                hideGroupFlowers = true;
                reloadItems();
                break;
            case R.id.mf_filter_without_group:
                hideGroupFlowers = false;
                reloadItems();
                break;
        }

        Prefs.setHideFlowersWithGroup(hideGroupFlowers);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(FlowerWithWatering item) {
        FragmentManager manager = getParentFragment() != null
                ? getParentFragment().getFragmentManager() : getFragmentManager();
        if (manager != null) {
            Fragment fragment = FlowerFragment.newInstance(item.getFlower().getId());
            RecyclerView.ViewHolder holder = flowersList.findViewHolderForAdapterPosition(adapter.getItemPosition(item));
            if (holder != null && holder.itemView != null) {
                manager.beginTransaction()
                        .addSharedElement(holder.itemView, holder.itemView.getTransitionName())
                        .replace(android.R.id.content, fragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Fragments.replace(manager, android.R.id.content, fragment, null, true);
            }
        }
    }

    private void reloadItems() {
        List<FlowerWithWatering> flowers = null;
        if (hideGroupFlowers) {
            flowers = provider.getAllFlowersWithWatering();
        } else {
            flowers = provider.getFlowersWithoutGroupWithWatering();
        }

        adapter.setItems(flowers);
    }

    private void initList() {
        if (adapter == null) {
            adapter = new FlowersWithWateringAdapter();
        }

        adapter.setListener(this);
        flowersList.setAdapter(adapter);
        flowersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        flowersList.addItemDecoration(new SpaceItemDecoration(Res.getDimensionPixelSize(R.dimen.margin_normal)));
    }
}
