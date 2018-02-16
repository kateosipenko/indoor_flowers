package com.indoor.flowers.util;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;

import com.evgeniysharafan.utils.Res;
import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.R;
import com.indoor.flowers.activity.MainActivity;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.receiver.AlarmBroadcastReceiver;
import com.indoor.flowers.service.FlowersAlarmsService;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.support.v4.app.NotificationCompat.BADGE_ICON_LARGE;
import static android.support.v4.app.NotificationCompat.BADGE_ICON_SMALL;

public class NotificationsUtils {

    private static final String NOTIFICATION_CHANEL_ID = "6fc42723-0016-4964-b748-5f78fccb1b1f";
    private static final String NOTIFICATION_CHANEL_NAME = "FlowersWateringChannel";

    private static final String EXTRA_FLOWER_ID = "action_flower_id";
    private static final String EXTRA_GROUP_ID = "action_group_id";

    public static void cancelAllForFlower(Context context, Flower flower) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }

        manager.cancel(Objects.hash(flower.getId(), Flower.class.getSimpleName(),
                NotificationType.WATERING));
    }

    public static void cancelAllForGroup(Context context, Group group) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }

        manager.cancel(Objects.hash(group.getId(), Group.class.getSimpleName(),
                NotificationType.WATERING));
    }

    public static void showNotificationForFlower(Context context, long flowerId) {
        int notificationId = Objects.hash(flowerId, Flower.class.getSimpleName(),
                NotificationType.WATERING);
        onShowNotificationRequested(context, flowerId, notificationId, false);
    }

    public static void showNotificationForGroup(Context context, long groupId) {
        int notificationId = Objects.hash(groupId, Group.class.getSimpleName(),
                NotificationType.WATERING);
        onShowNotificationRequested(context, groupId, notificationId, true);
    }

    private static void onShowNotificationRequested(Context context, long itemId,
                                                    int notificationId,
                                                    boolean isGroupNotification) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }

        checkNotificationChannel(manager);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANEL_ID);
        setCommonNotificationData(context, builder, itemId, isGroupNotification);

        FlowersProvider provider = new FlowersProvider(context);
        if (isGroupNotification) {
            Group group = provider.getGroupById(itemId);
            fillNotificationBuilder(builder, group);
            builder.addAction(R.drawable.ic_notification_watering, Res.getString(R.string.action_done),
                    PendingIntent.getService(context, FlowersAlarmsService.REQUEST_CODE_SET_WATERING_DONE,
                            FlowersAlarmsService.createWateringDoneIntent(context, group), 0));
        } else {
            Flower flower = provider.getFlowerById(itemId);
            fillNotificationBuilder(context, builder, flower);
            builder.addAction(R.drawable.ic_notification_watering, Res.getString(R.string.action_done),
                    PendingIntent.getService(context, FlowersAlarmsService.REQUEST_CODE_SET_WATERING_DONE,
                            FlowersAlarmsService.createWateringDoneIntent(context, flower), 0));
        }

        provider.unbind();

        manager.notify(notificationId, builder.build());
    }

    private static void setCommonNotificationData(Context context, NotificationCompat.Builder builder,
                                                  long itemId, boolean isGroupNotification) {
        builder.setColorized(true);
        builder.setContentTitle(Res.getString(R.string.notification_title_watering));
        builder.setSmallIcon(R.drawable.ic_notification_watering);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        Intent contentIntent = new Intent(context, MainActivity.class);
        contentIntent.putExtra(isGroupNotification ? EXTRA_GROUP_ID : EXTRA_FLOWER_ID, itemId);

        PendingIntent openIntent = PendingIntent.getActivity(context,
                isGroupNotification ? MainActivity.REQUEST_CODE_OPEN_GROUP : MainActivity.REQUEST_CODE_OPEN_FLOWER,
                contentIntent, 0);
        builder.setContentIntent(openIntent);

        builder.addAction(R.drawable.ic_notification_watering, Res.getString(R.string.action_postpone),
                AlarmBroadcastReceiver.getPostponeIntent(context, isGroupNotification, itemId));
        builder.addAction(R.drawable.ic_notification_watering, Res.getString(R.string.action_open), openIntent);
    }

    private static void fillNotificationBuilder(Context context, NotificationCompat.Builder builder, Flower flower) {
        builder.setContentText(Res.getString(R.string.notification_message_watering_format, flower.getName()));
        builder.setColor(Res.getColor(R.color.primary200));
        builder.setBadgeIconType(BADGE_ICON_SMALL);
    }

    private static void fillNotificationBuilder(NotificationCompat.Builder builder, Group group) {
        builder.setContentText(Res.getString(R.string.notification_message_watering_format, group.getName()));
        builder.setColor(Res.getColor(R.color.accent200));
        builder.setBadgeIconType(BADGE_ICON_LARGE);
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

    @IntDef({NotificationType.WATERING, NotificationType.FERTILIZER,
            NotificationType.TRANSFER, NotificationType.SPRINKLING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NotificationType {
        int WATERING = 0;
        int FERTILIZER = 1;
        int TRANSFER = 2;
        int SPRINKLING = 3;
    }
}
