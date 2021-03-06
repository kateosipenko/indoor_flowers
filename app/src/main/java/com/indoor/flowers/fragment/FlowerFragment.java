package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.evgeniysharafan.utils.Fragments;
import com.indoor.flowers.R;
import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.database.provider.NotificationsProvider;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.transition.FlowerSharedTransition;
import com.indoor.flowers.util.AnimationUtils;
import com.indoor.flowers.util.FilesUtils;
import com.indoor.flowers.util.FlowersAlarmsUtils;
import com.indoor.flowers.view.NameView;
import com.indoor.flowers.view.NameView.NameChangeListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FlowerFragment extends Fragment implements OnNavigationItemSelectedListener,
        NameChangeListener {

    private static final String KEY_FLOWER_ID = "key_flower_id";

    @BindView(R.id.ff_bottom_menu)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.ff_toolbar)
    Toolbar toolbar;
    @BindView(R.id.ff_name)
    NameView nameView;
    @BindView(R.id.ff_container)
    ViewGroup dataContainer;

    FlowersProvider provider;

    private Flower flower;
    private Unbinder unbinder;

    public static FlowerFragment newInstance(long flowerId) {
        Bundle args = new Bundle();
        args.putLong(KEY_FLOWER_ID, flowerId);
        FlowerFragment fragment = new FlowerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTransitions();

        setSharedElementEnterTransition(new FlowerSharedTransition(false)
                .setDuration(5000));
        setSharedElementReturnTransition(new FlowerSharedTransition(true)
                .setDuration(5000));

        setHasOptionsMenu(true);
        provider = new FlowersProvider(getActivity());
        long flowerId = getFlowerIdFromArgs();
        flower = provider.getFlowerById(flowerId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flower, container, false);
        unbinder = ButterKnife.bind(this, view);

        ViewCompat.setTransitionName(dataContainer, flower.getName());

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        nameView.setListener(this);
        setupActionBar();
        refreshName();
        if (savedInstanceState == null) {
            navigateToMenuItem(bottomNavigationView.getSelectedItemId());
        }
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if (flower != null && flower.getId() != DatabaseProvider.DEFAULT_ID) {
            inflater.inflate(R.menu.menu_flower, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mf_delete) {
            NotificationsProvider notificationsProvider = new NotificationsProvider(getActivity());
            FlowersAlarmsUtils.deleteAlarmsForEvents(getActivity(),
                    notificationsProvider.getEventsForTarget(flower.getId(), Flower.TABLE_NAME));
            FilesUtils.deleteDataForTarget(FilesUtils.DataPart.FLOWERS, flower.getId());
            provider.deleteFlower(flower, true);
            getActivity().onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navigateToMenuItem(item.getItemId());
        return true;
    }

    @Override
    public void onNameChanged(String name) {
        if (!TextUtils.isEmpty(name) && !name.equals(flower.getName())) {
            flower.setName(name);
            provider.updateFlower(flower);
        }
    }

    void navigateToMenuItem(int itemId) {
        Fragment fragment = null;
        switch (itemId) {
            case R.id.mfb_status:
                fragment = FlowerProfileFragment.newInstance(flower.getId());
                break;
            case R.id.mfb_gallery:
                fragment = TargetGalleryFragment.newInstance(flower.getId(), Flower.TABLE_NAME);
                break;
            case R.id.mfb_notifications:
                fragment = TargetNotificationsFragment.newInstance(flower.getId(), Flower.TABLE_NAME);
                break;
            case R.id.mfb_groups:
                fragment = FlowerGroupsFragment.newInstance(flower.getId());
                break;
        }

        if (fragment != null) {
            Fragments.replace(getChildFragmentManager(), R.id.ff_container, fragment, null);
        }
    }

    void refreshName() {
        if (nameView == null) {
            return;
        }

        nameView.setText(flower.getName());
        if (flower.getId() == DatabaseProvider.DEFAULT_ID) {
            nameView.startEditing();
        }
    }

    private void setupActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(null);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    private long getFlowerIdFromArgs() {
        return getArguments() != null && getArguments().containsKey(KEY_FLOWER_ID)
                ? getArguments().getLong(KEY_FLOWER_ID, -1) : -1;
    }

    private void setupTransitions() {
        TransitionSet exitTransition = new TransitionSet();
        exitTransition.addTransition(new Slide(Gravity.TOP).addTarget(R.id.ff_toolbar));
        exitTransition.addTransition(new Slide(Gravity.BOTTOM).addTarget(R.id.ff_bottom_menu));
        exitTransition.setDuration(AnimationUtils.TRANSITION_DURATION);
        exitTransition.setInterpolator(new OvershootInterpolator());

        setExitTransition(exitTransition);
        setReturnTransition(exitTransition);

        TransitionSet enterTransition = exitTransition.clone();
        enterTransition.setStartDelay(AnimationUtils.TRANSITION_DELAY);

        setEnterTransition(enterTransition);
        setReenterTransition(enterTransition);
    }
}
