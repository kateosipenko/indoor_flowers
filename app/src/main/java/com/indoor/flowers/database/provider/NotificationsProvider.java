package com.indoor.flowers.database.provider;

import android.content.Context;
import android.text.TextUtils;

import com.indoor.flowers.database.Columns;
import com.indoor.flowers.database.dao.NotificationDao;
import com.indoor.flowers.model.CalendarFilter;
import com.indoor.flowers.model.EventAction;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.model.Notification;
import com.indoor.flowers.model.NotificationWithTarget;
import com.indoor.flowers.util.EventsUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class NotificationsProvider extends DatabaseProvider {

    public NotificationsProvider(Context context) {
        super(context);
    }

    public void markEventDone(Notification notification, Calendar eventDoneDate) {
        EventAction action = new EventAction();
        action.setDate(eventDoneDate);
        action.setNotificationId(notification.getId());
        action.setId(invalidateIdForInsert(action.getId()));
        database.getEventActionDao().insert(action);
    }

    public List<NotificationWithTarget> getNearbyNotifications(Calendar start, int daysCount,
                                                               boolean includeOldEvents) {
        Calendar startDate = (Calendar) start.clone();
        Calendar endDate = (Calendar) startDate.clone();
        endDate.add(Calendar.DAY_OF_YEAR, daysCount);
        List<Notification> events = database.getNotificationDao().getNearbyNotifications(startDate.getTimeInMillis(),
                endDate.getTimeInMillis());
        if (events != null) {
            events = EventsUtils.createOrderedEventsWithPeriodically(events, startDate, endDate, includeOldEvents);
        }

        List<NotificationWithTarget> result = new ArrayList<>();
        if (events != null) {
            result = getNotificationsTarget(events);
        }

        return result;
    }

    public List<Notification> getEventsForTarget(long targetId, String targetTable) {
        return database.getNotificationDao().getNotificationForTarget(targetId, targetTable);
    }

    public HashMap<Integer, List<Notification>> getEventsForPeriod(Calendar startDate, Calendar endDate,
                                                                   CalendarFilter filter) {
        String selection = "";
        switch (filter.getElementsFilterType()) {
            case CalendarFilter.FilterElements.FLOWERS:
                selection += Columns.TARGET_TABLE + "='" + Flower.TABLE_NAME + "'";
                break;
            case CalendarFilter.FilterElements.GROUPS:
                selection += Columns.TARGET_TABLE + "='" + Group.TABLE_NAME + "'";
                break;
            case CalendarFilter.FilterElements.SELECTED:
                if (filter.getSelectedElements() != null && filter.getSelectedElements().size() > 0) {
                    selection += Columns.TARGET_ID + " in ("
                            + TextUtils.join(",", filter.getSelectedElements())
                            + ") ";
                }

                break;
        }

        if (filter.getSelectedEventTypes() != null && filter.getSelectedEventTypes().size() > 0) {
            if (!TextUtils.isEmpty(selection)) {
                selection += " and ";
            }

            selection += Columns.TYPE + " in ("
                    + TextUtils.join(",", filter.getSelectedEventTypes())
                    + ") ";
        }

        String query = String.format(NotificationDao.QUERY_EVENTS_FILTER, startDate.getTimeInMillis(),
                endDate.getTimeInMillis());
        if (!TextUtils.isEmpty(selection)) {
            query += " and " + selection;
        }

        List<Notification> items = database.getNotificationDao().getNotificationForSelection(query);
        return EventsUtils.groupNotifications(items, startDate, endDate);
    }

    public List<NotificationWithTarget> getNotificationsTarget(List<Notification> itemsPerDay) {
        List<NotificationWithTarget> result = new ArrayList<>();
        if (itemsPerDay != null) {
            for (Notification notification : itemsPerDay) {
                NotificationWithTarget item = new NotificationWithTarget();
                item.setNotification(notification);
                if (Flower.TABLE_NAME.equalsIgnoreCase(notification.getTargetTable())) {
                    Flower flower = database.getFlowersDao().getFlowerById(notification.getTargetId());
                    item.setTarget(flower);
                } else if (Group.TABLE_NAME.equalsIgnoreCase(notification.getTargetTable())) {
                    Group group = database.getGroupDao().getGroupById(notification.getTargetId());
                    item.setTarget(group);
                }

                result.add(item);
            }
        }

        return result;
    }

    public Notification getNotificationById(long id) {
        return database.getNotificationDao().getNotificationById(id);
    }

    public void createOrUpdateNotification(Notification notification) {
        if (notification.getId() == DEFAULT_ID) {
            notification.setId(invalidateIdForInsert(notification.getId()));
            long id = database.getNotificationDao().insert(notification);
            notification.setId(id);
        } else {
            database.getNotificationDao().update(notification);
        }
    }

    public void deleteNotification(Notification notification) {
        database.getNotificationDao().delete(notification);
    }

    // endregion NOTIFICATIONS
}
