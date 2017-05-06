package com.indoor.flowers.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.indoor.flowers.fragment.creation.CaringDataFragment;
import com.indoor.flowers.fragment.creation.FlowerDataFragment;
import com.indoor.flowers.fragment.creation.RoomDataFragment;

public class AddFlowerPagerAdapter extends FragmentStatePagerAdapter {

    private static final int PAGES_COUNT = 3;

    public AddFlowerPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGES_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = FlowerDataFragment.newInstance();
                break;
            case 1:
                fragment = RoomDataFragment.newInstance();
                break;
            case 2:
                fragment = CaringDataFragment.newInstance();
                break;
        }

        return fragment;
    }
}
