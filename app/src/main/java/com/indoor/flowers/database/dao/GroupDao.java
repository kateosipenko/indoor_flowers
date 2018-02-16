package com.indoor.flowers.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.indoor.flowers.model.Group;

import java.util.List;

@Dao
public interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Group group);

    @Query("select * from GroupTable")
    List<Group> getAllGroups();

    @Query("select * from GroupTable where _id=:groupId")
    Group getGroupById(long groupId);

    @Update
    void update(Group group);

    @Query("update SettingDataTable set last_watering_date=:timeInMillis "
            + " where setting_id=(select setting_data_id from GroupTable where _id=:groupId)")
    void setGroupLastTimeWatering(long groupId, long timeInMillis);
}
