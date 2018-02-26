package com.indoor.flowers.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import com.indoor.flowers.model.Notification;
import com.indoor.flowers.model.NotificationType;

import java.util.List;

@Dao
public interface NotificationDao {

    String QUERY_NOTIFICATION_FILTER = "select * from NotificationTable where type!=0 and " +
            " (frequency not null and frequency > 0 and (end_date is null or end_date >= %2$s) " +
            " or _id in " +
            " (select notification_id from EventActionTable where date between %1$s and  %2$s))";

    String QUERY_EVENTS_FILTER = "select * from NotificationTable where " +
            " (frequency not null and frequency > 0 and (end_date is null or end_date >= %2$s) " +
            " or _id in " +
            " (select notification_id from EventActionTable where date between %1$s and  %2$s))";

    @Insert
    long insert(Notification event);

    @Update
    void update(Notification event);

    @Delete
    void delete(Notification event);

    @Query("delete from NotificationTable where target_id=:targetId and target_table=:targetTable ")
    void deleteForTarget(long targetId, String targetTable);

    @Query("select * from NotificationTable where target_id=:targetId and target_table=:targetTable "
            + " and type=:eventType limit 1")
    Notification getForTarget(long targetId, String targetTable, int eventType);

    @RawQuery
    List<Notification> getNotificationForSelection(String query);

    @Query("select * from NotificationTable where _id=:eventId")
    Notification getNotificationById(long eventId);

    @Query("select * from NotificationTable where target_id=:targetId and target_table=:targetTable "
            + " and type!=" + NotificationType.CREATED)
    List<Notification> getNotificationForTarget(long targetId, String targetTable);

    @Query("select * from NotificationTable "
            + " where (frequency not null and frequency > 0 or end_date between :startDate and :endDate) "
            + " and type!=" + NotificationType.CREATED)
    List<Notification> getNearbyNotifications(long startDate, long endDate);
}
