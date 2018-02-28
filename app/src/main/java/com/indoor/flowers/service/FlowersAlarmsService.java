package com.indoor.flowers.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.NotificationsProvider;
import com.indoor.flowers.model.Notification;
import com.indoor.flowers.util.NotificationsUtils;

import java.util.Calendar;

public class FlowersAlarmsService extends IntentService {

    public static final String ACTION_SET_NOTIFICATION_DONE = "action_set_notification_done";

    private static final String EXTRA_NOTIFICATION_ID = "extra_notification_id";

    public static Intent createDoneIntent(Context context, Notification event) {
        Intent intent = new Intent(context, FlowersAlarmsService.class);
        intent.setAction(ACTION_SET_NOTIFICATION_DONE);
        intent.putExtra(EXTRA_NOTIFICATION_ID, event.getId());
        return intent;
    }

    public FlowersAlarmsService() {
        super(FlowersAlarmsService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }

        switch (intent.getAction()) {
            case ACTION_SET_NOTIFICATION_DONE:
                setNotificationDone(intent);
                break;
        }
    }

    private void setNotificationDone(Intent intent) {
        if (!intent.hasExtra(EXTRA_NOTIFICATION_ID)) {
            return;
        }

        NotificationsProvider provider = new NotificationsProvider(this);
        long eventId = intent.getLongExtra(EXTRA_NOTIFICATION_ID, DatabaseProvider.DEFAULT_ID);
        Notification event = provider.getNotificationById(eventId);
        NotificationsUtils.cancelEventNotifications(this, event);
        provider.markEventDone(event, Calendar.getInstance());
        provider.unbind();
    }
}
