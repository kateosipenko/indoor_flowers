package com.indoor.flowers.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;

import java.util.Calendar;

public class FlowersAlarmsService extends IntentService {

    public static final int REQUEST_CODE_SET_WATERING_DONE = 8324;

    public static final String ACTION_SET_WATERING_DONE = "action_set_watering_done";

    private static final String EXTRA_FLOWER_ID = "extra_flower_id";
    private static final String EXTRA_GROUP_ID = "extra_flower_id";

    public static Intent createWateringDoneIntent(Context context, Flower flower) {
        Intent intent = new Intent(context, FlowersAlarmsService.class);
        intent.setAction(ACTION_SET_WATERING_DONE);
        intent.putExtra(EXTRA_FLOWER_ID, flower.getId());
        return intent;
    }

    public static Intent createWateringDoneIntent(Context context, Group group) {
        Intent intent = new Intent(context, FlowersAlarmsService.class);
        intent.setAction(ACTION_SET_WATERING_DONE);
        intent.putExtra(EXTRA_GROUP_ID, group.getId());
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
        FlowersProvider provider = new FlowersProvider(this);
        if (intent.hasExtra(EXTRA_FLOWER_ID)) {
            long flowerId = intent.getLongExtra(EXTRA_FLOWER_ID, DatabaseProvider.DEFAULT_ID);
            provider.setFlowerLastTimeWatering(flowerId, Calendar.getInstance().getTimeInMillis());
        } else if (intent.hasExtra(EXTRA_GROUP_ID)) {
            long groupId = intent.getLongExtra(EXTRA_GROUP_ID, DatabaseProvider.DEFAULT_ID);
            provider.setGroupLastTimeWatering(groupId, Calendar.getInstance().getTimeInMillis());
        }

        provider.unbind();
    }
}
