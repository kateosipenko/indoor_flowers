package com.indoor.flowers.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import com.indoor.flowers.model.Event;
import com.indoor.flowers.model.EventType;

import java.util.List;

@Dao
public interface EventDao {

    String QUERY_EVENTS_FILTER = "select * from EventTable where "
            + " (frequency not null and frequency > 0 "
            + " and (end_date==0 or end_date between %1$s and %2$s) "
            + " and (creation_date<%1$s and creation_date<%2$s or creation_date between %1$s and %2$s) "
            + " or frequency is null and event_date between %1$s and %2$s) ";

    String QUERY_EVENTS_NEARBY = "select * from EventTable where "
            + " (event_date<%1$s or "
            + " (frequency not null and frequency > 0 "
            + " and (end_date==0 or end_date between %1$s and %2$s) "
            + " and (creation_date<%1$s and creation_date<%2$s or creation_date between %1$s and %2$s) "
            + " or frequency is null and event_date between %1$s and %2$s)) and event_type!=" + EventType.CREATED;

    @Insert
    long insert(Event event);

    @Update
    void update(Event event);

    @Delete
    void delete(Event event);

    @Query("delete from EventTable where target_id=:targetId and target_table=:targetTable ")
    void deleteForTarget(long targetId, String targetTable);

    @Query("select * from EventTable where target_id=:targetId and target_table=:targetTable "
            + " and event_type=:eventType limit 1")
    Event getForTarget(long targetId, String targetTable, int eventType);

    @RawQuery
    List<Event> getEventForSelection(String query);

    @Query("select * from EventTable where _id=:eventId")
    Event getEventById(long eventId);

    @Query("select * from EventTable where target_id=:targetId and target_table=:targetTable "
            + " and event_type!=" + EventType.CREATED)
    List<Event> getEventsForTarget(long targetId, String targetTable);
}
