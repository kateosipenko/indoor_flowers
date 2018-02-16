package com.indoor.flowers.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.indoor.flowers.model.Event;

import java.util.List;

@Dao
public interface EventDao {

    @Insert
    long insert(Event event);

    @Update
    void update(Event event);

    @Query("delete from EventTable where target_id=:targetId and target_table=:targetTable ")
    void deleteForTarget(long targetId, String targetTable);

    @Query("select * from EventTable where target_id=:targetId and target_table=:targetTable "
            + " and event_type=:eventType limit 1")
    Event getForTarget(long targetId, String targetTable, int eventType);

    @Query("select * from EventTable where frequency not null and frequency > 0 " +
            " and (end_date==0 or end_date between :startDate and :endDate) and creation_date < end_date" +
            " or frequency is null and event_date between :startDate and :endDate ")
    List<Event> getEventsForPeriod(long startDate, long endDate);
}
