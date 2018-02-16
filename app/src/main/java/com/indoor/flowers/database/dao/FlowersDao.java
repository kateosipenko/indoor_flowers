package com.indoor.flowers.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.indoor.flowers.database.Columns;
import com.indoor.flowers.model.Flower;

import java.util.List;

@Dao
public interface FlowersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Flower flower);

    @Update
    void update(Flower flower);

    @Delete
    void delete(Flower flower);

    @Query("select * from " + Flower.TABLE_NAME)
    List<Flower> getAllFlowers();

    @Query("select * from " + Flower.TABLE_NAME
            + " where " + Columns.ID + "=:flowerId")
    Flower getFlowerById(long flowerId);

    @Query("select * from " + Flower.TABLE_NAME
            + " where " + Columns.GROUP_ID + "=:groupId")
    List<Flower> getFlowersForGroup(long groupId);

    @Query("select * from " + Flower.TABLE_NAME
            + " where " + Columns.GROUP_ID + " is null or "
            + Columns.GROUP_ID + "=-1")
    List<Flower> getFlowersWithoutGroup();

    @Query("update FlowerTable set stg_last_watering_date=:timeInMillis where _id=:flowerId")
    void setFlowerLastTimeWatering(long flowerId, long timeInMillis);
}
