package com.indoor.flowers.adapter;

import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.indoor.flowers.R;
import com.indoor.flowers.model.Event;
import com.indoor.flowers.util.EventsUtils;
import com.indoor.flowers.util.RecyclerListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EventsAdapter extends RecyclerListAdapter<Event, EventsAdapter.ViewHolder> {

    @Override
    public int getRowLayoutRes() {
        return R.layout.row_event;
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

        @BindView(R.id.re_comment)
        TextView commentView;
        @BindView(R.id.re_title)
        TextView titleView;
        @BindView(R.id.re_icon)
        View iconView;

        private EventsAdapter adapter;

        public ViewHolder(View itemView, EventsAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.adapter = adapter;
        }

        @OnClick(R.id.re_root)
        void onEventClicked() {
            if (adapter.listener != null) {
                adapter.listener.onItemClicked(adapter.getItemByPosition(getAdapterPosition()));
            }
        }

        private void update(Event event) {
            iconView.setBackgroundTintList(ColorStateList.valueOf(EventsUtils.getColorForEventType(event.getEventType())));
            titleView.setText(event.getTitle());
            commentView.setText(event.getComment());
        }
    }
}
