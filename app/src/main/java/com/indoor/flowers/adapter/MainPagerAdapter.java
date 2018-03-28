package com.indoor.flowers.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.fragment.CalendarNearbyNotificationsFragment;
import com.indoor.flowers.fragment.FlowersFragment;
import com.indoor.flowers.fragment.GroupsFragment;

public class MainPagerAdapter extends FragmentStatePagerAdapter {

    private static final int ITEMS_COUNT = 3;

    public static final int POSITION_NOTIFICATIONS = 0;
    public static final int POSITION_FLOWERS = 1;
    public static final int POSITION_GROUPS = 2;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment result = null;
        switch (position) {
            case POSITION_NOTIFICATIONS:
                result = CalendarNearbyNotificationsFragment.newInstance();
                break;
            case POSITION_FLOWERS:
                result = FlowersFragment.newInstance();
                break;
            case POSITION_GROUPS:
                result = GroupsFragment.newInstance();
                break;
        }

        return result;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String result = null;
        switch (position) {
            case POSITION_NOTIFICATIONS:
                result = Res.getString(R.string.fm_notifications);
                break;
            case POSITION_FLOWERS:
                result = Res.getString(R.string.fm_flowers);
                break;
            case POSITION_GROUPS:
                result = Res.getString(R.string.fm_groups);
                break;
        }
        return result;
    }

    @Override
    public int getCount() {
        return ITEMS_COUNT;
    }

    public boolean canShowFloatingButton(int position) {
        return position == POSITION_FLOWERS || position == POSITION_GROUPS;
    }
}
