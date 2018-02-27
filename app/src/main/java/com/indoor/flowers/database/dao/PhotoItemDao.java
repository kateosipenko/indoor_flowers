package com.indoor.flowers.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.indoor.flowers.model.PhotoItem;

import java.util.List;

@Dao
public interface PhotoItemDao {

    @Insert
    long insert(PhotoItem photoItem);

    @Delete
    void delete(PhotoItem photoItem);

    @Query("select * from PhotoItemTable where target_table=:tableName and target_id=:id")
    List<PhotoItem> getPhotosForTarget(long id, String tableName);

    @Query("delete from PhotoItemTable where target_table=:tableName and target_id=:id")
    void deleteForTarget(long id, String tableName);
}
