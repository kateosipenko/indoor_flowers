package com.indoor.flowers.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import com.indoor.flowers.database.DbTypesConverter;
import com.indoor.flowers.model.EventAction;

import java.util.Calendar;
import java.util.List;

@Dao
@TypeConverters({DbTypesConverter.class})
public interface EventActionDao {

    @Insert
    long insert(EventAction item);

    @Update
    void update(EventAction item);

    @Delete
    void delete(EventAction item);

    @Query("select * from EventActionTable where notification_id=:notificationId "
            + " and date between :startDate and :endDate "
            + " order by date")
    List<EventAction> getEventActionsPerNotification(long notificationId, long startDate, long endDate);

    @Query("select max(date) from EventActionTable where notification_id=:notificationId")
    Calendar getNotificationLastActionDate(long notificationId);
}
