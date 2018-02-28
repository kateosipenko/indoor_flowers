package com.indoor.flowers.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.FlowerWithWatering;
import com.indoor.flowers.model.NotificationType;

import java.util.List;

@Dao
public interface FlowersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Flower flower);

    @Update
    void update(Flower flower);

    @Delete
    void delete(Flower flower);

    @Query("select * from FlowerTable where _id=:flowerId")
    Flower getFlowerById(long flowerId);

    @Query("select * from FlowerTable where "
            + " FlowerTable._id in (select flower_id from GroupFlowerTable where group_id=:groupId)")
    List<Flower> getFlowersForGroup(long groupId);

    @Query("select * from FlowerTable where "
            + " FlowerTable._id not in (select flower_id from GroupFlowerTable)")
    List<Flower> getFlowersWithoutGroup();

    @Query("select * from FlowerTable")
    List<Flower> getAllFlowers();

    @Query("select FlowerTable.*, NotificationTable.frequency, EventActionTable.date from FlowerTable "
            + " inner join NotificationTable "
            + " on NotificationTable.target_id=FlowerTable._id "
            + " and NotificationTable.target_table='FlowerTable' and NotificationTable.type=" + NotificationType.WATERING
            + " inner join EventActionTable "
            + " on EventActionTable.notification_id=NotificationTable._id")
    List<FlowerWithWatering> getAllFlowersWithWatering();

    @Query("select FlowerTable.*, NotificationTable.frequency, EventActionTable.date from FlowerTable "
            + " inner join NotificationTable "
            + " on NotificationTable.target_id=FlowerTable._id "
            + " and NotificationTable.target_table='FlowerTable' and NotificationTable.type=" + NotificationType.WATERING
            + " inner join EventActionTable "
            + " on EventActionTable.notification_id=NotificationTable._id "
            + " where FlowerTable._id not in (select flower_id from GroupFlowerTable)")
    List<FlowerWithWatering> getFlowersWithoutGroupWithWatering();

    @Query("select count(*)>0 from FlowerTable where _id=:id")
    boolean hasFlower(long id);

    @Query("select name from FlowerTable where _id=:targetId")
    String getFlowerName(long targetId);
}
