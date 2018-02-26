package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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
import com.evgeniysharafan.utils.picasso.CircleTransformation;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.EventsAdapter;
import com.indoor.flowers.adapter.FlowersAdapter;
import com.indoor.flowers.adapter.FlowersAdapter.FlowersSelectionListener;
import com.indoor.flowers.adapter.GroupPagerAdapter;
import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.database.provider.NotificationsProvider;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.model.Notification;
import com.indoor.flowers.util.FilesUtils;
import com.indoor.flowers.util.FlowersAlarmsUtils;
import com.indoor.flowers.util.OnItemClickListener;
import com.indoor.flowers.util.PhotoUtils;
import com.indoor.flowers.view.NameView;
import com.indoor.flowers.view.NameView.NameChangeListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class GroupFragment extends Fragment implements OnItemClickListener<Notification>,
        OnPageChangeListener, FlowersSelectionListener, NameChangeListener {

    private static final String KEY_GROUP_ID = "key_group_id";
    private static final String KEY_FLOWER_ID = "key_flower_id";

    @BindView(R.id.fg_title)
    NameView nameView;
    @BindView(R.id.fg_group_icon)
    ImageView iconView;
    @BindView(R.id.fg_tabs)
    TabLayout tabsView;
    @BindView(R.id.fg_pager)
    ViewPager viewPager;
    @BindView(R.id.fg_add)
    View addButtonView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private FlowersProvider flowersProvider;
    private NotificationsProvider notificationsProvider;
    private Unbinder unbinder;

    private Group group;

    private GroupPagerAdapter pagerAdapter;
    private FlowersAdapter flowersAdapter;
    private EventsAdapter eventsAdapter;

    public static GroupFragment newInstance() {
        return new GroupFragment();
    }

    public static GroupFragment newInstance(long groupId) {
        Bundle args = new Bundle();
        args.putLong(KEY_GROUP_ID, groupId);

        GroupFragment fragment = new GroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static GroupFragment newInstanceForFlower(long flowerId) {
        Bundle args = new Bundle();
        args.putLong(KEY_FLOWER_ID, flowerId);

        GroupFragment fragment = new GroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        flowersProvider = new FlowersProvider(getActivity());
        notificationsProvider = new NotificationsProvider(getActivity());
        long groupId = getArguments() != null && getArguments().containsKey(KEY_GROUP_ID)
                ? getArguments().getLong(KEY_GROUP_ID, DatabaseProvider.DEFAULT_ID)
                : DatabaseProvider.DEFAULT_ID;
        group = flowersProvider.getGroupById(groupId);
        if (group == null) {
            group = new Group();
            group.setId(DatabaseProvider.DEFAULT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupActionBar();
        initPager();
        refreshViewWithGroup();
        reloadItems();
        refreshIconView();
        nameView.setListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        flowersProvider.unbind();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (group.getId() != DatabaseProvider.DEFAULT_ID) {
            inflater.inflate(R.menu.menu_group, menu);
        } else {
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mg_delete) {
            FlowersAlarmsUtils.deleteAlarmsForEvents(getActivity(),
                    notificationsProvider.getEventsForTarget(group.getId(), Group.TABLE_NAME));
            flowersProvider.deleteGroup(group);
            getActivity().onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNameChanged(String name) {
        if (!TextUtils.isEmpty(name) && !name.equals(group.getName())) {
            group.setName(nameView.getText().toString());
            flowersProvider.createOrUpdateGroup(group);
            flowersProvider.refreshGroupFlowers(group, flowersAdapter.getSelectedFlowers());
            refreshAddButtonVisibility();
        }
    }

    @OnClick(R.id.fg_add)
    void onAddClicked() {
        Fragment fragment = null;
        if (viewPager.getCurrentItem() == GroupPagerAdapter.POSITION_EVENTS) {
            fragment = NotificationFragment.newInstance(group.getId(), Group.TABLE_NAME);
        } else {
            fragment = FlowerFragment.newInstance();
        }

        Fragments.replace(getFragmentManager(), android.R.id.content,
                fragment, null, true);
    }

    @Override
    public void onItemClicked(Notification item) {
        Fragments.replace(getFragmentManager(), android.R.id.content,
                NotificationFragment.newInstance(item.getId()), null, true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        reloadItems();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSelectedFlowersChanged(List<Flower> selectedFlowers) {
        if (group.getId() != DatabaseProvider.DEFAULT_ID) {
            flowersProvider.refreshGroupFlowers(group, selectedFlowers);
            FilesUtils.deleteFile(group.getImagePath());
            group.setImagePath(PhotoUtils.tryGenerateGroupPhoto(200, 200,
                    flowersAdapter.getSelectedFlowers()));
            flowersProvider.createOrUpdateGroup(group);
            refreshIconView();
        }
    }

    private void setupActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(null);
            }
        }
    }

    private void refreshIconView() {
        if (iconView == null) {
            return;
        }

        if (TextUtils.isEmpty(group.getImagePath())) {
            iconView.setImageBitmap(null);
        } else {
            Picasso.with(getActivity())
                    .load(new File(group.getImagePath()))
                    .transform(new CircleTransformation(0, 0))
                    .into(iconView);
        }
    }

    private void refreshViewWithGroup() {
        if (nameView == null) {
            return;
        }

        nameView.setText(group.getName());
        refreshAddButtonVisibility();
        if (group.getId() == DatabaseProvider.DEFAULT_ID) {
            nameView.startEditing();
        }
    }

    private void refreshAddButtonVisibility() {
        if (addButtonView == null) {
            return;
        }

        addButtonView.setVisibility(group.getId() == DatabaseProvider.DEFAULT_ID ? View.GONE : View.VISIBLE);
    }

    private void initPager() {
        initEventsAdapter();
        initFlowersAdapter();
        if (pagerAdapter == null) {
            pagerAdapter = new GroupPagerAdapter();
        }

        pagerAdapter.setAdapters(flowersAdapter, eventsAdapter);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);
        tabsView.setupWithViewPager(viewPager);
    }

    private void initEventsAdapter() {
        if (eventsAdapter == null) {
            eventsAdapter = new EventsAdapter();
        }

        eventsAdapter.setListener(this);
    }

    private void initFlowersAdapter() {
        if (flowersAdapter == null) {
            flowersAdapter = new FlowersAdapter();
        }

        flowersAdapter.setSelectionMode(true);
        flowersAdapter.setSelectionListener(this);
    }

    private void reloadItems() {
        if (viewPager == null) {
            return;
        }

        if (viewPager.getCurrentItem() == GroupPagerAdapter.POSITION_FLOWERS) {
            reloadFlowers();
        } else {
            reloadEvents();
        }
    }

    private void reloadEvents() {
        List<Notification> events = notificationsProvider.getEventsForTarget(group.getId(), Group.TABLE_NAME);
        eventsAdapter.setItems(events);
    }

    private void reloadFlowers() {
        List<Flower> allFlowers = flowersProvider.getAllFlowers();
        flowersAdapter.setItems(allFlowers);

        List<Flower> flowers = flowersProvider.getFlowersForGroup(group.getId());
        flowersAdapter.setSelectedFlowers(flowers);
        flowersAdapter.addSelected(getFlowerIdFromArgs());
    }

    private long getFlowerIdFromArgs() {
        return getArguments() != null ? getArguments().getLong(KEY_FLOWER_ID, DatabaseProvider.DEFAULT_ID)
                : DatabaseProvider.DEFAULT_ID;
    }
}
