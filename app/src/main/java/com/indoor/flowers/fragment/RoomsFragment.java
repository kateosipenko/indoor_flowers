package com.indoor.flowers.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evgeniysharafan.utils.Fragments;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.RoomsAdapter;
import com.indoor.flowers.adapter.RoomsAdapter.RoomClickListener;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Room;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RoomsFragment extends Fragment implements RoomClickListener {

    public static final String KEY_SELECTED_ROOM = "key_selected_room";

    @BindView(R.id.fr_rooms_list)
    RecyclerView roomsList;

    private Unbinder unbinder;

    private RoomsAdapter adapter;
    private FlowersProvider provider;

    public static RoomsFragment newInstance() {
        return new RoomsFragment();
    }

    public static RoomsFragment newInstance(Fragment targetFragment, int requestCode) {
        RoomsFragment fragment = new RoomsFragment();
        fragment.setTargetFragment(targetFragment, requestCode);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provider = new FlowersProvider(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);
        unbinder = ButterKnife.bind(this, view);
        initList();
        reloadRooms();
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

    @OnClick(R.id.fr_create_room)
    void onCreateRoomClicked() {
        Fragments.replace(getFragmentManager(), android.R.id.content, CreateRoomFragment.newInstance(),
                null, true);
    }

    @Override
    public void onRoomClicked(Room room) {
        if (getTargetFragment() != null) {
            setActivityResult(room);
            getActivity().onBackPressed();
        }
    }

    private void reloadRooms() {
        List<Room> allRooms = provider.getAllRooms();
        adapter.setRooms(allRooms);
    }

    private void setActivityResult(Room room) {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment == null) {
            return;
        }

        Intent data = new Intent();
        data.putExtra(KEY_SELECTED_ROOM, room.getId());
        targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
    }

    private void initList() {
        if (adapter == null) {
            adapter = new RoomsAdapter();
        }

        adapter.setListener(this);
        roomsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        roomsList.setAdapter(adapter);
    }
}
