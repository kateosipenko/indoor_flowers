package com.indoor.flowers.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.util.NotificationsUtils;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_SHOW_NOTIFICATION = "action_show_notification";
    public static final String ACTION_POSTPONE = "action_postpone";

    public static final String KEY_EVENT_ID = "key_event_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }

        switch (intent.getAction()) {
            case ACTION_SHOW_NOTIFICATION:
                NotificationsUtils.showNotificationForEvent(context, intent.getLongExtra(KEY_EVENT_ID,
                        DatabaseProvider.DEFAULT_ID));
                break;
        }
    }
}
