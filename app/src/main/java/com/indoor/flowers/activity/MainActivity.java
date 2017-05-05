package com.indoor.flowers.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;

import com.evgeniysharafan.utils.Fragments;
import com.indoor.flowers.fragment.MainFragment;
import com.indoor.flowers.util.ActivityCommunicator;
import com.indoor.flowers.util.FragmentCommunicator;

public class MainActivity extends AppCompatActivity implements ActivityCommunicator {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Fragments.replace(getSupportFragmentManager(), android.R.id.content,
                    MainFragment.newInstance(), null);
        }
    }

    @Override
    public void setActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return canGoBack() && super.onKeyDown(keyCode, event);
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (canGoBack()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = Fragments.getById(getSupportFragmentManager(), android.R.id.content);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean canGoBack() {
        Fragment fragment = Fragments.getById(getSupportFragmentManager(), android.R.id.content);
        return !(fragment instanceof FragmentCommunicator && ((FragmentCommunicator) fragment).onBackPressed());
    }
}
