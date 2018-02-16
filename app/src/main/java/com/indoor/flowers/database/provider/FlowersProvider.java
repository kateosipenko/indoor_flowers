package com.indoor.flowers.database.provider;

import android.content.Context;

import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;

import java.util.List;

public class FlowersProvider extends DatabaseProvider {

    public FlowersProvider(Context context) {
        super(context);
    }

    // region GROUP

    public void createGroup(Group group) {
        group.setId(invalidateIdForInsert(group.getId()));
        long id = database.getGroupDao().insert(group);
        group.setId(id);
    }

    public List<Group> getAllGroups() {
        return database.getGroupDao().getAllGroups();
    }

    public Group getGroupById(long groupId) {
        return database.getGroupDao().getGroupById(groupId);
    }

    public void updateGroup(Group group) {
        database.getGroupDao().update(group);
    }

    public void setGroupLastTimeWatering(long groupId, long timeInMillis) {
        database.getGroupDao().setGroupLastTimeWatering(groupId, timeInMillis);
    }

    // endregion GROUP

    // region FLOWER

    public void createFlower(Flower flower) {
        flower.setId(invalidateIdForInsert(flower.getId()));
        long id = database.getFlowersDao().insert(flower);
        flower.setId(id);
    }

    public List<Flower> getAllFlowers() {
        return database.getFlowersDao().getAllFlowers();
    }

    public Flower getFlowerById(long flowerId) {
        return database.getFlowersDao().getFlowerById(flowerId);
    }

    public List<Flower> getFlowersForGroup(long groupId) {
        return database.getFlowersDao().getFlowersForGroup(groupId);
    }

    public void updateFlower(Flower flower) {
        database.getFlowersDao().update(flower);
    }

    public List<Flower> getFlowersWithoutGroup() {
        return database.getFlowersDao().getFlowersWithoutGroup();
    }

    public void deleteFlower(Flower flower) {
        database.getFlowersDao().delete(flower);
    }

    public void setFlowerLastTimeWatering(long flowerId, long timeInMillis) {
        database.getFlowersDao().setFlowerLastTimeWatering(flowerId, timeInMillis);
    }

    // endregion FLOWER
}
