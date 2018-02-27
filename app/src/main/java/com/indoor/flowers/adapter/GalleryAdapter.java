package com.indoor.flowers.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.indoor.flowers.R;
import com.indoor.flowers.model.PhotoItem;
import com.indoor.flowers.util.RecyclerListAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GalleryAdapter extends RecyclerListAdapter<PhotoItem, GalleryAdapter.ViewHolder> {

    public void add(PhotoItem photoItem) {
        this.items.add(photoItem);
        notifyItemInserted(this.items.size() - 1);
    }

    @Override
    public int getRowLayoutRes() {
        return R.layout.row_gallery;
    }

    @Override
    public ViewHolder onCreateViewHolder(View view) {
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.update(getItemByPosition(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rg_image)
        ImageView imageView;

        private GalleryAdapter adapter;

        public ViewHolder(View itemView, GalleryAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.adapter = adapter;
        }

        @OnClick(R.id.rg_image)
        void onImageClicked() {
            PhotoItem item = adapter.getItemByPosition(getAdapterPosition());
            if (item != null && adapter.listener != null) {
                adapter.listener.onItemClicked(item);
            }
        }

        private void update(PhotoItem item) {
            if (item == null || TextUtils.isEmpty(item.getImagePath())) {
                imageView.setImageBitmap(null);
            } else {
                Picasso.with(itemView.getContext())
                        .load(new File(item.getImagePath()))
                        .into(imageView);
            }
        }
    }
}
