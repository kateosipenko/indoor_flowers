package com.indoor.flowers.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.evgeniysharafan.utils.Res;
import com.evgeniysharafan.utils.Toasts;
import com.evgeniysharafan.utils.picasso.CircleTransformation;
import com.indoor.flowers.R;
import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.database.provider.NotificationsProvider;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.NotificationType;
import com.indoor.flowers.model.NotificationWithTarget;
import com.indoor.flowers.model.PhotoItem;
import com.indoor.flowers.util.FilesUtils;
import com.indoor.flowers.util.NotificationsUtils;
import com.indoor.flowers.util.PermissionHelper;
import com.indoor.flowers.util.PermissionUtil;
import com.indoor.flowers.util.TakePhotoUtils;
import com.indoor.flowers.util.TakePhotoUtils.OnPhotoTakenListener;
import com.indoor.flowers.view.NameView;
import com.indoor.flowers.view.NameView.NameChangeListener;
import com.indoor.flowers.view.StatusView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FlowerProfileFragment extends Fragment implements NameChangeListener,
        OnPhotoTakenListener {

    private static final String KEY_FLOWER_ID = "key_flower_id";

    @BindView(R.id.ffp_snackbar)
    CoordinatorLayout snackbarContainer;
    @BindView(R.id.ffp_progress_container)
    ViewGroup progressContainer;
    @BindView(R.id.ffp_icon)
    ImageView imageView;
    @BindView(R.id.ffp_name)
    NameView nameView;
    @BindView(R.id.ffp_water_status)
    StatusView waterStatusView;
    @BindView(R.id.ffp_fertilizer_status)
    StatusView fertilizerStatusView;
    @BindView(R.id.ffp_status_container)
    ViewGroup statusContainer;

    private Unbinder unbinder;
    private PermissionHelper permissionHelper;
    private FlowersProvider flowersProvider;
    private NotificationsProvider notificationsProvider;

    private Flower flower;

    public FlowerProfileFragment() {
        permissionHelper = new PermissionHelper(this, 0, PermissionUtil.CAMERA_PERMISSIONS,
                new int[]{R.string.permissions_camera_required, R.string.permissions_storage_required});
    }

    public static FlowerProfileFragment newInstance(long flowerId) {
        Bundle args = new Bundle();
        args.putLong(KEY_FLOWER_ID, flowerId);
        FlowerProfileFragment fragment = new FlowerProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flowersProvider = new FlowersProvider(getActivity());
        notificationsProvider = new NotificationsProvider(getActivity());
        long flowerId = getFlowerIdFromArgs();
        flower = flowersProvider.getFlowerById(flowerId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flower_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        permissionHelper.setSnackbarContainer(snackbarContainer);
        refreshViewWithFlower();

        statusContainer.post(new Runnable() {
            @Override
            public void run() {
                int statusItemHeight = (int) (statusContainer.getMeasuredHeight() * 0.6);
                int statusItemWidth = (int) (statusContainer.getMeasuredWidth() * 0.4);
                refreshStatusItemSize(waterStatusView, statusItemHeight, statusItemWidth);
                refreshStatusItemSize(fertilizerStatusView, statusItemHeight, statusItemWidth);
                refreshStatusData();
            }
        });
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
        notificationsProvider.unbind();
        super.onDestroy();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        tryAddPhoto();
    }

    @OnClick({R.id.ffp_icon, R.id.ffp_change_icon})
    void onChooseImageClicked() {
        tryAddPhoto();
    }

    @Override
    public void onNameChanged(String name) {
        if (!TextUtils.isEmpty(name) && !name.equals(flower.getName())) {
            flower.setName(name);
            flowersProvider.updateFlower(flower);
        }
    }

    @Override
    public void onPhotoTaken(File photo) {
        flower.setImagePath(photo.getPath());
        refreshFlowerImage();
        flowersProvider.updateFlower(flower);

        PhotoItem photoItem = new PhotoItem();
        String filePath = FilesUtils.copyFileForTarget(photo.getPath(), FilesUtils.DataPart.FLOWERS, flower.getId());
        photoItem.setImagePath(filePath);

        photoItem.setTargetId(flower.getId());
        photoItem.setTargetTable(Flower.TABLE_NAME);
        photoItem.setDate(Calendar.getInstance());
        flowersProvider.addPhoto(photoItem);
        hideProgress();
    }

    @Override
    public void onPhotoError() {
        Toasts.showLong(R.string.photo_choose_error);
        hideProgress();
    }

    private void refreshStatusItemSize(StatusView view, int height, int width) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;
        params.width = width;
        view.setLayoutParams(params);
    }

    private void refreshViewWithFlower() {
        if (nameView == null) {
            return;
        }

        nameView.setText(flower.getName());
        if (flower.getId() == DatabaseProvider.DEFAULT_ID) {
            nameView.startEditing();
        }

        refreshFlowerImage();
    }

    private void refreshStatusData() {
        NotificationWithTarget waterNotification = flowersProvider.getLastNotificationAction(flower.getId(),
                NotificationType.WATERING);
        NotificationWithTarget fertilizerNotification = flowersProvider.getLastNotificationAction(flower.getId(),
                NotificationType.FERTILIZER);

        if (waterNotification != null) {
            waterStatusView.setWaterLevel(NotificationsUtils.getNotificationLevel(waterNotification));
            waterStatusView.setVisibility(View.VISIBLE);
        } else {
            waterStatusView.setVisibility(View.GONE);
        }

        if (fertilizerNotification != null) {
            fertilizerStatusView.setFertilizerLevel(NotificationsUtils.getNotificationLevel(fertilizerNotification));
            fertilizerStatusView.setVisibility(View.VISIBLE);
        } else {
            fertilizerStatusView.setVisibility(View.GONE);
        }
    }

    private void tryAddPhoto() {
        if (permissionHelper.hasAllPermissions()) {
            TakePhotoUtils.getInstance().showSystemChooser(FlowerProfileFragment.this);
        } else {
            permissionHelper.checkPermissions();
        }
    }

    private void refreshFlowerImage() {
        if (imageView == null) {
            return;
        }

        int padding = 0;
        if (TextUtils.isEmpty(flower.getImagePath())) {
            padding = Res.getDimensionPixelSize(R.dimen.ff_image_start_padding);
            imageView.setImageResource(R.drawable.ic_photo_camera);
        } else {
            Picasso.with(getActivity())
                    .load(new File(flower.getImagePath()))
                    .transform(new CircleTransformation(0, 0))
                    .into(imageView);
            padding = Res.getDimensionPixelSize(R.dimen.ff_image_padding);
        }

        imageView.setPadding(padding, padding, padding, padding);
    }

    private long getFlowerIdFromArgs() {
        Bundle args = getArguments();
        return args != null && args.containsKey(KEY_FLOWER_ID)
                ? args.getLong(KEY_FLOWER_ID, DatabaseProvider.DEFAULT_ID)
                : DatabaseProvider.DEFAULT_ID;
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
