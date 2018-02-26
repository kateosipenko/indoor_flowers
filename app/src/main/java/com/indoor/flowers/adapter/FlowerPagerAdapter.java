package com.indoor.flowers.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.util.SpaceItemDecoration;

import java.util.Objects;

public class FlowerPagerAdapter extends PagerAdapter {

    public static final int POSITION_EVENTS = 0;
    public static final int POSITION_GROUPS = 1;

    private static final int ITEMS_COUNT = 2;

    private GroupsAdapter groupsAdapter;
    private EventsAdapter eventsAdapter;

    public void setAdapters(GroupsAdapter groupsAdapter, EventsAdapter eventsAdapter) {
        this.groupsAdapter = groupsAdapter;
        this.eventsAdapter = eventsAdapter;
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String result = null;
        switch (position) {
            case POSITION_GROUPS:
                result = Res.getString(R.string.faf_tab_groups);
                break;
            case POSITION_EVENTS:
                result = Res.getString(R.string.faf_tab_notifications);
                break;
        }
        return result;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        RecyclerView recyclerView = new RecyclerView(container.getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(getAdapterForPosition(position));
        int padding = Res.getDimensionPixelSize(R.dimen.margin_default);
        recyclerView.setPadding(padding, padding, padding, padding);
        recyclerView.setClipToPadding(false);
        recyclerView.setClipChildren(false);
        recyclerView.setFocusableInTouchMode(true);
        recyclerView.setClickable(true);
        recyclerView.addItemDecoration(new SpaceItemDecoration(Res.getDimensionPixelSize(R.dimen.margin_normal)));
        container.addView(recyclerView);
        return recyclerView;
    }

    @Override
    public int getCount() {
        return ITEMS_COUNT;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return Objects.equals(view, object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private RecyclerView.Adapter getAdapterForPosition(int position) {
        switch (position) {
            case POSITION_GROUPS:
                return groupsAdapter;
            case POSITION_EVENTS:
                return eventsAdapter;
        }

        return null;
    }
}
