package com.indoor.flowers.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.indoor.flowers.model.EventAction;
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

    @Query("select FlowerTable.*, \n" +
            "(select NotificationTable.frequency from NotificationTable where  NotificationTable.target_id=FlowerTable._id \n" +
            " and NotificationTable.target_table='FlowerTable' and NotificationTable.type=1) as frequency,\n" +
            " (select EventActionTable.date from EventActionTable where EventActionTable.notification_id=(select NotificationTable._id from NotificationTable where  NotificationTable.target_id=FlowerTable._id \n" +
            " and NotificationTable.target_table='FlowerTable' and NotificationTable.type=1)) as date\n" +
            " from FlowerTable\n")
    List<FlowerWithWatering> getAllFlowersWithWatering();

    @Query("select FlowerTable.*, \n" +
            "(select NotificationTable.frequency from NotificationTable where  NotificationTable.target_id=FlowerTable._id \n" +
            " and NotificationTable.target_table='FlowerTable' and NotificationTable.type=1) as frequency,\n" +
            " (select EventActionTable.date from EventActionTable where EventActionTable.notification_id=(select NotificationTable._id from NotificationTable where  NotificationTable.target_id=FlowerTable._id \n" +
            " and NotificationTable.target_table='FlowerTable' and NotificationTable.type=1)) as date\n" +
            " from FlowerTable\n"
            + " where FlowerTable._id not in (select flower_id from GroupFlowerTable)")
    List<FlowerWithWatering> getFlowersWithoutGroupWithWatering();

    @Query("select count(*)>0 from FlowerTable where _id=:id")
    boolean hasFlower(long id);

    @Query("select name from FlowerTable where _id=:targetId")
    String getFlowerName(long targetId);

    @Query("select * from EventActionTable " +
            " where notification_id in " +
            "(  select _id from NotificationTable where " +
            "     type=:type and " +
            "     (target_id=:flowerId and target_table='FlowerTable' " +
            "     or target_table='GroupTable' and " +
            "        (select distinct(_id) from FlowerTable where _id in " +
            "              (select flower_id from GroupFlowerTable where group_id=target_id)" +
            "        )==1" +
            "     )" +
            ") order by date limit 1")
    EventAction getFlowerLastNotificationAction(long flowerId, @NotificationType int type);

    @Query("select * from EventActionTable " +
            " where notification_id in " +
            "(  select _id from NotificationTable where " +
            "     type=:type and " +
            "     (target_id=:groupId and target_table='GroupTable' " +
            "     )" +
            ") order by date limit 1")
    EventAction getGrouprLastNotificationAction(long groupId, @NotificationType int type);
}
