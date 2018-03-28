package com.indoor.flowers.database.provider;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.indoor.flowers.database.Columns;
import com.indoor.flowers.database.dao.NotificationDao;
import com.indoor.flowers.model.CalendarFilter;
import com.indoor.flowers.model.CalendarFilter.FilterElements;
import com.indoor.flowers.model.EventAction;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.model.Notification;
import com.indoor.flowers.model.NotificationWithTarget;
import com.indoor.flowers.util.CalendarUtils;
import com.indoor.flowers.util.EventsUtils;

import java.util.Calendar;
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

    public List<NotificationWithTarget> getNearbyEvents(Calendar start, int daysCount,
                                                        boolean includeOldEvents) {
        Calendar startDate = (Calendar) start.clone();
        Calendar endDate = (Calendar) startDate.clone();
        endDate.add(Calendar.DAY_OF_YEAR, daysCount);
        return database.getNotificationDao().getNearbyEvents(startDate, endDate, includeOldEvents);
    }

    public List<Notification> getEventsForTarget(long targetId, String targetTable) {
        return database.getNotificationDao().getNotificationForTarget(targetId, targetTable);
    }

    public SparseArray<List<NotificationWithTarget>> getEventsForPeriod(Calendar startDate, Calendar endDate,
                                                                        CalendarFilter filter) {
        String query = buildNotificationsCalendarQuery(filter, startDate, endDate);
        List<Notification> notifications = database.getNotificationDao().getNotificationForSelection(query);
        return EventsUtils.createDayNotificationsMap(notifications, database, startDate, endDate);
    }

    private String buildNotificationsCalendarQuery(CalendarFilter filter, Calendar startDate,
                                                   Calendar endDate) {
        String selection = "";
        switch (filter.getElementsFilterType()) {
            case FilterElements.FLOWERS:
                selection += Columns.TARGET_TABLE + "='" + Flower.TABLE_NAME + "'";
                break;
            case FilterElements.GROUPS:
                selection += Columns.TARGET_TABLE + "='" + Group.TABLE_NAME + "'";
                break;
            case FilterElements.SELECTED:
                if (filter.getSelectedFlowers() != null && filter.getSelectedFlowers().size() > 0) {
                    selection += "(" + Columns.TARGET_TABLE + "='" + Flower.TABLE_NAME + "'"
                            + " and " + Columns.TARGET_ID + " in ("
                            + TextUtils.join(",", filter.getSelectedFlowers())
                            + ")) ";
                }
                if (filter.getSelectedGroups() != null && filter.getSelectedGroups().size() > 0) {
                    if (!TextUtils.isEmpty(selection)) {
                        selection += " or ";
                    }

                    selection += "(" + Columns.TARGET_TABLE + "='" + Group.TABLE_NAME + "'"
                            + " and " + Columns.TARGET_ID + " in ("
                            + TextUtils.join(",", filter.getSelectedGroups())
                            + ")) ";
                }

                if (!TextUtils.isEmpty(selection)) {
                    selection = "(" + selection + ")";
                }

                break;
            case FilterElements.NONE:
                selection = "";
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

        return query;
    }

    public Notification getNotificationById(long id) {
        return database.getNotificationDao().getNotificationById(id);
    }

    public NotificationWithTarget getNotificationWithTarget(long id) {
        return database.getNotificationDao().getNotificationWithTarget(id);
    }

    public void createOrUpdateNotification(Notification notification) {
        if (notification.getId() == DEFAULT_ID) {
            notification.setId(invalidateIdForInsert(notification.getId()));
            long id = database.getNotificationDao().insert(notification);
            notification.setId(id);
            if (notification.getFrequency() != null && CalendarUtils.isOldDate(notification.getDate())) {
                Calendar eventDate = notification.getDate();
                Calendar today = Calendar.getInstance();
                do {
                    markEventDone(notification, eventDate);
                    eventDate = (Calendar) eventDate.clone();
                    eventDate.add(Calendar.DAY_OF_YEAR, notification.getFrequency());
                } while (eventDate.before(today));
            } else {
                markEventDone(notification, notification.getDate());
            }
        } else {
            database.getNotificationDao().update(notification);
        }
    }

    public void deleteNotification(Notification notification) {
        database.getNotificationDao().delete(notification);
    }

    // endregion NOTIFICATIONS
}
