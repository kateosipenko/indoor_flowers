package com.indoor.flowers.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.TypeConverters;

import com.indoor.flowers.database.Columns;
import com.indoor.flowers.database.DbTypesConverter;

import java.util.Calendar;

@TypeConverters({DbTypesConverter.class})
public class NotificationWithTarget {

    @Embedded
    private Notification notification;
    @ColumnInfo(name = Columns.IMAGE_PATH)
    private String imagePath;
    @ColumnInfo(name = Columns.NAME)
    private String name;
    @ColumnInfo(name = Columns.EVENT_DATE)
    private Calendar eventDate;

    public NotificationWithTarget clone() {
        NotificationWithTarget result = new NotificationWithTarget();
        result.setNotification(notification != null ? notification.clone() : null);
        result.setImagePath(imagePath);
        result.setName(name);
        result.setEventDate(eventDate != null ? (Calendar) eventDate.clone() : null);
        return result;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getEventDate() {
        return eventDate;
    }

    public void setEventDate(Calendar eventDate) {
        this.eventDate = eventDate;
    }
}
