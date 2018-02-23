package com.indoor.flowers.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import com.indoor.flowers.model.EventAction;

@Dao
public interface EventActionDao {

    @Insert
    long insert(EventAction item);

    @Update
    void update(EventAction item);

    @Delete
    void delete(EventAction item);
}
