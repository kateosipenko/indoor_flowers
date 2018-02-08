package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public abstract class ToolbarFragment extends Fragment {

    protected AppCompatActivity getCompatActivity() {
        return (AppCompatActivity) getActivity();
    }

    protected void setupActionBar(@StringRes int title, boolean homeEnabled) {
        AppCompatActivity compatActivity = getCompatActivity();
        if (compatActivity == null || compatActivity.isDestroyed()) {
            return;
        }

        ActionBar actionBar = compatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setHomeButtonEnabled(homeEnabled);
            actionBar.setDisplayHomeAsUpEnabled(homeEnabled);
            actionBar.show();
        }
    }

    protected void restoreState(Bundle bundle) {
    }
}
