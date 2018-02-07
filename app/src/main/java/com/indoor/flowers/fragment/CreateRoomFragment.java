package com.indoor.flowers.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.evgeniysharafan.utils.Toasts;
import com.indoor.flowers.R;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Room;
import com.indoor.flowers.view.ValueSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CreateRoomFragment extends Fragment {

    @BindView(R.id.fcr_temperature_range)
    ValueSeekBar temperatureSeekBar;
    @BindView(R.id.fcr_humidity_range)
    ValueSeekBar humiditySeekBar;
    @BindView(R.id.fcr_brightness_range)
    ValueSeekBar brightnessSeekBar;

    private FlowersProvider flowersProvider;

    private Unbinder unbinder;

    public static CreateRoomFragment newInstance() {
        return new CreateRoomFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flowersProvider = new FlowersProvider(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_room, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupActionBar();
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

    @OnClick(R.id.fcr_save)
    void onSaveRoomClick() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialong_edit_text, null, false);
        final EditText editText = ButterKnife.findById(view, R.id.det_text);
        new AlertDialog.Builder(getActivity(), R.style.Dialog)
                .setTitle(R.string.fcr_save_room)
                .setView(view)
                .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            Toasts.showLong(R.string.fcr_name_room_error);
                            onSaveRoomClick();
                        } else {
                            createRoom(name);
                        }
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .create()
                .show();
    }

    private void createRoom(String name) {
        Room room = new Room();
        room.setName(name);
        room.setBrightness((int) brightnessSeekBar.getValue());
        room.setHumidity((int) humiditySeekBar.getValue());
        room.setTemperature((int) temperatureSeekBar.getValue());
        flowersProvider.createRoom(room);
        getActivity().onBackPressed();
    }

    private void setupActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.fcr_title);
            actionBar.show();
        }
    }
}
