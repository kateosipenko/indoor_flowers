package com.indoor.flowers.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.indoor.flowers.database.Columns;
import com.indoor.flowers.model.Room;

import java.util.List;

@Dao
public interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Room room);

    @Query("select * from " + Room.TABLE_NAME)
    List<Room> getAllRooms();

    @Query("select * from " + Room.TABLE_NAME + " where "
            + Columns.ID + "=:roomID")
    Room geRoomById(long roomID);
}
