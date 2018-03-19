package com.indoor.flowers.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evgeniysharafan.utils.Fragments;
import com.evgeniysharafan.utils.Toasts;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.GalleryAdapter;
import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.PhotoItem;
import com.indoor.flowers.util.OnItemClickListener;
import com.indoor.flowers.util.PermissionHelper;
import com.indoor.flowers.util.PermissionUtil;
import com.indoor.flowers.util.TakePhotoUtils;
import com.indoor.flowers.util.TakePhotoUtils.OnPhotoTakenListener;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TargetGalleryFragment extends Fragment implements OnItemClickListener<PhotoItem>,
        OnPhotoTakenListener {

    private static final String KEY_TARGET_ID = "key_target_id";
    private static final String KEY_TARGET_TABLE = "key_target_table";

    @BindView(R.id.fl_list)
    RecyclerView listView;
    @BindView(R.id.fl_snackbar)
    CoordinatorLayout snackbarContainer;
    @BindView(R.id.fl_progress_container)
    ViewGroup progressContainer;

    private Unbinder unbinder;
    private GalleryAdapter galleryAdapter;

    private FlowersProvider provider;

    private PermissionHelper permissionHelper;

    public TargetGalleryFragment() {
        permissionHelper = new PermissionHelper(this, 0, PermissionUtil.CAMERA_PERMISSIONS,
                new int[]{R.string.permissions_camera_required, R.string.permissions_storage_required});
    }

    public static TargetGalleryFragment newInstance(long targetId, String targetTable) {
        Bundle args = new Bundle();
        args.putLong(KEY_TARGET_ID, targetId);
        args.putString(KEY_TARGET_TABLE, targetTable);

        TargetGalleryFragment fragment = new TargetGalleryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provider = new FlowersProvider(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        permissionHelper.setSnackbarContainer(snackbarContainer);
        if (galleryAdapter == null) {
            galleryAdapter = new GalleryAdapter();
        }

        galleryAdapter.setListener(this);
        listView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        listView.setAdapter(galleryAdapter);
        reloadPhotos();
        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
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

    @Override
    public void onItemClicked(PhotoItem item) {
        FragmentManager manager = getParentFragment() != null ? getParentFragment().getFragmentManager()
                : getFragmentManager();
        Fragments.replace(manager, android.R.id.content,
                GalleryFragment.newInstance(getTargetId(), getTargetTable(), item.getId()),
                null, true);
    }

    @OnClick(R.id.fl_action_add)
    void onAddItemClicked() {
        tryAddPhoto();
    }

    @Override
    public void onPhotoTaken(File photo) {
        PhotoItem photoItem = new PhotoItem();
        photoItem.setImagePath(photo.getPath());
        photoItem.setTargetId(getTargetId());
        photoItem.setTargetTable(getTargetTable());
        photoItem.setDate(Calendar.getInstance());
        provider.addPhoto(photoItem);
        galleryAdapter.add(photoItem);
        hideProgress();
    }

    @Override
    public void onPhotoError() {
        Toasts.showLong(R.string.photo_choose_error);
        hideProgress();
    }

    private void tryAddPhoto() {
        if (permissionHelper.hasAllPermissions()) {
            TakePhotoUtils.getInstance().showSystemChooser(TargetGalleryFragment.this);
        } else {
            permissionHelper.checkPermissions();
        }
    }

    private void reloadPhotos() {
        List<PhotoItem> photos = provider.getPhotosForTarget(getTargetId(), getTargetTable());
        galleryAdapter.setItems(photos);
    }

    private long getTargetId() {
        Bundle args = getArguments();
        return args != null && args.containsKey(KEY_TARGET_ID)
                ? args.getLong(KEY_TARGET_ID, DatabaseProvider.DEFAULT_ID)
                : DatabaseProvider.DEFAULT_ID;
    }

    private String getTargetTable() {
        Bundle args = getArguments();
        return args != null && args.containsKey(KEY_TARGET_TABLE)
                ? args.getString(KEY_TARGET_TABLE)
                : null;
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
