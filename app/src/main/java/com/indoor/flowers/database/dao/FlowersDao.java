package com.indoor.flowers.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.FlowerWithSetting;

import java.util.List;

@Dao
public interface FlowersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Flower flower);

    @Update
    void update(Flower flower);

    @Delete
    void delete(Flower flower);

    @Query("select * from FlowerTable inner join SettingDataTable on FlowerTable.setting_data_id=SettingDataTable.setting_id")
    List<FlowerWithSetting> getAllFlowers();

    @Query("select * from FlowerTable where _id=:flowerId")
    Flower getFlowerById(long flowerId);

    @Query("select * from FlowerTable inner join SettingDataTable on FlowerTable.setting_data_id=SettingDataTable.setting_id"
            + " and FlowerTable._id in (select flower_id from GroupFlowerTable where group_id=:groupId)")
    List<FlowerWithSetting> getFlowersForGroup(long groupId);

    @Query("select * from FlowerTable inner join SettingDataTable on FlowerTable.setting_data_id=SettingDataTable.setting_id"
            + " and FlowerTable._id not in (select flower_id from GroupFlowerTable)")
    List<FlowerWithSetting> getFlowersWithoutGroup();

    @Query("update SettingDataTable set last_watering_date=:timeInMillis " +
            "where setting_id=(select setting_data_id from FlowerTable where _id=:flowerId)")
    void setFlowerLastTimeWatering(long flowerId, long timeInMillis);
}
