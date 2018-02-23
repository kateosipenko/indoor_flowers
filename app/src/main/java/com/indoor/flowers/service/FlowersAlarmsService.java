package com.indoor.flowers.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.NotificationsProvider;
import com.indoor.flowers.model.Notification;

import java.util.Calendar;

public class FlowersAlarmsService extends IntentService {

    public static final int REQUEST_CODE_SET_WATERING_DONE = 8324;

    public static final String ACTION_SET_WATERING_DONE = "action_set_watering_done";

    private static final String EXTRA_EVENT_ID = "extra_flower_id";

    public static Intent createDoneIntent(Context context, Notification event) {
        Intent intent = new Intent(context, FlowersAlarmsService.class);
        intent.setAction(ACTION_SET_WATERING_DONE);
        intent.putExtra(EXTRA_EVENT_ID, event.getId());
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
            case ACTION_SET_WATERING_DONE:
                onSetWateringDoneIntentGot(intent);
                break;
        }
    }

    private void onSetWateringDoneIntentGot(Intent intent) {
        if (!intent.hasExtra(EXTRA_EVENT_ID)) {
            return;
        }

        NotificationsProvider provider = new NotificationsProvider(this);
        long eventId = intent.getLongExtra(EXTRA_EVENT_ID, DatabaseProvider.DEFAULT_ID);
        Notification event = provider.getNotificationById(eventId);
        if (event.getFrequency() != null) {
            Calendar eventDate = Calendar.getInstance();
            eventDate.add(Calendar.DAY_OF_YEAR, event.getFrequency());
            event.setDate(eventDate);
            provider.createOrUpdateNotification(event);
        }

        provider.unbind();
    }
}
