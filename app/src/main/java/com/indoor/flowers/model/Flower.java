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
    private SettingData settingData;

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

    public SettingData getSettingData() {
        return settingData;
    }

    public void setSettingData(SettingData settingData) {
        this.settingData = settingData;
    }

    public Calendar getNextWateringTime() {
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(settingData.getLastWateringDate().getTimeInMillis());
        // TODO: remove fake
//        result.add(Calendar.DAY_OF_MONTH, settingData.getWateringFrequency());
//        result.set(Calendar.HOUR_OF_DAY, settingData.getPreferredTime().get(Calendar.HOUR_OF_DAY));
//        result.set(Calendar.MINUTE, settingData.getPreferredTime().get(Calendar.MINUTE));

        result.add(Calendar.SECOND, settingData.getWateringFrequency());

        return result;
    }
}
