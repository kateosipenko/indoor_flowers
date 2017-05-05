package com.indoor.flowers.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.indoor.flowers.R;

import butterknife.ButterKnife;

public class AddFlowerPagerAdapter extends PagerAdapter {

    private static final int PAGES_COUNT = 3;

    @Override
    public int getCount() {
        return PAGES_COUNT;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int layoutId = 0;
        Holder holder = null;
        switch (position) {
            case 0:
                layoutId = R.layout.layout_add_flower_data;
                holder = new FlowerDataHolder();
                break;
        }

        if (holder != null) {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_add_flower_data, container, false);
            holder.setItemView(view);
            view.setTag(holder);
            container.addView(view);
            return view;
        }

        TextView textView = new TextView(container.getContext());
        textView.setText(String.valueOf(position));
        container.addView(textView);
        return textView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    static class FlowerDataHolder extends Holder {

    }

    static class Holder {
        public void setItemView(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
