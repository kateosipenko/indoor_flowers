package com.indoor.flowers.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

import com.indoor.flowers.model.SettingData;

@Dao
public interface SettingDao {

    String COMMON_PROJECTION = " SettingDataTable.watering_period, SettingDataTable.last_watering_date, "
            + "SettingDataTable.last_nutrition_date, SettingDataTable.nutrition_freq, "
            + "SettingDataTable.last_transplanting_date, SettingDataTable.next_transplanting_date ";

    @Insert
    long insert(SettingData data);
}
