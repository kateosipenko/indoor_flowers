package com.indoor.flowers.util;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.evgeniysharafan.utils.Res;
import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.R;
import com.indoor.flowers.activity.MainActivity;
import com.indoor.flowers.database.provider.NotificationsProvider;
import com.indoor.flowers.model.Notification;
import com.indoor.flowers.model.NotificationType;
import com.indoor.flowers.model.NotificationWithTarget;
import com.indoor.flowers.service.FlowersAlarmsService;

import java.util.List;
import java.util.Objects;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationsUtils {

    private static final String CHANEL_WATERING_ID = "6fc42723-0016-4964-b748-5f78fccb1b1f";
    private static final String CHANEL_FERTILIZING_ID = "2a55b7d6-1294-493f-a53f-09349171c3e9";
    private static final String CHANEL_TRANSPLANTING_ID = "f1e1e4fc-b244-495e-a861-a29cd4286f62";

    public static void cancelEventNotifications(Context context, List<Notification> events) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }

        for (Notification event : events) {
            manager.cancel(getNotificationId(event));
        }
    }

    public static void cancelEventNotifications(Context context, Notification event) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }

        manager.cancel(getNotificationId(event));
    }

    public static void showNotificationForEvent(Context context, long eventId) {
        onShowNotificationRequested(context, eventId);
    }

    private static void onShowNotificationRequested(Context context, long notificationId) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }

        NotificationsProvider provider = new NotificationsProvider(context);
        NotificationWithTarget notificationWithTarget = provider.getNotificationWithTarget(notificationId);
        provider.unbind();

        Notification notification = notificationWithTarget.getNotification();

        checkNotificationChannel(manager, notification);

        String channelId = getChannelId(notification);
        if (TextUtils.isEmpty(channelId)) {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        builder.setColorized(true);
        builder.setContentTitle(notification.getTitle());
        builder.setContentText(notification.getComment());
        switch (notification.getType()) {
            case NotificationType.FERTILIZER:
                builder.setSubText(Res.getString(R.string.event_fertilizer));
                break;
            case NotificationType.TRANSPLANTING:
                builder.setSubText(Res.getString(R.string.event_transplanting));
                break;
            case NotificationType.WATERING:
                builder.setSubText(Res.getString(R.string.event_watering));
                break;
        }

        builder.setSmallIcon(R.drawable.ic_notification_time);
        builder.setColor(EventsUtils.getColorForEventType(notification.getType()));

        if (!TextUtils.isEmpty(notificationWithTarget.getImagePath())) {
            builder.setLargeIcon(BitmapFactory.decodeFile(notificationWithTarget.getImagePath()));
        }

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                MainActivity.REQUEST_CODE_NOTIFICATION, new Intent(context, MainActivity.class), 0);
        builder.setContentIntent(contentIntent);

        builder.addAction(new NotificationCompat.Action(R.drawable.ic_add, Res.getString(R.string.action_done),
                PendingIntent.getService(context,
                        getNotificationId(notification),
                        FlowersAlarmsService.createDoneIntent(context, notification), 0)));

        manager.notify(getNotificationId(notification), builder.build());
    }

    @SuppressLint("NewApi")
    private static void checkNotificationChannel(NotificationManager notificationManager,
                                                 Notification notification) {
        String channelId = getChannelId(notification);
        if (Utils.getApiVersion() >= Build.VERSION_CODES.O && !TextUtils.isEmpty(channelId)) {
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if (channel == null) {
                channel = new NotificationChannel(channelId, getChannelTitle(notification),
                        NotificationManager.IMPORTANCE_DEFAULT);
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                channel.setSound(alarmSound, new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build());
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private static int getNotificationId(Notification event) {
        return Objects.hash(event.getId(), event.getType());
    }

    private static String getChannelTitle(Notification notification) {
        switch (notification.getType()) {
            case NotificationType.FERTILIZER:
                return Res.getString(R.string.notifications_channel_fertilizing);
            case NotificationType.TRANSPLANTING:
                return Res.getString(R.string.notifications_channel_transplanting);
            case NotificationType.WATERING:
                return Res.getString(R.string.notifications_channel_watering);
        }

        return null;
    }

    private static String getChannelId(Notification notification) {
        switch (notification.getType()) {
            case NotificationType.FERTILIZER:
                return CHANEL_FERTILIZING_ID;
            case NotificationType.TRANSPLANTING:
                return CHANEL_TRANSPLANTING_ID;
            case NotificationType.WATERING:
                return CHANEL_WATERING_ID;
        }

        return null;
    }
}
