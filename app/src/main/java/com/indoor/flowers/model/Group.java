package com.indoor.flowers.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.indoor.flowers.database.Columns;

import java.util.Objects;

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
    @ColumnInfo(name = Columns.SETTING_DATA_ID)
    private long settingDataId;

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

    public long getSettingDataId() {
        return settingDataId;
    }

    public void setSettingDataId(long settingDataId) {
        this.settingDataId = settingDataId;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Group)) {
            return false;
        }

        return Objects.equals(this.id, ((Group) obj).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name);
    }
}
