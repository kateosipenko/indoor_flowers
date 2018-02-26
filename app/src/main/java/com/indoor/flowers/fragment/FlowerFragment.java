package com.indoor.flowers.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.evgeniysharafan.utils.Fragments;
import com.evgeniysharafan.utils.Toasts;
import com.evgeniysharafan.utils.picasso.CircleTransformation;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.EventsAdapter;
import com.indoor.flowers.adapter.FlowerPagerAdapter;
import com.indoor.flowers.adapter.GroupsAdapter;
import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.database.provider.NotificationsProvider;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.model.Notification;
import com.indoor.flowers.util.FlowersAlarmsUtils;
import com.indoor.flowers.util.OnItemClickListener;
import com.indoor.flowers.util.PermissionHelper;
import com.indoor.flowers.util.PermissionUtil;
import com.indoor.flowers.util.TakePhotoUtils;
import com.indoor.flowers.util.TakePhotoUtils.OnPhotoTakenListener;
import com.indoor.flowers.view.NameView;
import com.indoor.flowers.view.NameView.NameChangeListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FlowerFragment extends Fragment implements OnPhotoTakenListener,
        NameChangeListener {

    private static final String KEY_FLOWER_ID = "key_flower_id";

    @BindView(R.id.ff_icon)
    ImageView imageView;
    @BindView(R.id.ff_name)
    NameView nameView;
    @BindView(R.id.faf_snackbar)
    CoordinatorLayout snackbarContainer;
    @BindView(R.id.faf_pager)
    ViewPager viewPager;
    @BindView(R.id.faf_add_event)
    View addEventButton;
    @BindView(R.id.ff_toolbar)
    Toolbar toolbar;
    @BindView(R.id.ff_tabs)
    TabLayout tabLayout;
    @BindView(R.id.faf_progress_container)
    ViewGroup progressContainer;

    private PermissionHelper permissionHelper;

    private Flower flower;
    private FlowersProvider provider;
    private NotificationsProvider notificationsProvider;

    private Unbinder unbinder;

    private FlowerPagerAdapter pagerAdapter;
    private EventsAdapter eventsAdapter;
    private GroupsAdapter groupsAdapter;

    public FlowerFragment() {
        permissionHelper = new PermissionHelper(this, 0, PermissionUtil.CAMERA_PERMISSIONS,
                new int[]{R.string.permissions_camera_required, R.string.permissions_storage_required});
    }

    public static FlowerFragment newInstance() {
        return new FlowerFragment();
    }

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
        setHasOptionsMenu(true);
        provider = new FlowersProvider(getActivity());
        notificationsProvider = new NotificationsProvider(getActivity());
        long flowerId = getFlowerIdFromArgs();
        if (flowerId != DatabaseProvider.DEFAULT_ID) {
            flower = provider.getFlowerById(flowerId);
        } else {
            flower = new Flower();
            flower.setId(DatabaseProvider.DEFAULT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flower, container, false);
        unbinder = ButterKnife.bind(this, view);
        permissionHelper.setSnackbarContainer(snackbarContainer);

        setupActionBar();
        setupViewPager();
        refreshViewWithFlower();
        reloadEvents();
        reloadGroups();

        nameView.setListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        hideProgress();
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        notificationsProvider.unbind();
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
            FlowersAlarmsUtils.deleteAlarmsForEvents(getActivity(),
                    notificationsProvider.getEventsForTarget(flower.getId(), Flower.TABLE_NAME));
            provider.deleteFlower(flower, true);
            getActivity().onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.ff_icon})
    void onChooseImageClicked() {
        if (permissionHelper.hasAllPermissions()) {
            TakePhotoUtils.getInstance().showSystemChooser(this);
        } else {
            permissionHelper.checkPermissions();
        }
    }

    @Override
    public void onNameChanged(String name) {
        if (!TextUtils.isEmpty(name) && !name.equals(flower.getName())) {
            flower.setName(name);
            provider.createOrUpdateFlower(flower);

            refreshChangeIconEnabled();
            refreshDataVisibility();
            getActivity().invalidateOptionsMenu();
        }
    }

    @OnClick(R.id.faf_add_event)
    void onAddEventClicked() {
        Fragment fragment = null;
        if (tabLayout.getSelectedTabPosition() == 0) {
            fragment = NotificationFragment.newInstance(flower.getId(), Flower.TABLE_NAME);
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            fragment = GroupFragment.newInstanceForFlower(flower.getId());
        }

        if (fragment != null) {
            Fragments.replace(getFragmentManager(), android.R.id.content, fragment, null, true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TakePhotoUtils.REQUEST_CODE_CAMERA:
            case TakePhotoUtils.REQUEST_CODE_GALLERY:
            case TakePhotoUtils.REQUEST_CODE_SYSTEM_CHOOSER:
                if (TakePhotoUtils.getInstance().isPhotoRequestOk(requestCode, resultCode)) {
                    showProgress();
                    TakePhotoUtils.getInstance().onActivityResult(requestCode, resultCode, data, this);
                }
                break;
        }
    }

    @Override
    public void onPhotoTaken(File photo) {
        flower.setImagePath(photo.getPath());
        Picasso.with(getActivity())
                .load(photo)
                .transform(new CircleTransformation(0, 0))
                .into(imageView);
        hideProgress();
        provider.updateFlower(flower);
    }

    @Override
    public void onPhotoError() {
        Toasts.showLong(R.string.photo_choose_error);
        hideProgress();
    }

    private void reloadEvents() {
        List<Notification> events = notificationsProvider.getEventsForTarget(flower.getId(), Flower.TABLE_NAME);
        eventsAdapter.setItems(events);
    }

    private void reloadGroups() {
        List<Group> groups = provider.getGroupsForFlower(flower.getId());
        groupsAdapter.setItems(groups);
    }

    private void setupViewPager() {
        if (pagerAdapter == null) {
            pagerAdapter = new FlowerPagerAdapter();
        }
        if (eventsAdapter == null) {
            eventsAdapter = new EventsAdapter();
        }
        if (groupsAdapter == null) {
            groupsAdapter = new GroupsAdapter();
        }

        eventsAdapter.setListener(new OnItemClickListener<Notification>() {
            @Override
            public void onItemClicked(Notification item) {
                Fragments.replace(getFragmentManager(), android.R.id.content,
                        NotificationFragment.newInstance(item.getId()), null, true);
            }
        });
        groupsAdapter.setListener(new OnItemClickListener<Group>() {
            @Override
            public void onItemClicked(Group item) {
                Fragments.replace(getFragmentManager(), android.R.id.content,
                        GroupFragment.newInstance(item.getId()), null, true);
            }
        });
        pagerAdapter.setAdapters(groupsAdapter, eventsAdapter);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void refreshViewWithFlower() {
        if (nameView == null) {
            return;
        }

        refreshDataVisibility();
        refreshChangeIconEnabled();
        nameView.setText(flower.getName());
        if (flower.getId() == DatabaseProvider.DEFAULT_ID) {
            nameView.startEditing();
        }
        if (!TextUtils.isEmpty(flower.getImagePath())) {
            Picasso.with(getActivity())
                    .load(new File(flower.getImagePath()))
                    .transform(new CircleTransformation(0, 0))
                    .into(imageView);
        }
    }

    private void refreshChangeIconEnabled() {
        if (imageView == null) {
            return;
        }

        if (flower.getId() == DatabaseProvider.DEFAULT_ID) {
            imageView.setEnabled(false);
        } else {
            imageView.setEnabled(true);
        }
    }

    private void refreshDataVisibility() {
        if (viewPager == null || flower == null) {
            return;
        }

        if (flower.getId() == DatabaseProvider.DEFAULT_ID) {
            viewPager.setVisibility(View.INVISIBLE);
            addEventButton.setVisibility(View.INVISIBLE);
        } else {
            viewPager.setVisibility(View.VISIBLE);
            addEventButton.setVisibility(View.VISIBLE);
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

    private void showProgress() {
        if (progressContainer == null) {
            return;
        }

        progressContainer.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        if (progressContainer == null) {
            return;
        }

        progressContainer.setVisibility(View.GONE);
    }
}
