package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evgeniysharafan.utils.Fragments;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.FlowersAdapter;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.util.EmptyDataUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FlowersFragment extends Fragment {

    @BindView(R.id.ff_flowers_list)
    RecyclerView flowersList;
    @BindView(R.id.ff_empty_text)
    TextView emptyTextView;

    private FlowersAdapter adapter;
    private FlowersProvider provider;

    private Unbinder unbinder;

    public static FlowersFragment newInstance() {
        return new FlowersFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        provider.unbind();
        super.onDestroy();
    }

    @OnClick(R.id.ff_add_flower)
    public void onAddFlowerClicked() {
        Fragments.replace(getFragmentManager(), android.R.id.content,
                AddFlowerFragment.newInstance(), null, true);
    }

    private void reloadItems() {
        List<Flower> flowers = provider.getAllFlowers();
        adapter.setFlowers(flowers);
    }

    private void initList() {
        if (adapter == null) {
            adapter = new FlowersAdapter();
        }

        flowersList.setAdapter(adapter);
        flowersList.setLayoutManager(new LinearLayoutManager(getActivity()));

        EmptyDataUtil emptyDataUtil = new EmptyDataUtil();
        emptyDataUtil.attachToList(flowersList);
        emptyDataUtil.setEmptyTextLayout(emptyTextView);
    }
}
