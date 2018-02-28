package com.indoor.flowers.util;

import android.support.annotation.ColorInt;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.database.FlowersDatabase;
import com.indoor.flowers.model.EventAction;
import com.indoor.flowers.model.Notification;
import com.indoor.flowers.model.NotificationType;
import com.indoor.flowers.model.NotificationWithTarget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class EventsUtils {

    public static String getTitleForEvent(@NotificationType int eventType) {
        String result = "";
        switch (eventType) {
            case NotificationType.CREATED:
                result = Res.getString(R.string.event_created);
                break;
            case NotificationType.FERTILIZER:
                result = Res.getString(R.string.event_fertilizer);
                break;
            case NotificationType.TRANSPLANTING:
                result = Res.getString(R.string.event_transplanting);
                break;
            case NotificationType.WATERING:
                result = Res.getString(R.string.event_watering);
                break;
        }
        return result;
    }

    @ColorInt
    public static int getColorForEventType(@NotificationType int eventType) {
        @ColorInt int result = Res.getColor(R.color.material_white);
        switch (eventType) {
            case NotificationType.CREATED:
                result = Res.getColor(R.color.event_created);
                break;
            case NotificationType.FERTILIZER:
                result = Res.getColor(R.color.event_fertilizer);
                break;
            case NotificationType.TRANSPLANTING:
                result = Res.getColor(R.color.event_transplantation);
                break;
            case NotificationType.WATERING:
                result = Res.getColor(R.color.event_watering);
                break;
        }

        return result;
    }

    public static HashMap<Integer, List<NotificationWithTarget>> createDayNotificationsMap(
            List<Notification> notifications, FlowersDatabase database,
            Calendar minDate, Calendar maxDate) {
        HashMap<Integer, List<NotificationWithTarget>> eventsByDays = new HashMap<>();
        int daysCount = CalendarUtils.getDaysDiff(minDate, maxDate);
        int startDayOfYear = minDate.get(Calendar.DAY_OF_YEAR);
        for (int i = 0; i < daysCount; i++) {
            eventsByDays.put(startDayOfYear, new ArrayList<NotificationWithTarget>());
            ++startDayOfYear;
        }

        for (Notification notification : notifications) {
            List<EventAction> actionsPerRange = database.getEventActionDao()
                    .getEventActionsPerNotification(notification.getId(), minDate.getTimeInMillis(),
                            maxDate.getTimeInMillis());
            Calendar lastActionDate = database.getEventActionDao()
                    .getNotificationLastActionDate(notification.getId());
            if (actionsPerRange != null && actionsPerRange.size() > 0) {
                for (EventAction action : actionsPerRange) {
                    addNotificationForDay(notification, action.getDate(), eventsByDays);
                }
            }

            if (notification.getFrequency() != null) {
                if (lastActionDate == null) {
                    lastActionDate = notification.getDate();
                    addNotificationForDay(notification, lastActionDate, eventsByDays);
                }

                do {
                    lastActionDate = (Calendar) lastActionDate.clone();
                    lastActionDate.add(Calendar.DAY_OF_YEAR, notification.getFrequency());
                    if (notification.getEndDate() == null
                            || lastActionDate.before(notification.getEndDate())) {
                        addNotificationForDay(notification, lastActionDate, eventsByDays);
                    }
                } while (lastActionDate.before(maxDate)
                        && (notification.getEndDate() == null
                        || lastActionDate.before(notification.getEndDate())));
            } else if (actionsPerRange == null) {
                addNotificationForDay(notification, notification.getDate(), eventsByDays);
            }
        }

        return eventsByDays;
    }

    private static void addNotificationForDay(Notification notification, Calendar date,
                                              HashMap<Integer, List<NotificationWithTarget>> eventsByDays) {
        NotificationWithTarget target = new NotificationWithTarget();
        target.setNotification(notification);
        target.setEventDate(date);
        List<NotificationWithTarget> dayEvents = eventsByDays.get(date.get(Calendar.DAY_OF_YEAR));
        if (dayEvents != null) {
            dayEvents.add(target);
        }
    }
}
