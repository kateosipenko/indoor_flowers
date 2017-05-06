package com.indoor.flowers.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.R;

import java.util.HashMap;

/**
 * Accumulates logic of requesting needed permissions, showing dialog or snackbar, etc.
 */
public class PermissionHelper {

    private int permissionsCode;
    private String[] permissions;
    private HashMap<String, Integer> permissionsWithRationale = new HashMap<>();
    CoordinatorLayout snackbarContainer;

    private boolean isPermissionsDialogShowing;
    private Snackbar permissionsSnackbar;
    private Fragment targetFragment;
    private AlertDialog permissionDialog;
    private Activity targetActivity;

    public PermissionHelper(int permissionsRequestCode,
                            String[] permissions, int[] rationaleTexts) {
        this(null, permissionsRequestCode, permissions, rationaleTexts);
    }

    public PermissionHelper(Fragment targetFragment, int permissionsRequestCode,
                            String[] permissions, int[] rationaleTexts) {
        this.targetFragment = targetFragment;
        this.permissionsCode = permissionsRequestCode;
        this.permissions = permissions;

        if (permissions.length != rationaleTexts.length) {
            throw new IllegalArgumentException("permissions and rationaleTexts count should much");
        }

        for (int i = 0; i < permissions.length; i++) {
            permissionsWithRationale.put(permissions[i], i < rationaleTexts.length ? rationaleTexts[i] : -1);
        }
    }

    public void setTargetFragment(Fragment fragment) {
        this.targetFragment = fragment;
    }

    public boolean hasAllPermissions(String... permissions) {
        return PermissionUtil.hasAllPermissions(permissions);
    }

    public boolean hasAllPermissions() {
        return PermissionUtil.hasAllPermissions(permissions);
    }

    public void hideSnackbar() {
        if (permissionsSnackbar != null) {
            permissionsSnackbar.dismiss();
        }
    }

    public void setSnackbarContainer(CoordinatorLayout snackbarContainer) {
        this.snackbarContainer = snackbarContainer;
    }

    public void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean(PermissionUtil.STATE_IS_PERMISSIONS_DIALOG_SHOWING, isPermissionsDialogShowing);
    }

    public void restoreState(Bundle bundle) {
        isPermissionsDialogShowing = bundle.getBoolean(PermissionUtil.STATE_IS_PERMISSIONS_DIALOG_SHOWING);
    }

    public void checkPermissions() {
        askForPermissionsIfNeeded(permissionsCode, permissions);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        isPermissionsDialogShowing = false;
        PermissionUtil.setPermissionsResult(requestCode);
        checkPermissions();
    }

    private void askForPermissionsIfNeeded(@PermissionUtil.PermissionRequestCode int requestCode, String... permissions) {
        if (isPermissionsDialogShowing) {
            return;
        }

        if (permissionsSnackbar != null && permissionsSnackbar.isShown()) {
            permissionsSnackbar.dismiss();
        }

        if (PermissionUtil.hasAllPermissions(permissions)) {
            return;
        }

        final String[] deniedPermissions = PermissionUtil.getDeniedPermissions(permissions);
        boolean shouldShow = PermissionUtil.shouldShowRationale(targetFragment == null ?
                        targetActivity :
                        targetFragment.getActivity(),
                deniedPermissions);
        if (shouldShow) {
            if (snackbarContainer != null) {
                permissionsSnackbar = showSnackbarWithRequestPermissions(requestCode, deniedPermissions);
            } else {
                showPermissionDialog(getRationaleText(deniedPermissions), requestCode, true, deniedPermissions);
            }
        } else {
            if (PermissionUtil.hasPermissionsResult(requestCode)) {
                if (snackbarContainer != null) {
                    Activity snackbarActivity = targetFragment == null ? targetActivity : targetFragment.getActivity();
                    permissionsSnackbar = PermissionUtil.showSnackbarWithOpenDetails(snackbarActivity,
                            snackbarContainer, getRationaleText(deniedPermissions));
                } else {
                    showPermissionDialog(getRationaleText(deniedPermissions), requestCode, true, deniedPermissions);
                }
            } else {
                askForPermissions(requestCode, deniedPermissions);
            }
        }
    }

    private Snackbar showSnackbarWithRequestPermissions(@PermissionUtil.PermissionRequestCode final int requestCode,
                                                        final String... deniedPermissions) {
        return PermissionUtil.showSnackbar(snackbarContainer, getRationaleText(deniedPermissions),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        askForPermissions(requestCode, deniedPermissions);
                    }
                });
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void askForPermissions(@PermissionUtil.PermissionRequestCode int requestCode, String... deniedPermissions) {
        if (targetFragment != null) {
            targetFragment.requestPermissions(deniedPermissions, requestCode);
        } else if (Utils.hasLollipop()) {
            targetActivity.requestPermissions(deniedPermissions, requestCode);
        }
        isPermissionsDialogShowing = true;
    }

    private int getRationaleText(String[] permissions) {
        int rationaleText = -1;
        for (String permission : permissions) {
            int value = permissionsWithRationale.get(permission);
            if (value != -1) {
                rationaleText = value;
                break;
            }
        }

        return rationaleText;
    }

    private void showPermissionDialog(@StringRes int message, @PermissionUtil.PermissionRequestCode final int requestCode,
                                      final boolean openSettings, final String... deniedPermissions) {
        if (permissionDialog != null) {
            permissionDialog.dismiss();
            permissionDialog = null;
        }
        Activity activity = targetFragment == null ? targetActivity : targetFragment.getActivity();
        permissionDialog = new AlertDialog.Builder(activity, R.style.AppTheme)
                .setTitle(R.string.permission_dialog_title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(R.string.action_enable, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (openSettings) {
                            Activity activity = targetFragment == null ? targetActivity : targetFragment.getActivity();
                            PermissionUtil.openDetailsSettings(activity);
                        } else {
                            askForPermissions(requestCode, deniedPermissions);
                        }

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        permissionDialog.show();
    }

    public void setTargetActivity(Activity activity) {
        this.targetActivity = activity;
    }
}
