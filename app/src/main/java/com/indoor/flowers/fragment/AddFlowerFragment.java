package com.indoor.flowers.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.evgeniysharafan.utils.Toasts;
import com.indoor.flowers.R;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.SettingData;
import com.indoor.flowers.service.FlowersNotificationsService;
import com.indoor.flowers.util.PermissionHelper;
import com.indoor.flowers.util.PermissionUtil;
import com.indoor.flowers.util.ProgressShowingUtil;
import com.indoor.flowers.util.TakePhotoUtils;
import com.indoor.flowers.util.TakePhotoUtils.OnPhotoTakenListener;
import com.indoor.flowers.view.SettingsDataView;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

public class AddFlowerFragment extends Fragment implements OnPhotoTakenListener {

    private static final String KEY_NAME = "key_name";
    private static final String KEY_GROUP_ID = "key_group_id";

    @BindView(R.id.faf_flower_image)
    ImageView imageView;
    @BindView(R.id.faf_flower_name)
    EditText nameView;
    @BindView(R.id.faf_setting_data)
    SettingsDataView settingsDataView;
    @BindView(R.id.faf_snackbar)
    CoordinatorLayout snackbarContainer;

    private ProgressShowingUtil progressUtil;
    private PermissionHelper permissionHelper;

    private Flower flower;
    private FlowersProvider provider;

    private Unbinder unbinder;

    public AddFlowerFragment() {
        permissionHelper = new PermissionHelper(this, 0, PermissionUtil.CAMERA_PERMISSIONS,
                new int[]{R.string.permissions_camera_required, R.string.permissions_storage_required});
    }

    public static AddFlowerFragment newInstance() {
        return new AddFlowerFragment();
    }

    public static AddFlowerFragment newInstance(long groupId) {
        Bundle args = new Bundle();
        args.putLong(KEY_GROUP_ID, groupId);
        AddFlowerFragment fragment = new AddFlowerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressUtil = new ProgressShowingUtil(this);
        provider = new FlowersProvider(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_flower, container, false);
        unbinder = ButterKnife.bind(this, view);

        permissionHelper.setSnackbarContainer(snackbarContainer);

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        }

        if (flower == null) {
            flower = new Flower();
            flower.setGroupId(getGroupIdFromArgs());
        }

        refreshViewWithFlower();
        return view;
    }

    @Override
    public void onDestroyView() {
        progressUtil.hideProgress();
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        provider.unbind();
        super.onDestroy();
    }

    @OnClick(R.id.faf_choose_image)
    void onChooseImageClicked() {
        if (permissionHelper.hasAllPermissions()) {
            TakePhotoUtils.getInstance().showSystemChooser(this);
        } else {
            permissionHelper.checkPermissions();
        }
    }

    @OnClick(R.id.faf_create)
    void onCreateFlowerClicked() {
        if (TextUtils.isEmpty(flower.getName())) {
            Toasts.showLong(R.string.faf_error_name_empty);
            return;
        }

        SettingData data = settingsDataView.getSettingData();
        if (data.getLastWateringDate() == null || data.getWateringFrequency() <= 0) {
            Toasts.showLong(R.string.sdv_data_empty);
            return;
        }

        flower.setSettings(data);
        provider.createFlower(flower);
        FlowersNotificationsService.setupNotificationForFlower(getActivity(), flower);
        getActivity().onBackPressed();
    }

    @OnTextChanged(R.id.faf_flower_name)
    void onNameTextChanged(CharSequence s, int start, int before, int count) {
        flower.setName(nameView.getText().toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TakePhotoUtils.REQUEST_CODE_CAMERA:
            case TakePhotoUtils.REQUEST_CODE_GALLERY:
            case TakePhotoUtils.REQUEST_CODE_SYSTEM_CHOOSER:
                if (TakePhotoUtils.getInstance().isPhotoRequestOk(requestCode, resultCode)) {
                    progressUtil.showProgress();
                    TakePhotoUtils.getInstance().onActivityResult(requestCode, resultCode, data, this);
                }
                break;
        }
    }

    @Override
    public void onPhotoTaken(File photo) {
        flower.setImagePath(photo.getPath());
        Picasso.with(getActivity()).load(photo).into(imageView);
        progressUtil.hideProgress();
    }

    @Override
    public void onPhotoError() {
        Toasts.showLong(R.string.photo_choose_error);
        progressUtil.hideProgress();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (nameView != null) {
            outState.putString(KEY_NAME, nameView.getText().toString());
        }
    }

    private void refreshViewWithFlower() {
        if (nameView == null) {
            return;
        }

        nameView.setText(flower.getName());
        if (!TextUtils.isEmpty(flower.getImagePath())) {
            Picasso.with(getActivity())
                    .load(new File(flower.getImagePath()))
                    .into(imageView);
        }
    }

    private void restoreState(Bundle state) {
        String name = state.getString(KEY_NAME);
        nameView.setText(name);
    }

    private long getGroupIdFromArgs() {
        return getArguments() != null && getArguments().containsKey(KEY_GROUP_ID)
                ? getArguments().getLong(KEY_GROUP_ID, -1) : -1;
    }
}
