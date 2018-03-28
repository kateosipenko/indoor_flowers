package com.indoor.flowers.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.indoor.flowers.database.Columns;
import com.indoor.flowers.database.DbTypesConverter;

import java.util.Calendar;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = EventAction.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = Notification.class,
                        onDelete = CASCADE,
                        childColumns = Columns.NOTIFICATION_ID,
                        parentColumns = Columns.ID)})
@TypeConverters({DbTypesConverter.class})
public class EventAction {
    static final String TABLE_NAME = "EventActionTable";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Columns.ID)
    private long id;
    @ColumnInfo(name = Columns.NOTIFICATION_ID)
    private long notificationId;
    @ColumnInfo(name = Columns.DATE)
    private Calendar date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(long notificationId) {
        this.notificationId = notificationId;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }
}
