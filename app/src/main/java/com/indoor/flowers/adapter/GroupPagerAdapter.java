package com.indoor.flowers.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.util.SpaceItemDecoration;

import java.util.Objects;

public class GroupPagerAdapter extends PagerAdapter {

    public static final int POSITION_FLOWERS = 0;
    public static final int POSITION_EVENTS = 1;
    public static final int POSITION_GALLERY = 2;

    private static final int ITEMS_COUNT = 3;

    private FlowersAdapter flowersAdapter;
    private EventsAdapter eventsAdapter;
    private GalleryAdapter galleryAdapter;

    public void setAdapters(FlowersAdapter flowersAdapter, EventsAdapter eventsAdapter,
                            GalleryAdapter galleryAdapter) {
        this.flowersAdapter = flowersAdapter;
        this.eventsAdapter = eventsAdapter;
        this.galleryAdapter = galleryAdapter;
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String result = null;
        switch (position) {
            case POSITION_FLOWERS:
                result = Res.getString(R.string.fg_group_flowers);
                break;
            case POSITION_EVENTS:
                result = Res.getString(R.string.fg_notifications);
                break;
            case POSITION_GALLERY:
                result = Res.getString(R.string.fg_gallery);
                break;
        }
        return result;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        RecyclerView recyclerView = new RecyclerView(container.getContext());
        recyclerView.setLayoutManager(getLayoutManager(container.getContext(), position));
        recyclerView.setAdapter(getAdapterForPosition(position));
        recyclerView.setClipToPadding(false);
        recyclerView.setClipChildren(false);
        recyclerView.setFocusableInTouchMode(true);
        recyclerView.setClickable(true);
        int padding = Res.getDimensionPixelSize(R.dimen.margin_default);
        recyclerView.setPadding(padding, padding, padding, padding);
        recyclerView.addItemDecoration(new SpaceItemDecoration(padding, padding));
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

    private RecyclerView.LayoutManager getLayoutManager(Context context, int position) {
        switch (position) {
            case POSITION_GALLERY:
                return new GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false);
            default:
                return new LinearLayoutManager(context);
        }
    }

    private RecyclerView.Adapter getAdapterForPosition(int position) {
        switch (position) {
            case POSITION_FLOWERS:
                return flowersAdapter;
            case POSITION_EVENTS:
                return eventsAdapter;
            case POSITION_GALLERY:
                return galleryAdapter;
        }

        return null;
    }
}
