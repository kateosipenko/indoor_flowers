package com.indoor.flowers.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.indoor.flowers.database.Columns;
import com.indoor.flowers.database.DbTypesConverter;

import java.util.Calendar;

@Entity(tableName = Flower.TABLE_NAME)
@TypeConverters({DbTypesConverter.class})
public class Flower {

    public static final String TABLE_NAME = "FlowerTable";

    @ColumnInfo(name = Columns.ID)
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = Columns.NAME)
    private String name;
    @ColumnInfo(name = Columns.IMAGE_PATH)
    private String imagePath;
    @ColumnInfo(name = Columns.GROUP_ID)
    private long groupId;
    @Embedded(prefix = Columns.SETTINGS_PREFIX)
    private SettingData settings;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public SettingData getSettings() {
        return settings;
    }

    public void setSettings(SettingData settings) {
        this.settings = settings;
    }

    public Calendar getNextWateringTime() {
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(settings.getLastWateringDate().getTimeInMillis());
        result.add(Calendar.DAY_OF_MONTH, settings.getWateringFrequency());
        return result;
    }
}
