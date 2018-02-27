package com.indoor.flowers.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.model.GroupFlower;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class GroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insert(Group group);

    @Query("select * from GroupTable")
    public abstract List<Group> getAllGroups();

    @Query("select * from GroupTable where _id=:groupId")
    public abstract Group getGroupById(long groupId);

    @Update
    public abstract void update(Group group);

    @Query("select count(*)>0 from GroupTable where _id=:groupId")
    public abstract boolean hasGroup(long groupId);

    @Query("delete from GroupFlowerTable where group_id=:groupId")
    public abstract void deleteFlowersForGroup(long groupId);

    @Insert
    public abstract void insertGroupFlowers(List<GroupFlower> items);

    @Delete
    public abstract void deleteGroup(Group group);

    @Query("select * from GroupTable where _id in "
            + "(select group_id from GroupFlowerTable where flower_id=:id)")
    public abstract List<Group> getGroupsForFlower(long id);

    @Query("select name from GroupTable where _id=:targetId")
    public abstract String getGroupName(long targetId);

    public void updateGroupFlowers(long groupId, List<Flower> flowers) {
        deleteFlowersForGroup(groupId);
        if (flowers != null) {
            List<GroupFlower> groupFlowers = new ArrayList<>();
            for (Flower flower : flowers) {
                groupFlowers.add(new GroupFlower(groupId, flower.getId()));
            }

            insertGroupFlowers(groupFlowers);
        }
    }
}
