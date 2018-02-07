package com.indoor.flowers.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.indoor.flowers.model.Flower;

import java.util.List;

@Dao
public interface FlowersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Flower flower);

    @Query("select * from " + Flower.TABLE_NAME)
    List<Flower> getAllFlowers();
}
