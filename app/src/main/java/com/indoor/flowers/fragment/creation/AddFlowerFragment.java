package com.indoor.flowers.fragment.creation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.indoor.flowers.R;
import com.indoor.flowers.adapter.AddFlowerPagerAdapter;
import com.indoor.flowers.util.PermissionHelper;
import com.indoor.flowers.util.PermissionUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddFlowerFragment extends Fragment {

    private static final int REQUEST_CODE_PERMISSIONS = 1323;

    @Bind(R.id.faf_pager)
    ViewPager pager;
    @Bind(R.id.faf_tabs)
    TabLayout tabs;
    @Bind(R.id.faf_snackbar)
    CoordinatorLayout snackBarContainer;

    private AddFlowerPagerAdapter adapter;
    private PermissionHelper permissionHelper;

    public AddFlowerFragment() {
        permissionHelper = new PermissionHelper(this, REQUEST_CODE_PERMISSIONS, PermissionUtil.CAMERA_PERMISSIONS,
                new int[]{R.string.permissions_camera_required, R.string.permissions_storage_required});
    }

    public static AddFlowerFragment newInstance() {
        return new AddFlowerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_flower, container, false);
        ButterKnife.bind(this, view);

        permissionHelper.setSnackbarContainer(snackBarContainer);
        if (savedInstanceState != null) {
            permissionHelper.restoreState(savedInstanceState);
        }

        setupPager();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        permissionHelper.checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        permissionHelper.onSaveInstanceState(outState);
    }

    private void setupPager() {
        if (adapter == null) {
            adapter = new AddFlowerPagerAdapter(getChildFragmentManager());
        }

        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
    }
}
