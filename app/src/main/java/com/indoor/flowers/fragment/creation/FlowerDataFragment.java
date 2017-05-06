package com.indoor.flowers.fragment.creation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.evgeniysharafan.utils.Toasts;
import com.indoor.flowers.R;
import com.indoor.flowers.util.PermissionHelper;
import com.indoor.flowers.util.PermissionUtil;
import com.indoor.flowers.util.ProgressShowingUtil;
import com.indoor.flowers.util.TakePhotoUtils;
import com.indoor.flowers.util.TakePhotoUtils.OnPhotoTakenListener;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FlowerDataFragment extends Fragment implements OnPhotoTakenListener {

    private static final String KEY_IMAGE_PATH = "key_image_path";
    private static final String KEY_NAME = "key_name";

    @Bind(R.id.ffd_flower_image)
    ImageView imageView;
    @Bind(R.id.ffd_flower_name)
    EditText nameView;

    private String imagePath;
    private ProgressShowingUtil progressUtil;
    private PermissionHelper permissionHelper;

    public FlowerDataFragment() {
        permissionHelper = new PermissionHelper(this, 0, PermissionUtil.CAMERA_PERMISSIONS,
                new int[]{R.string.permissions_camera_required, R.string.permissions_storage_required});
    }

    public static FlowerDataFragment newInstance() {
        return new FlowerDataFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressUtil = new ProgressShowingUtil(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flower_data, container, false);
        ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        progressUtil.hideProgress();
    }

    @OnClick(R.id.ffd_choose_image)
    void onChooseImageClicked() {
        if (permissionHelper.hasAllPermissions()) {
            TakePhotoUtils.getInstance().showSystemChooser(this);
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
                    progressUtil.showProgress();
                    TakePhotoUtils.getInstance().onActivityResult(requestCode, resultCode, data, this);
                }
                break;

        }
    }

    @Override
    public void onPhotoTaken(File photo) {
        this.imagePath = photo.getPath();
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
        outState.putString(KEY_IMAGE_PATH, imagePath);
        if (nameView != null) {
            outState.putString(KEY_NAME, nameView.getText().toString());
        }
    }

    private void restoreState(Bundle state) {
        imagePath = state.getString(KEY_IMAGE_PATH);
        String name = state.getString(KEY_NAME);
        if (imageView != null) {
            if (!TextUtils.isEmpty(imagePath)) {
                Picasso.with(getActivity()).load(new File(imagePath)).into(imageView);
            }

            nameView.setText(name);
        }
    }
}
