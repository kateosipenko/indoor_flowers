package com.indoor.flowers.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.indoor.flowers.database.Columns;
import com.indoor.flowers.database.DbTypesConverter;

import java.util.Calendar;

@Entity(tableName = Notification.TABLE_NAME)
@TypeConverters({DbTypesConverter.class})
public class Notification {

    public static final String TABLE_NAME = "NotificationTable";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Columns.ID)
    private long id;
    @ColumnInfo(name = Columns.TYPE)
    @NotificationType
    private int type;
    @ColumnInfo(name = Columns.TARGET_ID)
    private long targetId;
    @ColumnInfo(name = Columns.TITLE)
    private String title;
    @ColumnInfo(name = Columns.COMMENT)
    private String comment;
    @ColumnInfo(name = Columns.TARGET_TABLE)
    private String targetTable;
    @ColumnInfo(name = Columns.DATE)
    private Calendar date;
    @ColumnInfo(name = Columns.END_DATE)
    private Calendar endDate;
    @ColumnInfo(name = Columns.FREQUENCY)
    private Integer frequency;
    @ColumnInfo(name = Columns.ACTIVE)
    private boolean isActive;

    public Notification clone() {
        Notification result = new Notification();
        result.setId(id);
        result.setType(type);
        result.setTargetId(targetId);
        result.setTargetTable(targetTable);
        result.setTitle(title);
        result.setComment(comment);
        result.setDate(date);
        result.setEndDate(endDate);
        result.setFrequency(frequency);
        result.setActive(isActive);
        return result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NotificationType
    public int getType() {
        return type;
    }

    public void setType(@NotificationType int type) {
        this.type = type;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
