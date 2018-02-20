package com.indoor.flowers.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import com.indoor.flowers.model.Event;

import java.util.List;

@Dao
public interface EventDao {

    String QUERY_EVENTS_FILTER = "select * from EventTable where "
            + " (frequency not null and frequency > 0 "
            + " and (end_date==0 or end_date between %1$s and %2$s) "
            + " and (creation_date<%1$s and creation_date<%2$s or creation_date between %1$s and %2$s) "
            + " or frequency is null and event_date between %1$s and %2$s) ";

    @Insert
    long insert(Event event);

    @Update
    void update(Event event);

    @Query("delete from EventTable where target_id=:targetId and target_table=:targetTable ")
    void deleteForTarget(long targetId, String targetTable);

    @Query("select * from EventTable where target_id=:targetId and target_table=:targetTable "
            + " and event_type=:eventType limit 1")
    Event getForTarget(long targetId, String targetTable, int eventType);

    @RawQuery
    List<Event> getEventForSelection(String query);
}
