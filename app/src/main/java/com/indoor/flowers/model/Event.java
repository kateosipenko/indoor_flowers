package com.indoor.flowers.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.indoor.flowers.database.Columns;
import com.indoor.flowers.database.DbTypesConverter;

import java.util.Calendar;

@Entity(tableName = Event.TABLE_NAME)
@TypeConverters({DbTypesConverter.class})
public class Event {

    public static final String TABLE_NAME = "EventTable";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Columns.ID)
    private long id;
    @ColumnInfo(name = Columns.EVENT_TYPE)
    @EventType
    private int eventType;
    @ColumnInfo(name = Columns.TARGET_ID)
    private long targetId;
    @ColumnInfo(name = Columns.TITLE)
    private String title;
    @ColumnInfo(name = Columns.COMMENT)
    private String comment;
    @ColumnInfo(name = Columns.TARGET_TABLE)
    private String targetTable;
    @ColumnInfo(name = Columns.CREATION_DATE)
    private Calendar creationDate;
    @ColumnInfo(name = Columns.EVENT_DATE)
    private Calendar eventDate;
    @ColumnInfo(name = Columns.END_DATE)
    private Calendar endDate;
    @ColumnInfo(name = Columns.FREQUENCY)
    private Integer frequency;

    public Event clone() {
        Event result = new Event();
        result.setId(id);
        result.setEventType(eventType);
        result.setTargetId(targetId);
        result.setTargetTable(targetTable);
        result.setTitle(title);
        result.setComment(comment);
        result.setCreationDate(creationDate);
        result.setEventDate(eventDate);
        result.setEndDate(endDate);
        result.setFrequency(frequency);
        return result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @EventType
    public int getEventType() {
        return eventType;
    }

    public void setEventType(@EventType int eventType) {
        this.eventType = eventType;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public Calendar getEventDate() {
        return eventDate;
    }

    public void setEventDate(Calendar eventDate) {
        this.eventDate = eventDate;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
