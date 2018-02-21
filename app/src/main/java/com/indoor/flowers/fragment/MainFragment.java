package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.evgeniysharafan.utils.Fragments;
import com.indoor.flowers.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainFragment extends Fragment implements OnNavigationItemSelectedListener {

    @BindView(R.id.fm_bottom_bar)
    BottomNavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Unbinder unbinder;

    private int selectedItemId = -1;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        navigationView.setOnNavigationItemSelectedListener(this);
        setupToolbar();
        if (selectedItemId == -1) {
            navigateToItem(R.id.mnd_notifications);
        } else {
            navigateToItem(selectedItemId);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        removeToolbar();
        super.onDestroyView();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navigateToItem(item.getItemId());
        return true;
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

    private void navigateToItem(int itemId) {
        Fragment fragment = null;
        switch (itemId) {
            case R.id.mnd_flowers:
                fragment = FlowersFragment.newInstance();
                break;
            case R.id.mnd_groups:
                fragment = GroupsFragment.newInstance();
                break;
            case R.id.mnd_calendar:
                fragment = EventsCalendarFragment.newInstance();
                break;
            case R.id.mnd_notifications:
                fragment = NotificationsFragment.newInstance();
                break;
        }

        if (fragment != null) {
            selectedItemId = itemId;
            Fragments.replace(getFragmentManager(), R.id.fm_content, fragment, null, false);
        }
    }
}
