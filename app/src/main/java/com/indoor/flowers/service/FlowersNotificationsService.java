package com.indoor.flowers.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.indoor.flowers.R;
import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Flower;

import java.util.Calendar;

public class FlowersNotificationsService extends IntentService {

    public static final int REQUEST_CODE_NOTIFICATION = 3453;

    public static final String ACTION_FLOWER_WATERING = "action_flower_watering";

    public static final String EXTRA_FLOWER_ID = "extra_flower_id";

    public FlowersNotificationsService() {
        super(FlowersNotificationsService.class.getName());
    }

    public static void setupNotificationForFlower(Context context, Flower flower) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, FlowersNotificationsService.class);
        intent.setAction(ACTION_FLOWER_WATERING);
        intent.putExtra(EXTRA_FLOWER_ID, flower.getId());

        PendingIntent pendingIntent = PendingIntent.getService(context,
                REQUEST_CODE_NOTIFICATION, intent, 0);

        if (manager != null) {
            manager.set(AlarmManager.RTC, flower.getNextWateringTime().getTimeInMillis(), pendingIntent);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }

        switch (intent.getAction()) {
            case ACTION_FLOWER_WATERING:
                showFlowerWateringNotification(intent.getLongExtra(EXTRA_FLOWER_ID, DatabaseProvider.DEFAULT_ID));
                break;
        }
    }

    private void showFlowerWateringNotification(long flowerId) {
        FlowersProvider provider = new FlowersProvider(this);
        Flower flower = provider.getFlowerById(flowerId);

        if (flower == null) {
            return;
        }

        flower.setLastWateringDate(Calendar.getInstance());
        provider.updateFlower(flower);
        setupNotificationForFlower(this, flower);

        Notification notification = new NotificationCompat.Builder(this,
                FlowersNotificationsService.class.getSimpleName())
                .setContentTitle("Пора полить " + flower.getName())
                .setSmallIcon(R.drawable.ic_flower)
                .setLargeIcon(BitmapFactory.decodeFile(flower.getImagePath()))
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify((int) flowerId, notification);
        }

        provider.unbind();
    }
}
