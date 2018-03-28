package com.indoor.flowers.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import com.indoor.flowers.database.Columns;
import com.indoor.flowers.model.Notification;
import com.indoor.flowers.model.NotificationType;
import com.indoor.flowers.model.NotificationWithTarget;
import com.indoor.flowers.util.CalendarUtils;
import com.indoor.flowers.util.EventDatesComparator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Dao
public abstract class NotificationDao {

    public static final String QUERY_EVENTS_FILTER = "select * from NotificationTable "
            + " where (end_date between %1$s and  %2$s "
            + " or frequency not null and frequency > 0 and (end_date is null or end_date>=%1$s))";

    @Insert
    public abstract long insert(Notification event);

    @Update
    public abstract void update(Notification event);

    @Delete
    public abstract void delete(Notification event);

    @Query("delete from NotificationTable where target_id=:targetId and target_table=:targetTable ")
    public abstract void deleteForTarget(long targetId, String targetTable);

    @Query("select * from NotificationTable where target_id=:targetId and target_table=:targetTable "
            + " and type=:eventType limit 1")
    public abstract Notification getForTarget(long targetId, String targetTable, int eventType);

    @RawQuery
    public abstract List<Notification> getNotificationForSelection(String query);

    @Query("select * from NotificationTable where _id=:eventId")
    public abstract Notification getNotificationById(long eventId);

    @Query("select NotificationTable.*, " +
            "    case when target_table='FlowerTable' " +
            "    then (select name from FlowerTable where _id=target_id) " +
            "    else (select name from GroupTable where _id=target_id) " +
            "    end as name, " +
            "                 " +
            "    case when target_table='FlowerTable' " +
            "    then (select image_path from FlowerTable where _id=target_id) " +
            "    else (select image_path from GroupTable where _id=target_id) " +
            "    end as image_path, " +
            "                 " +
            "    (select max(date) from EventActionTable " +
            "     where EventActionTable.notification_id=NotificationTable._id) " +
            "     as " + Columns.EVENT_DATE +
            " from NotificationTable " +
            " where _id=:notificationId")
    public abstract NotificationWithTarget getNotificationWithTarget(long notificationId);

    @Query("select * from NotificationTable where target_id=:targetId and target_table=:targetTable "
            + " and type!=" + NotificationType.CREATED)
    public abstract List<Notification> getNotificationForTarget(long targetId, String targetTable);

    @Query("select NotificationTable.*, " +
            "    case when target_table='FlowerTable' " +
            "    then (select name from FlowerTable where _id=target_id) " +
            "    else (select name from GroupTable where _id=target_id) " +
            "    end as name, " +
            "                 " +
            "    case when target_table='FlowerTable' " +
            "    then (select image_path from FlowerTable where _id=target_id) " +
            "    else (select image_path from GroupTable where _id=target_id) " +
            "    end as image_path, " +
            "                 " +
            "    (select max(date) from EventActionTable " +
            "     where EventActionTable.notification_id=NotificationTable._id) " +
            "     as " + Columns.EVENT_DATE +
            "                 " +
            "from NotificationTable " +
            "where date>=:startDate or end_date between :startDate and :endDate " +
            "or frequency>0 and (end_date is null or end_date>=:startDate) and date<=:startDate")
    public abstract List<NotificationWithTarget> getNotificationsForRange(long startDate, long endDate);

    public List<NotificationWithTarget> getNearbyEvents(Calendar startDate, Calendar endDate,
                                                        boolean includeOldNotifications) {
        List<NotificationWithTarget> notificationsInRange = getNotificationsForRange(
                startDate.getTimeInMillis(), endDate.getTimeInMillis());
        List<NotificationWithTarget> result = new ArrayList<>();
        for (NotificationWithTarget notificationWithTarget : notificationsInRange) {
            Notification notification = notificationWithTarget.getNotification();
            if (notification.getType() == NotificationType.CREATED) {
                continue;
            }

            if (notification.getFrequency() != null) {
                Calendar eventDate = null;
                if (notificationWithTarget.getEventDate() != null) {
                    eventDate = (Calendar) notificationWithTarget.getEventDate().clone();
                    eventDate.add(Calendar.DAY_OF_YEAR, notification.getFrequency());
                } else {
                    eventDate = notification.getDate();
                }

                if (eventDate.before(startDate) && !includeOldNotifications) {
                    long daysDiff = CalendarUtils.getDaysDiff(eventDate, startDate);
                    eventDate.set(Calendar.DAY_OF_YEAR, startDate.get(Calendar.DAY_OF_YEAR));
                    int daysToEvent = (int) (daysDiff % notification.getFrequency());
                    int addDays = daysToEvent > 0 ? notification.getFrequency() - daysToEvent : 0;
                    eventDate.add(Calendar.DAY_OF_YEAR, addDays);
                }

                do {
                    NotificationWithTarget periodically = notificationWithTarget.getCopy();
                    periodically.setEventDate(eventDate);
                    result.add(periodically);
                    eventDate = (Calendar) eventDate.clone();
                    eventDate.add(Calendar.DAY_OF_YEAR, notification.getFrequency());
                } while (eventDate.before(endDate)
                        || CalendarUtils.getDaysDiff(eventDate, endDate) == 0);
            } else {
                if (notificationWithTarget.getEventDate() == null) {
                    notificationWithTarget.setEventDate(notification.getDate());
                }

                result.add(notificationWithTarget);
            }
        }

        Collections.sort(result, new EventDatesComparator());
        return result;
    }
}
