package com.indoor.flowers.database.provider;

import android.content.Context;

import com.indoor.flowers.model.EventAction;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.FlowerWithWatering;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.model.GroupWithWatering;
import com.indoor.flowers.model.NotificationType;
import com.indoor.flowers.model.NotificationWithTarget;
import com.indoor.flowers.model.PhotoItem;

import java.util.List;

public class FlowersProvider extends DatabaseProvider {

    public FlowersProvider(Context context) {
        super(context);
    }

    // region GROUP

    public List<Group> getGroupsForFlower(long id) {
        return database.getGroupDao().getGroupsForFlower(id);
    }

    public List<GroupWithWatering> getAllGroupsWithWatering() {
        return database.getGroupDao().getAllGroupsWithWatering();
    }

    public void deleteGroup(Group group) {
        database.getNotificationDao().deleteForTarget(group.getId(), Group.TABLE_NAME);
        database.getGroupDao().deleteFlowersForGroup(group.getId());
        database.getGroupDao().deleteGroup(group);
    }

    public void createGroup(Group group) {
        group.setId(invalidateIdForInsert(group.getId()));
        long id = database.getGroupDao().insert(group);
        group.setId(id);
        createEventForCreation(group.getId(), Group.TABLE_NAME, group.getName());
    }

    public void updateGroup(Group group) {
        database.getGroupDao().update(group);
    }

    public void refreshGroupFlowers(long groupId, List<Flower> flowers) {
        database.getGroupDao().updateGroupFlowers(groupId, flowers);
    }

    public List<Group> getAllGroups() {
        return database.getGroupDao().getAllGroups();
    }

    public Group getGroupById(long groupId) {
        return database.getGroupDao().getGroupById(groupId);
    }

    // endregion GROUP

    // region FLOWER

    public NotificationWithTarget getLastNotificationAction(String targetTable, long targetId,
                                                            @NotificationType int type) {
        EventAction action = Flower.TABLE_NAME.equals(targetTable)
                ? database.getFlowersDao().getFlowerLastNotificationAction(targetId, type)
                : database.getFlowersDao().getGrouprLastNotificationAction(targetId, type);
        NotificationWithTarget result = null;
        if (action != null) {
            result = database.getNotificationDao().getNotificationWithTarget(
                    action.getNotificationId());
            if (result != null) {
                result.setEventDate(action.getDate());
            }
        }

        return result;
    }

    public void updateFlower(Flower flower) {
        database.getFlowersDao().update(flower);
    }

    public void createFlower(Flower flower) {
        flower.setId(invalidateIdForInsert(flower.getId()));
        long id = database.getFlowersDao().insert(flower);
        flower.setId(id);

        createEventForCreation(id, Flower.TABLE_NAME, flower.getName());
    }

    public List<Flower> getAllFlowers() {
        return database.getFlowersDao().getAllFlowers();
    }

    public List<FlowerWithWatering> getAllFlowersWithWatering() {
        return database.getFlowersDao().getAllFlowersWithWatering();
    }

    public List<FlowerWithWatering> getFlowersWithoutGroupWithWatering() {
        return database.getFlowersDao().getFlowersWithoutGroupWithWatering();
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
        if (deleteTargetNotifications) {
            database.getNotificationDao().deleteForTarget(flower.getId(), Flower.TABLE_NAME);
        }
        database.getPhotoItemDao().deleteForTarget(flower.getId(), Flower.TABLE_NAME);
        database.getFlowersDao().delete(flower);
    }

    // endregion FLOWER

    // region PHOTOS

    public List<PhotoItem> getPhotosForTarget(long id, String tableName) {
        return database.getPhotoItemDao().getPhotosForTarget(id, tableName);
    }

    public void addPhoto(PhotoItem photoItem) {
        photoItem.setId(invalidateIdForInsert(photoItem.getId()));
        long id = database.getPhotoItemDao().insert(photoItem);
        photoItem.setId(id);
    }

    public String getTargetName(long targetId, String targetTable) {
        String result = null;
        if (Flower.TABLE_NAME.equalsIgnoreCase(targetTable)) {
            result = database.getFlowersDao().getFlowerName(targetId);
        } else if (Group.TABLE_NAME.equalsIgnoreCase(targetTable)) {
            result = database.getGroupDao().getGroupName(targetId);
        }

        return result;
    }

    public void deletePhoto(PhotoItem photoItem) {
        database.getPhotoItemDao().delete(photoItem);
    }

    // endregion PHOTOS
}
