package com.indoor.flowers.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.indoor.flowers.database.Columns;
import com.indoor.flowers.model.Group;

import java.util.List;

@Dao
public interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Group group);

    @Query("select * from " + Group.TABLE_NAME)
    List<Group> getAllGroups();

    @Query("select * from " + Group.TABLE_NAME + " where "
            + Columns.ID + "=:groupId")
    Group getGroupById(long groupId);
}
