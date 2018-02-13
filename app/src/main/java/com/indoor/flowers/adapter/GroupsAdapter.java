package com.indoor.flowers.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.indoor.flowers.R;
import com.indoor.flowers.model.Group;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupViewHolder> {

    private static final int VIEW_TYPE_NO_GROUP = 0;
    private static final int VIEW_TYPE_GROUP = 1;

    private GroupClickListener listener;
    private List<Group> groups = new ArrayList<>();

    private int selectedPosition = 0;

    public void setListener(GroupClickListener listener) {
        this.listener = listener;
    }

    public void setGroups(List<Group> items) {
        this.groups.clear();
        if (items != null) {
            this.groups.addAll(items);
        }

        notifyDataSetChanged();
    }

    public Group getSelectedGroup() {
        return selectedPosition >= 1 && selectedPosition < groups.size() + 1
                ? groups.get(selectedPosition - 1) : null;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_NO_GROUP;
        }

        return VIEW_TYPE_GROUP;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        @LayoutRes int layout = viewType == VIEW_TYPE_NO_GROUP ? R.layout.row_no_group : R.layout.row_group;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new GroupViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        holder.update(getItemByPosition(position));
    }

    @Override
    public int getItemCount() {
        return groups.size() + 1;
    }

    private Group getItemByPosition(int position) {
        if (position == 0) {
            return null;
        }

        int itemPosition = position - 1;
        return itemPosition >= 0 && itemPosition < groups.size() ? groups.get(itemPosition) : null;
    }

    private void setSelectedPosition(int position) {
        int old = selectedPosition;
        this.selectedPosition = position;
        notifyItemChanged(old);
        notifyItemChanged(selectedPosition);
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rg_title)
        @Nullable
        TextView nameView;
        @BindView(R.id.rg_root)
        View rootLayout;

        private GroupsAdapter adapter;

        public GroupViewHolder(View itemView, GroupsAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.adapter = adapter;
        }

        @Optional
        @OnClick({R.id.rg_title})
        void onItemDataClicked(View view) {
            onRootClicked();
        }

        @OnClick(R.id.rg_root)
        void onRootClicked() {
            Group group = adapter.getItemByPosition(getAdapterPosition());
            adapter.setSelectedPosition(getAdapterPosition());
            if (adapter.listener != null) {
                adapter.listener.onGroupClicked(group);
            }
        }

        private void update(Group group) {
            if (getAdapterPosition() == adapter.selectedPosition) {
                setAlpha(1f);
            } else {
                setAlpha(0.4f);
            }

            if (nameView != null) {
                nameView.setText(group.getName());
            }
        }

        private void setAlpha(float alpha) {
            if (rootLayout instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) rootLayout;
                for (int i = 0; i < group.getChildCount(); i++) {
                    group.getChildAt(i).setAlpha(alpha);
                }
            } else {
                rootLayout.setAlpha(alpha);
            }
        }
    }

    public interface GroupClickListener {
        void onGroupClicked(Group group);
    }
}
