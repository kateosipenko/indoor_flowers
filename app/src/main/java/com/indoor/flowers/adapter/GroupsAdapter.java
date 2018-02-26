package com.indoor.flowers.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.evgeniysharafan.utils.picasso.CircleTransformation;
import com.indoor.flowers.R;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.util.RecyclerListAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupsAdapter extends RecyclerListAdapter<Group, GroupsAdapter.GroupViewHolder> {

    @Override
    public int getRowLayoutRes() {
        return R.layout.row_group;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(View view) {
        return new GroupViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        holder.update(getItemByPosition(position));
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rg_title)
        TextView nameView;
        @BindView(R.id.rg_icon)
        ImageView iconView;

        private GroupsAdapter adapter;

        public GroupViewHolder(View itemView, GroupsAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.adapter = adapter;
        }

        @OnClick({R.id.rg_title, R.id.rg_icon, R.id.rg_root})
        void onRootClicked() {
            Group group = adapter.getItemByPosition(getAdapterPosition());
            if (adapter.listener != null) {
                adapter.listener.onItemClicked(group);
            }
        }

        private void update(Group group) {
            nameView.setText(group != null ? group.getName() : null);
            if (group != null && !TextUtils.isEmpty(group.getImagePath())) {
                Picasso.with(itemView.getContext())
                        .load(new File(group.getImagePath()))
                        .transform(new CircleTransformation(0, 0))
                        .into(iconView);
            } else {
                iconView.setImageBitmap(null);
            }
        }
    }
}
