package com.indoor.flowers.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;

import com.indoor.flowers.database.Columns;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = GroupFlower.TABLE_NAME,
        primaryKeys = {Columns.GROUP_ID, Columns.FLOWER_ID},
        foreignKeys = {
                @ForeignKey(entity = Group.class, parentColumns = Columns.ID,
                        childColumns = Columns.GROUP_ID, onDelete = CASCADE),
                @ForeignKey(entity = Flower.class, parentColumns = Columns.ID,
                        childColumns = Columns.FLOWER_ID, onDelete = CASCADE)})
public class GroupFlower {

    public static final String TABLE_NAME = "GroupFlowerTable";

    @ColumnInfo(name = Columns.GROUP_ID)
    private long groupId;
    @ColumnInfo(name = Columns.FLOWER_ID)
    private long flowerId;

    public GroupFlower() {
    }

    @Ignore
    public GroupFlower(long groupId, long flowerId) {
        this.groupId = groupId;
        this.flowerId = flowerId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getFlowerId() {
        return flowerId;
    }

    public void setFlowerId(long flowerId) {
        this.flowerId = flowerId;
    }
}
