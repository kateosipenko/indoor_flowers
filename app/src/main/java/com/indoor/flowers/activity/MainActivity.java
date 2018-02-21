package com.indoor.flowers.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.evgeniysharafan.utils.Fragments;
import com.indoor.flowers.fragment.MainFragment;
import com.indoor.flowers.util.FragmentCommunicator;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_NOTIFICATION = 8745;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Fragments.replace(getSupportFragmentManager(), android.R.id.content,
                    MainFragment.newInstance(), null, false);
        }
    }

    @Override
    public void onBackPressed() {
        if (canGoBack()) {
            super.onBackPressed();
        }
    }

    private boolean canGoBack() {
        Fragment fragment = Fragments.getById(getSupportFragmentManager(), android.R.id.content);
        return !(fragment instanceof FragmentCommunicator && ((FragmentCommunicator) fragment).onBackPressed());
    }
}
