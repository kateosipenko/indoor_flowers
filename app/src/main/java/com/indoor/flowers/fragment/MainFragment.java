package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.evgeniysharafan.utils.Fragments;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.MainPagerAdapter;
import com.indoor.flowers.util.DialogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainFragment extends Fragment implements OnPageChangeListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fm_tabs)
    TabLayout tabLayout;
    @BindView(R.id.fm_pager)
    ViewPager pager;
    @BindView(R.id.fm_fab)
    FloatingActionButton floatingActionButton;

    private Unbinder unbinder;
    private MainPagerAdapter adapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupToolbar();
        setupPager();
        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        removeToolbar();
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mm_calendar) {
            Fragments.replace(getFragmentManager(), android.R.id.content,
                    EventsCalendarFragment.newInstance(), null, true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        floatingActionButton.setVisibility(adapter.canShowFloatingButton(position)
                ? View.VISIBLE : View.GONE);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @OnClick(R.id.fm_fab)
    void onFloatingButtonClicked() {
        if (pager.getCurrentItem() == MainPagerAdapter.POSITION_FLOWERS) {
            DialogUtils.showCreateFlowerDialog(getActivity(), getFragmentManager());
        } else if (pager.getCurrentItem() == MainPagerAdapter.POSITION_GROUPS) {
            DialogUtils.showCreateGroupDialog(getActivity(), getFragmentManager());
        }
    }

    private void setupPager() {
        if (adapter == null) {
            adapter = new MainPagerAdapter(getChildFragmentManager());
        }

        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(this);
    }

    private void removeToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(null);
        }
    }

    private void setupToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            toolbar.setTitle(R.string.app_name);
            activity.setSupportActionBar(toolbar);
        }
    }
}
