package com.indoor.flowers.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.indoor.flowers.database.Columns;

@Entity(tableName = Group.TABLE_NAME)
public class Group {

    public static final String TABLE_NAME = "GroupTable";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Columns.ID)
    private long id;
    @ColumnInfo(name = Columns.NAME)
    private String name;
    @ColumnInfo(name = Columns.USE_COMMON_SETTINGS)
    private boolean useCommonSettings;
    @Embedded(prefix = Columns.SETTINGS_PREFIX)
    private SettingData settingData;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean useCommonSettings() {
        return useCommonSettings;
    }

    public void setUseCommonSettings(boolean useCommonSettings) {
        this.useCommonSettings = useCommonSettings;
    }

    public SettingData getSettingData() {
        return settingData;
    }

    public void setSettingData(SettingData settingData) {
        this.settingData = settingData;
    }
}
