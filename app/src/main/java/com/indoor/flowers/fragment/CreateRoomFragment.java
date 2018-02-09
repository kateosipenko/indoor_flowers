package com.indoor.flowers.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.evgeniysharafan.utils.Toasts;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.RoomDefaultIconsAdapter;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.BrightnessLevel;
import com.indoor.flowers.model.HumidityLevel;
import com.indoor.flowers.model.Room;
import com.indoor.flowers.util.OnItemClickListener;
import com.indoor.flowers.util.PermissionHelper;
import com.indoor.flowers.util.PermissionUtil;
import com.indoor.flowers.util.TakePhotoUtils;
import com.indoor.flowers.util.TakePhotoUtils.OnPhotoTakenListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

public class CreateRoomFragment extends Fragment implements OnCheckedChangeListener,
        OnItemClickListener<Integer>, OnPhotoTakenListener {

    @BindView(R.id.fcr_brightness_group)
    RadioGroup brightnessGroup;
    @BindView(R.id.fcr_humidity_group)
    RadioGroup humidityGroup;
    @BindView(R.id.fcr_name)
    EditText nameView;
    @BindView(R.id.fcr_change_picture)
    ImageView changePictureView;
    @BindView(R.id.fcr_room_icon)
    ImageView iconView;
    @BindView(R.id.fcr_default_icons_list)
    RecyclerView defaultIconsList;
    @BindView(R.id.fcr_snackbar)
    CoordinatorLayout snackbarContainer;

    private FlowersProvider flowersProvider;
    private Unbinder unbinder;

    private RoomDefaultIconsAdapter defaultIconsAdapter;
    private PermissionHelper permissionHelper;

    private Room room = new Room();

    public CreateRoomFragment() {
        permissionHelper = new PermissionHelper(this, 0, PermissionUtil.CAMERA_PERMISSIONS,
                new int[]{R.string.permissions_camera_required, R.string.permissions_storage_required});
    }

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

        permissionHelper.setSnackbarContainer(snackbarContainer);

        brightnessGroup.setOnCheckedChangeListener(this);
        humidityGroup.setOnCheckedChangeListener(this);

        initDefaultIcons();
        refreshPicture();
        loadDefaultRoomPictures();
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
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == brightnessGroup) {
            onBrightnessChanged(checkedId);
        } else {
            onHumidityChanged(checkedId);
        }
    }

    @OnTextChanged(R.id.fcr_name)
    void onNameTextChanged(CharSequence s, int start, int before, int count) {
        room.setName(nameView.getText().toString());
    }

    @OnClick(R.id.fcr_save)
    void onSaveRoomClick() {
        if (canCreateRoom()) {
            flowersProvider.createRoom(room);
            getActivity().onBackPressed();
        }
    }

    @OnClick({R.id.fcr_change_picture, R.id.fcr_room_icon})
    void onChangePictureClicked(View view) {
        if (permissionHelper.hasAllPermissions()) {
            TakePhotoUtils.getInstance().showSystemChooser(this);
        } else {
            permissionHelper.checkPermissions();
        }
    }

    @Override
    public void onItemClicked(Integer item) {
        room.setImagePath(String.valueOf(item));
        refreshPicture();
    }

    @Override
    public void onPhotoTaken(File photo) {
        room.setImagePath(photo.getPath());
        refreshPicture();
    }

    @Override
    public void onPhotoError() {
        Toasts.showLong(R.string.photo_choose_error);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TakePhotoUtils.REQUEST_CODE_CAMERA:
            case TakePhotoUtils.REQUEST_CODE_GALLERY:
            case TakePhotoUtils.REQUEST_CODE_SYSTEM_CHOOSER:
                TakePhotoUtils.getInstance().onActivityResult(requestCode, resultCode, data, this);
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initDefaultIcons() {
        if (defaultIconsAdapter == null) {
            defaultIconsAdapter = new RoomDefaultIconsAdapter();
        }

        defaultIconsAdapter.setCallback(this);
        defaultIconsList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        defaultIconsList.setAdapter(defaultIconsAdapter);
    }

    private void onBrightnessChanged(int checkedId) {
        switch (checkedId) {
            case R.id.fcr_brightness_small:
                room.setBrightness(BrightnessLevel.SMALL);
                break;
            case R.id.fcr_brightness_medium:
                room.setBrightness(BrightnessLevel.MEDIUM);
                break;
            case R.id.fcr_brightness_full:
                room.setBrightness(BrightnessLevel.MAX);
                break;
        }
    }

    private void onHumidityChanged(int checkedId) {
        switch (checkedId) {
            case R.id.fcr_humidity_small:
                room.setHumidity(HumidityLevel.SMALL);
                break;
            case R.id.fcr_humidity_medium:
                room.setHumidity(HumidityLevel.MEDIUM);
                break;
            case R.id.fcr_humidity_full:
                room.setHumidity(HumidityLevel.MAX);
                break;
        }
    }

    private boolean canCreateRoom() {
        return !TextUtils.isEmpty(room.getName())
                && brightnessGroup.getCheckedRadioButtonId() != -1
                && humidityGroup.getCheckedRadioButtonId() != -1
                && !TextUtils.isEmpty(room.getImagePath());
    }

    private void refreshPicture() {
        if (changePictureView == null) {
            return;
        }

        if (room != null && !TextUtils.isEmpty(room.getImagePath())) {
            changePictureView.setVisibility(View.VISIBLE);
            int resId = room.getIconRes();
            if (resId != -1) {
                iconView.setImageResource(resId);
            } else {
                Picasso.with(getActivity())
                        .load(new File(room.getImagePath()))
                        .into(iconView);
            }
        } else {
            changePictureView.setVisibility(View.GONE);
            iconView.setImageBitmap(null);
        }
    }

    private void loadDefaultRoomPictures() {
        List<Integer> defaultPicturesRes = new ArrayList<>();
        defaultPicturesRes.add(R.drawable.room_dining);
        defaultPicturesRes.add(R.drawable.room_balcony);
        defaultPicturesRes.add(R.drawable.room_bed);
        defaultPicturesRes.add(R.drawable.room_living);
        defaultPicturesRes.add(R.drawable.room_work);

        defaultIconsAdapter.setIcons(defaultPicturesRes);
    }
}
