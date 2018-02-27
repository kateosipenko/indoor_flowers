package com.indoor.flowers.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.indoor.flowers.database.Columns;
import com.indoor.flowers.database.DbTypesConverter;

import java.util.Calendar;

@Entity(tableName = PhotoItem.TABLE_NAME)
@TypeConverters({DbTypesConverter.class})
public class PhotoItem {

    public static final String TABLE_NAME = "PhotoItemTable";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Columns.ID)
    private long id;
    @ColumnInfo(name = Columns.TARGET_ID)
    private long targetId;
    @ColumnInfo(name = Columns.TARGET_TABLE)
    private String targetTable;
    @ColumnInfo(name = Columns.IMAGE_PATH)
    private String imagePath;
    @ColumnInfo(name = Columns.DATE)
    private Calendar date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }
}
