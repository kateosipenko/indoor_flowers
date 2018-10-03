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
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.evgeniysharafan.utils.Fragments;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.MainPagerAdapter;
import com.indoor.flowers.util.AnimationUtils;
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
        setupTransitions();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupToolbar();
        setupPager();
        onPageSelected(pager.getCurrentItem());
        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        removeToolbar();
        super.onDestroyView();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case MainPagerAdapter.POSITION_NOTIFICATIONS:
                floatingActionButton.setImageResource(R.drawable.ic_filter);
                break;
            default:
                floatingActionButton.setImageResource(R.drawable.ic_add);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @OnClick(R.id.fm_fab)
    void onFloatingButtonClicked() {
        switch (pager.getCurrentItem()) {
            case MainPagerAdapter.POSITION_FLOWERS:
                DialogUtils.showCreateFlowerDialog(getActivity(), getFragmentManager());
                break;
            case MainPagerAdapter.POSITION_GROUPS:
                DialogUtils.showCreateGroupDialog(getActivity(), getFragmentManager());
                break;
            case MainPagerAdapter.POSITION_NOTIFICATIONS:
                Fragments.replace(getFragmentManager(), android.R.id.content,
                        EventFilterFragment.newInstance(), null, true);
                break;
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

    private void setupTransitions() {
        TransitionSet enterTransition = new TransitionSet();
        enterTransition.addTransition(new Slide(Gravity.TOP)
                .addTarget(R.id.toolbar));
        enterTransition.addTransition(new Slide(Gravity.TOP)
                .addTarget(R.id.fm_tabs));
        enterTransition.addTransition(new Slide(AnimationUtils.getGravityDirection(Gravity.START))
                .addTarget(R.id.fm_pager));
        enterTransition.addTransition(new Fade()
                .addTarget(R.id.fm_fab));
        enterTransition.setDuration(AnimationUtils.TRANSITION_DURATION);
        enterTransition.setInterpolator(new AccelerateDecelerateInterpolator());
        setEnterTransition(enterTransition);
        setExitTransition(enterTransition);

        TransitionSet reenterTransition = enterTransition.clone();
        reenterTransition.setStartDelay(AnimationUtils.TRANSITION_DELAY);
        setReenterTransition(reenterTransition);
        setReturnTransition(reenterTransition);
    }
}
