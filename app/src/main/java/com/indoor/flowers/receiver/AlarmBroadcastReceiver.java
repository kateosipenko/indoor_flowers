package com.indoor.flowers.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.util.NotificationsUtils;

import java.util.Objects;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_SHOW_NOTIFICATION = "action_show_notification";
    public static final String ACTION_POSTPONE = "action_postpone";

    private static final String EXTRA_FLOWER_ID = "action_flower_id";
    private static final String EXTRA_GROUP_ID = "action_group_id";

    public static PendingIntent getPostponeIntent(Context context, boolean isGroupNotification, long itemId) {
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.setAction(ACTION_POSTPONE);
        intent.putExtra(isGroupNotification ? EXTRA_GROUP_ID : EXTRA_FLOWER_ID, itemId);
        return PendingIntent.getBroadcast(context, Objects.hash(isGroupNotification, itemId), intent, 0);
    }

    public static PendingIntent getNotificationIntent(Context context, boolean isGroupNotification,
                                                      long itemId) {
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.setAction(AlarmBroadcastReceiver.ACTION_SHOW_NOTIFICATION);
        intent.putExtra(isGroupNotification ? EXTRA_GROUP_ID : EXTRA_FLOWER_ID, itemId);
        return PendingIntent.getBroadcast(context, Objects.hash(isGroupNotification, itemId), intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }

        switch (intent.getAction()) {
            case ACTION_SHOW_NOTIFICATION:
                onShowNotificationIntentGot(context, intent);
                break;
        }
    }

    private void onShowNotificationIntentGot(Context context, Intent intent) {
        if (intent.hasExtra(EXTRA_FLOWER_ID)) {
            NotificationsUtils.showNotificationForFlower(context,
                    intent.getLongExtra(EXTRA_FLOWER_ID, DatabaseProvider.DEFAULT_ID));
        } else if (intent.hasExtra(EXTRA_GROUP_ID)) {
            NotificationsUtils.showNotificationForGroup(context,
                    intent.getLongExtra(EXTRA_GROUP_ID, DatabaseProvider.DEFAULT_ID));
        }
    }
}
