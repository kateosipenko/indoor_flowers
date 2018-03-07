package com.indoor.flowers.adapter;

import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.model.Notification;
import com.indoor.flowers.util.EventsUtils;
import com.indoor.flowers.util.RecyclerListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EventsAdapter extends RecyclerListAdapter<Notification, EventsAdapter.ViewHolder> {

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
        @BindView(R.id.re_start_date)
        TextView startDateView;
        @BindView(R.id.re_end_date)
        TextView endDateView;
        @BindView(R.id.re_icon)
        ImageView iconView;

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

        private void update(Notification event) {
            iconView.setImageResource(EventsUtils.getIconForEventType(event.getType()));
            iconView.setBackgroundTintList(ColorStateList.valueOf(EventsUtils.getColorForEventType(event.getType())));
            titleView.setText(event.getTitle());
            commentView.setText(event.getComment());
            if (event.getEndDate() != null) {
                endDateView.setText(Res.getString(R.string.re_end_date_format, event.getEndDate()));
                endDateView.setVisibility(View.VISIBLE);
            } else {
                endDateView.setText(null);
                endDateView.setVisibility(View.GONE);
            }

            StringBuilder dataBuilder = new StringBuilder();
            dataBuilder.append(Res.getString(R.string.re_start_date_format, event.getDate()));
            if (event.getFrequency() != null) {
                dataBuilder.append(" ");
                appendFrequency(dataBuilder, event.getFrequency());
            }

            startDateView.setText(dataBuilder.toString());
        }

        private void appendFrequency(StringBuilder builder, Integer frequency) {
            if (frequency == 1) {
                builder.append(Res.getString(R.string.re_frequency_one));
            } else {
                int lastDigit = frequency % 10;
                if (lastDigit == 1 && frequency > 20) {
                    builder.append(Res.getString(R.string.re_frequency_one_format, frequency));
                } else if (lastDigit < 5 && frequency >= 20) {
                    builder.append(Res.getString(R.string.re_frequency_four_format, frequency));
                } else {
                    builder.append(Res.getString(R.string.re_frequency_more_format, frequency));
                }
            }
        }
    }
}
