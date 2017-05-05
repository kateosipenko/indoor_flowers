package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.indoor.flowers.R;
import com.indoor.flowers.adapter.AddFlowerPagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddFlowerFragment extends Fragment {

    @Bind(R.id.faf_pager)
    ViewPager pager;
    @Bind(R.id.faf_tabs)
    TabLayout tabs;

    private AddFlowerPagerAdapter adapter;

    public static AddFlowerFragment newInstance() {
        return new AddFlowerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_flower, container, false);
        ButterKnife.bind(this, view);
        setupPager();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void setupPager() {
        if (adapter == null) {
            adapter = new AddFlowerPagerAdapter();
        }

        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
    }
}
