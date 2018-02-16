package com.indoor.flowers.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.receiver.AlarmBroadcastReceiver;

import java.util.concurrent.TimeUnit;

import static android.content.Context.ALARM_SERVICE;

public class FlowersAlarmsUtils {

    // region FLOWER

    public static void deleteAlarmsForFlower(Context context, Flower flower) {
        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (manager != null) {
            PendingIntent pendingIntent = AlarmBroadcastReceiver.getNotificationIntent(context,
                    false, flower.getId());
            manager.cancel(pendingIntent);
        }

        NotificationsUtils.cancelAllForFlower(context, flower);
    }

    public static void refreshAlarmsForFlower(Context context, Flower flower) {
        if (flower == null || flower.getSettingData() == null) {
            return;
        }

        updateAlarmsForItem(context, false, flower.getId(),
                flower.getSettingData().getNextWateringTime().getTimeInMillis(),
                flower.getSettingData().getWateringFrequency());
    }

    // endregion FLOWER

    // region GROUP

    public static void deleteAlarmsForGroup(Context context, Group group) {
        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (manager != null) {
            PendingIntent pendingIntent = AlarmBroadcastReceiver.getNotificationIntent(context,
                    true, group.getId());
            manager.cancel(pendingIntent);
        }

        NotificationsUtils.cancelAllForGroup(context, group);
    }

    public static void refreshAlarmsForGroup(Context context, Group group) {
        if (group == null || group.getSettingData() == null) {
            return;
        }

        updateAlarmsForItem(context, true, group.getId(),
                group.getSettingData().getNextWateringTime().getTimeInMillis(),
                group.getSettingData().getWateringFrequency());
    }

    // endregion GROUP

    private static void updateAlarmsForItem(Context context, boolean isGroupNotification, long id,
                                            long alarmAt, long periodInDays) {
        PendingIntent pendingIntent = AlarmBroadcastReceiver.getNotificationIntent(context,
                isGroupNotification, id);
        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (manager != null) {
            manager.cancel(pendingIntent);
            manager.setRepeating(AlarmManager.RTC, alarmAt,
                    TimeUnit.DAYS.toMillis(periodInDays),
                    pendingIntent);
        }
    }
}
