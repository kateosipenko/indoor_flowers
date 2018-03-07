package com.indoor.flowers.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.evgeniysharafan.utils.picasso.CircleTransformation;
import com.indoor.flowers.R;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.model.GroupWithWatering;
import com.indoor.flowers.util.CalendarUtils;
import com.indoor.flowers.util.RecyclerListAdapter;
import com.indoor.flowers.view.NotificationStatus;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupsWithWateringAdapter extends RecyclerListAdapter<GroupWithWatering,
        GroupsWithWateringAdapter.ViewHolder> {

    @Override
    public int getRowLayoutRes() {
        return R.layout.row_group;
    }

    @Override
    public ViewHolder onCreateViewHolder(View view) {
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.update(getItemByPosition(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rg_title)
        TextView nameView;
        @BindView(R.id.rg_icon)
        ImageView iconView;
        @BindView(R.id.rg_watering_status)
        NotificationStatus wateringStatus;

        private GroupsWithWateringAdapter adapter;

        public ViewHolder(View itemView, GroupsWithWateringAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.adapter = adapter;
        }

        @OnClick({R.id.rg_title, R.id.rg_icon, R.id.rg_root})
        void onRootClicked() {
            GroupWithWatering group = adapter.getItemByPosition(getAdapterPosition());
            if (adapter.listener != null) {
                adapter.listener.onItemClicked(group);
            }
        }

        private void update(GroupWithWatering groupWithWatering) {
            Group group = groupWithWatering.getGroup();
            nameView.setText(group != null ? group.getName() : null);
            if (group != null && !TextUtils.isEmpty(group.getImagePath())) {
                Picasso.with(itemView.getContext())
                        .load(new File(group.getImagePath()))
                        .transform(new CircleTransformation(0, 0))
                        .into(iconView);
            } else {
                iconView.setImageBitmap(null);
            }

            if (groupWithWatering.getDate() != null) {
                Calendar nextWatering = (Calendar) groupWithWatering.getDate().clone();
                nextWatering.add(Calendar.DAY_OF_YEAR, groupWithWatering.getFrequency());


                int daysDiff = CalendarUtils.getDaysDiff(Calendar.getInstance(), nextWatering);
                wateringStatus.setProgress(groupWithWatering.getFrequency(), daysDiff);
                wateringStatus.setVisibility(View.VISIBLE);
            } else {
                wateringStatus.setVisibility(View.GONE);
            }

        }
    }
}
