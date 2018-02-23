package com.indoor.flowers.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.indoor.flowers.database.provider.NotificationsProvider;
import com.indoor.flowers.model.Notification;
import com.indoor.flowers.receiver.AlarmBroadcastReceiver;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static android.content.Context.ALARM_SERVICE;

public class FlowersAlarmsUtils {

    public static void deleteAlarmsForEvents(Context context, List<Notification> events) {
        NotificationsUtils.cancelEventNotifications(context, events);
        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (manager == null) {
            return;
        }

        for (Notification event : events) {
            manager.cancel(createPendingIntentForEvent(context, event));
        }
    }

    public static void deleteEventAlarms(Context context, Notification event) {
        NotificationsUtils.cancelEventNotifications(context, event);
        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (manager == null) {
            return;
        }

        manager.cancel(createPendingIntentForEvent(context, event));
    }

    public static void refreshAlarmsForEvent(Context context, long eventId) {
        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (manager == null) {
            return;
        }

        NotificationsProvider provider = new NotificationsProvider(context);
        Notification event = provider.getNotificationById(eventId);
        provider.unbind();
        PendingIntent pendingIntent = createPendingIntentForEvent(context, event);
        manager.cancel(pendingIntent);
        if (event.getFrequency() != null) {
            manager.setRepeating(AlarmManager.RTC, event.getDate().getTimeInMillis(),
                    TimeUnit.DAYS.toMillis(event.getFrequency()),
                    createPendingIntentForEvent(context, event));
        } else {
            manager.setExact(AlarmManager.RTC, event.getDate().getTimeInMillis(),
                    createPendingIntentForEvent(context, event));
        }
    }

    private static PendingIntent createPendingIntentForEvent(Context context, Notification event) {
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.setAction(AlarmBroadcastReceiver.ACTION_SHOW_NOTIFICATION);
        intent.putExtra(AlarmBroadcastReceiver.KEY_EVENT_ID, event.getId());
        return PendingIntent.getBroadcast(context, getEventRequestCode(event), intent, 0);
    }

    private static int getEventRequestCode(Notification event) {
        return Objects.hash(event.getId());
    }
}
