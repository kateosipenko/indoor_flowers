package com.indoor.flowers.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.indoor.flowers.model.PhotoItem;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GalleryPagerAdapter extends PagerAdapter {

    private List<PhotoItem> photoItems = new ArrayList<>();

    public void setPhotos(List<PhotoItem> items) {
        this.photoItems.clear();
        if (items != null) {
            this.photoItems.addAll(items);
        }

        notifyDataSetChanged();
    }

    public PhotoItem getItemByPosition(int position) {
        return position >= 0 && position < getCount() ?
                photoItems.get(position) : null;
    }

    public int getPositionById(long id) {
        int selectedPosition = -1;
        for (int i = 0; i < photoItems.size(); i++) {
            PhotoItem item = photoItems.get(i);
            if (item.getId() == id) {
                selectedPosition = i;
                break;
            }
        }

        return selectedPosition;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
        imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        imageView.setScaleType(ScaleType.FIT_CENTER);

        PhotoItem photoItem = getItemByPosition(position);
        if (photoItem != null) {
            Picasso.with(container.getContext())
                    .load(new File(photoItem.getImagePath()))
                    .into(imageView);
        }

        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return photoItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return Objects.equals(view, object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
