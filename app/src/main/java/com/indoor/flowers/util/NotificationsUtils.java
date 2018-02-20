package com.indoor.flowers.util;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.evgeniysharafan.utils.Res;
import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.R;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Event;
import com.indoor.flowers.model.EventType;
import com.indoor.flowers.service.FlowersAlarmsService;

import java.util.List;
import java.util.Objects;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationsUtils {

    private static final String NOTIFICATION_CHANEL_ID = "6fc42723-0016-4964-b748-5f78fccb1b1f";
    private static final String NOTIFICATION_CHANEL_NAME = "FlowersWateringChannel";

    public static void cancelEventNotifications(Context context, List<Event> events) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }

        for (Event event : events) {
            manager.cancel(getNotificationId(event));
        }
    }

    public static void cancelEventNotifications(Context context, Event event) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }

        manager.cancel(getNotificationId(event));
    }

    public static void showNotificationForEvent(Context context, long eventId) {
        onShowNotificationRequested(context, eventId);
    }

    private static void onShowNotificationRequested(Context context, long eventId) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }

        checkNotificationChannel(manager);

        FlowersProvider provider = new FlowersProvider(context);
        Event event = provider.getEventById(eventId);
        provider.unbind();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANEL_ID);
        builder.setColorized(true);
        builder.setContentTitle(event.getTitle());
        builder.setContentText(event.getComment());
        switch (event.getEventType()) {
            case EventType.NUTRITION:
                builder.setSubText(Res.getString(R.string.event_nutrition));
                break;
            case EventType.TRANSPLANTING:
                builder.setSubText(Res.getString(R.string.event_transplanting));
                break;
            case EventType.WATERING:
                builder.setSubText(Res.getString(R.string.event_watering));
                break;
        }

        builder.setSmallIcon(R.drawable.ic_notification_watering);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.addAction(R.drawable.ic_notification_watering, Res.getString(R.string.action_done),
                PendingIntent.getService(context, FlowersAlarmsService.REQUEST_CODE_SET_WATERING_DONE,
                        FlowersAlarmsService.createDoneIntent(context, event), 0));

        manager.notify(getNotificationId(event), builder.build());
    }

    @SuppressLint("NewApi")
    private static void checkNotificationChannel(NotificationManager notificationManager) {
        if (Utils.getApiVersion() >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(NOTIFICATION_CHANEL_ID);
            if (channel == null) {
                channel = new NotificationChannel(NOTIFICATION_CHANEL_ID, NOTIFICATION_CHANEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT);
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                channel.setSound(alarmSound, new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build());
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private static int getNotificationId(Event event) {
        return Objects.hash(event.getId(), event.getEventType());
    }
}
