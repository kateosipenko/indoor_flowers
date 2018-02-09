package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.evgeniysharafan.utils.Fragments;
import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.FlowersAdapter;
import com.indoor.flowers.adapter.RoomsAdapter;
import com.indoor.flowers.adapter.RoomsAdapter.RoomClickListener;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Room;
import com.indoor.flowers.util.AnimationUtils;
import com.indoor.flowers.util.SpaceItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainFragment extends Fragment implements RoomClickListener {

    @BindView(R.id.fm_root)
    ConstraintLayout rootLayout;
    @BindView(R.id.fm_flowers_container)
    ConstraintLayout flowersContainer;
    @BindView(R.id.fm_rooms_container)
    ConstraintLayout roomsContainer;

    @BindView(R.id.fm_flowers_list)
    RecyclerView flowersList;
    @BindView(R.id.fm_rooms_list)
    RecyclerView roomsList;

    private Unbinder unbinder;

    private ConstraintSet flowersOpenedSet = new ConstraintSet();
    private ConstraintSet roomsOpenedSet = new ConstraintSet();

    private ConstraintSet roomsFullSet = new ConstraintSet();
    private ConstraintSet roomsCollapsedSet = new ConstraintSet();

    private ConstraintSet flowersFullSet = new ConstraintSet();
    private ConstraintSet flowersCollapsedSet = new ConstraintSet();

    private FlowersProvider provider;

    private RoomsAdapter roomsAdapter;
    private GridLayoutManager roomsLayoutManager;

    private FlowersAdapter flowersAdapter;
    private GridLayoutManager flowersLayoutManager;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransitions();
        provider = new FlowersProvider(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        createConstraintsSets();
        initFlowersList();
        initRoomsList();
        reloadRooms();
        reloadFlowers(roomsAdapter.getSelectedRoom());
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

    @OnClick({R.id.fm_flowers_settings, R.id.fm_rooms_settings})
    void onSettingsClicked(View view) {
        ConstraintSet mainSet = roomsOpenedSet;
        boolean showRooms = false;
        switch (view.getId()) {
            case R.id.fm_flowers_settings:
                mainSet = flowersOpenedSet;
                break;
            case R.id.fm_rooms_settings:
                mainSet = roomsOpenedSet;
                showRooms = true;
                break;
        }

        TransitionManager.beginDelayedTransition(rootLayout);
        mainSet.applyTo(rootLayout);
        if (showRooms) {
            roomsFullSet.applyTo(roomsContainer);
            flowersCollapsedSet.applyTo(flowersContainer);
            roomsLayoutManager.setSpanCount(2);
            flowersLayoutManager.setSpanCount(1);
        } else {
            roomsCollapsedSet.applyTo(roomsContainer);
            flowersFullSet.applyTo(flowersContainer);
            roomsLayoutManager.setSpanCount(1);
            flowersLayoutManager.setSpanCount(2);
        }
    }

    @OnClick({R.id.fm_add_flower, R.id.fm_add_room})
    void onAddClicked(View view) {
        Fragment fragment = null;
        switch (view.getId()) {
            case R.id.fm_add_flower:
                fragment = AddFlowerFragment.newInstance(roomsAdapter.getSelectedRoom().getId());
                break;
            case R.id.fm_add_room:
                fragment = CreateRoomFragment.newInstance();
                break;
        }

        if (fragment != null) {
            Fragments.replace(getFragmentManager(), android.R.id.content, fragment,
                    null, true);
        }
    }

    @Override
    public void onRoomClicked(Room room) {
        reloadFlowers(room);
    }

    private void reloadRooms() {
        List<Room> rooms = provider.getAllRooms();
        roomsAdapter.setRooms(rooms);
    }

    private void reloadFlowers(Room selectedRoom) {
        if (selectedRoom != null) {
            List<Flower> flowers = provider.getFlowersForRoom(selectedRoom.getId());
            flowersAdapter.setFlowers(flowers);
        } else {
            flowersAdapter.clear();
        }
    }

    private void initRoomsList() {
        if (roomsAdapter == null) {
            roomsAdapter = new RoomsAdapter();
        }

        roomsAdapter.setListener(this);
        roomsLayoutManager = new GridLayoutManager(getActivity(), 1);
        roomsList.setLayoutManager(roomsLayoutManager);
        int space = Res.getDimensionPixelSize(R.dimen.padding_normal);
        roomsList.addItemDecoration(new SpaceItemDecoration(space, space));
        roomsList.setAdapter(roomsAdapter);
    }

    private void initFlowersList() {
        if (flowersAdapter == null) {
            flowersAdapter = new FlowersAdapter();
        }

        flowersLayoutManager = new GridLayoutManager(getActivity(), 1);
        flowersList.setLayoutManager(flowersLayoutManager);
        int space = Res.getDimensionPixelSize(R.dimen.padding_normal);
        flowersList.addItemDecoration(new SpaceItemDecoration(space, space));
        flowersList.setAdapter(flowersAdapter);
    }

    private void createConstraintsSets() {
        flowersOpenedSet.clone(rootLayout);

        roomsOpenedSet.clone(rootLayout);
        roomsOpenedSet.constrainWidth(R.id.fm_rooms_container, ConstraintSet.MATCH_CONSTRAINT);
        roomsOpenedSet.constrainWidth(R.id.fm_flowers_container, Res.getDimensionPixelSize(R.dimen.fm_rooms_width));

        roomsCollapsedSet.clone(roomsContainer);
        roomsFullSet.clone(roomsContainer);
        roomsFullSet.setVisibility(R.id.fm_rooms_settings, ConstraintSet.GONE);
        roomsFullSet.setVisibility(R.id.fm_add_room, ConstraintSet.VISIBLE);
        roomsFullSet.constrainWidth(R.id.fm_rooms_list, ConstraintSet.MATCH_CONSTRAINT);

        flowersFullSet.clone(flowersContainer);
        flowersCollapsedSet.clone(flowersContainer);
        flowersCollapsedSet.setVisibility(R.id.fm_flowers_settings, ConstraintSet.VISIBLE);
        flowersCollapsedSet.setVisibility(R.id.fm_add_flower, ConstraintSet.GONE);
    }

    private void setTransitions() {
        TransitionSet set = new TransitionSet();
        set.addTransition(new Slide(AnimationUtils.getGravityDirection(Gravity.START))
                .addTarget(R.id.fm_rooms_container));
        set.addTransition(new Slide(AnimationUtils.getGravityDirection(Gravity.END))
                .addTarget(R.id.fm_flowers_container));
        set.setDuration(300);
        set.setInterpolator(new OvershootInterpolator());
        setEnterTransition(set);
        setExitTransition(set);
        setReenterTransition(set);
        setReturnTransition(set);
    }
}
