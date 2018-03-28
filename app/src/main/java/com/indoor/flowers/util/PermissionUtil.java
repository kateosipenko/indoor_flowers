package com.indoor.flowers.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.evgeniysharafan.utils.L;
import com.evgeniysharafan.utils.PrefUtils;
import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

@SuppressWarnings("unused")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class PermissionUtil {

    public static final String[] START_PERMISSIONS;
    public static final String[] LOCATION_PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    public static final String[] STORAGE_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final String[] PHONE_STATE_PERMISSIONS = {Manifest.permission.READ_PHONE_STATE};
    public static final String[] CAMERA_PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final String[] CONTACTS_PERMISSIONS = {Manifest.permission.READ_CONTACTS};
    public static final String STATE_IS_PERMISSIONS_DIALOG_SHOWING = "state_is_permissions_dialog_showing";

    static {
        List<String> startPermissions = new ArrayList<>();
        Collections.addAll(startPermissions, STORAGE_PERMISSIONS);
        Collections.addAll(startPermissions, PHONE_STATE_PERMISSIONS);
        Collections.addAll(startPermissions, LOCATION_PERMISSIONS);
        START_PERMISSIONS = startPermissions.toArray(new String[startPermissions.size()]);
    }

    private PermissionUtil() {
    }

    public static boolean hasAllPermissions(String... permissions) {
        for (String permission : permissions) {
            if (!isPermissionGranted(permission)) {
                return false;
            }
        }

        return true;
    }

    public static String[] getDeniedPermissions(String... permissions) {
        ArrayList<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (!isPermissionGranted(permission)) {
                deniedPermissions.add(permission);
            }
        }

        return deniedPermissions.toArray(new String[deniedPermissions.size()]);
    }

    public static boolean shouldShowRationale(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }

        return false;
    }

    public static void setPermissionsResult(@PermissionRequestCode int requestCode) {
        PrefUtils.put(String.valueOf(requestCode), true);
    }

    public static boolean hasPermissionsResult(@PermissionRequestCode int requestCode) {
        return PrefUtils.getBool(String.valueOf(requestCode), false);
    }

    public static Snackbar showSnackbarWithOpenDetails(final Activity activity,
                                                       View container, @StringRes int text) {
        return showSnackbar(container, text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetailsSettings(activity);
            }
        });
    }

    public static Snackbar showSnackbar(View container, @StringRes int text, View.OnClickListener click) {
        if (container == null) {
            L.e("container == null, return null");
            return null;
        }

        Snackbar snackbar = Snackbar.make(container, text, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.action_enable, click).show();

        return snackbar;
    }

    public static void openDetailsSettings(Activity activity) {
        Uri data = new Uri.Builder().scheme("package").opaquePart(Utils.getPackageName()).build();
        activity.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(data));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(Utils.getApp(), permission) == PERMISSION_GRANTED;
    }

    @IntDef({PermissionRequestCode.START, PermissionRequestCode.LOCATION, PermissionRequestCode.STORAGE,
            PermissionRequestCode.CAMERA, PermissionRequestCode.CONTACTS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PermissionRequestCode {
        int START = 1;
        int LOCATION = 2;
        int STORAGE = 3;
        int CAMERA = 4;
        int CONTACTS = 5;
    }

}
