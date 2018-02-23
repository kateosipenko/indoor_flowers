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

    public void deleteGroup(Group group) {
        database.getNotificationDao().deleteForTarget(group.getId(), Group.TABLE_NAME);
        database.getGroupDao().deleteFlowersForGroup(group.getId());
        database.getGroupDao().deleteGroup(group);
    }

    public void createOrUpdateGroup(Group group, List<Flower> flowers) {
        if (database.getGroupDao().hasGroup(group.getId())) {
            database.getGroupDao().update(group);
        } else {
            group.setId(invalidateIdForInsert(group.getId()));
            long id = database.getGroupDao().insert(group);
            group.setId(id);
            createEventForCreation(group.getId(), Group.TABLE_NAME, group.getName());
        }

        database.getGroupDao().updateGroupFlowers(group.getId(), flowers);
    }

    public List<Group> getAllGroups() {
        return database.getGroupDao().getAllGroups();
    }

    public Group getGroupById(long groupId) {
        return database.getGroupDao().getGroupById(groupId);
    }

    // endregion GROUP

    // region FLOWER

    public void updateFlower(Flower flower) {
        database.getFlowersDao().update(flower);
    }

    public void createOrUpdateFlower(Flower flower) {
        if (database.getFlowersDao().hasFlower(flower.getId())) {
            database.getFlowersDao().update(flower);
        } else {
            flower.setId(invalidateIdForInsert(flower.getId()));
            long id = database.getFlowersDao().insert(flower);
            flower.setId(id);

            createEventForCreation(id, Flower.TABLE_NAME, flower.getName());
        }
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

    public List<Flower> getFlowersWithoutGroup() {
        return database.getFlowersDao().getFlowersWithoutGroup();
    }

    public void deleteFlower(Flower flower, boolean deleteTargetNotifications) {
        database.getFlowersDao().delete(flower);
        if (deleteTargetNotifications) {
            database.getNotificationDao().deleteForTarget(flower.getId(), Flower.TABLE_NAME);
        }
    }

    // endregion FLOWER
}
