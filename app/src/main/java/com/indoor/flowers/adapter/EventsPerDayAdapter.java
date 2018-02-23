package com.indoor.flowers.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.evgeniysharafan.utils.ColorUtils;
import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.model.Notification;
import com.indoor.flowers.model.NotificationType;
import com.indoor.flowers.model.NotificationWithTarget;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.util.RecyclerListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsPerDayAdapter extends RecyclerListAdapter<NotificationWithTarget, EventsPerDayAdapter.ViewHolder> {

    @Override
    public int getRowLayoutRes() {
        return R.layout.row_event_per_day;
    }

    @Override
    public ViewHolder onCreateViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.update(getItemByPosition(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.repd_title)
        TextView titleView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void update(NotificationWithTarget event) {
            if (event != null && event.getNotification() != null) {
                titleView.setBackgroundColor(getColorForEvent(event.getNotification()));
                SpannableStringBuilder builder = new SpannableStringBuilder(getEventTitle(event));
                Drawable drawable = null;
                String title = null;
                if (event.getTarget() != null && event.getTarget() instanceof Flower) {
                    Flower flower = (Flower) event.getTarget();
                    if (!TextUtils.isEmpty(flower.getImagePath())) {
                        Bitmap bitmap = BitmapFactory.decodeFile(flower.getImagePath());
                        drawable = new BitmapDrawable(Res.get(), bitmap);
                    } else {
                        drawable = Res.getDrawable(R.drawable.ic_flower);
                    }

                    title = flower.getName();
                } else {
                    drawable = Res.getDrawable(R.drawable.ic_grouping);
                    title = ((Group) event.getTarget()).getName();
                }

                setupTitle(builder, drawable, title);
                titleView.setText(builder);
            } else {
                titleView.setBackgroundTintList(null);
                titleView.setText(null);
            }
        }

        private String getEventTitle(NotificationWithTarget event) {
            String result = null;
            switch (event.getNotification().getType()) {
                case NotificationType.CREATED:
                    result = event.getTarget() instanceof Group ? Res.getString(R.string.event_created_group)
                            : Res.getString(R.string.event_created_flower);
                    break;
                case NotificationType.FERTILIZER:
                    result = Res.getString(R.string.event_fertilizer);
                    break;
                case NotificationType.TRANSPLANTING:
                    result = Res.getString(R.string.event_transplanting);
                    break;
                case NotificationType.WATERING:
                    result = Res.getString(R.string.event_watering);
                    break;
            }
            return result;
        }

        private void setupTitle(SpannableStringBuilder builder, Drawable drawable,
                                String title) {
            drawable.setBounds(0, 0, titleView.getLineHeight(), titleView.getLineHeight());
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);

            SpannableString spannableString = new SpannableString("   ");
            spannableString.setSpan(span, 0, 3, 0);
            builder.append(" ");
            builder.append(spannableString);
            builder.append(" ");

            builder.append(title);
        }

        @ColorInt
        private int getColorForEvent(Notification event) {
            @ColorInt int color = Color.TRANSPARENT;
            switch (event.getType()) {
                case NotificationType.CREATED:
                    color = Res.getColor(R.color.event_created);
                    break;
                case NotificationType.FERTILIZER:
                    color = Res.getColor(R.color.event_fertilizer);
                    break;
                case NotificationType.TRANSPLANTING:
                    color = Res.getColor(R.color.event_transplantation);
                    break;
                case NotificationType.WATERING:
                    color = Res.getColor(R.color.event_watering);
                    break;
            }

            return ColorUtils.setColorAlpha(color, 0.5f);
        }
    }
}
