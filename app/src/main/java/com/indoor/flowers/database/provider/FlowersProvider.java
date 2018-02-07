package com.indoor.flowers.database.provider;

import android.content.Context;

import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Room;

import java.util.List;

public class FlowersProvider extends DatabaseProvider {

    public FlowersProvider(Context context) {
        super(context);
    }

    // region ROOM

    public void createRoom(Room room) {
        room.setId(invalidateIdForInsert(room.getId()));
        long id = database.getRoomDao().insert(room);
        room.setId(id);
    }

    public List<Room> getAllRooms() {
        return database.getRoomDao().getAllRooms();
    }

    public Room getRoomById(long roomID) {
        return database.getRoomDao().geRoomById(roomID);
    }

    // endregion ROOM

    // region FLOWER

    public void createFlower(Flower flower) {
        flower.setId(invalidateIdForInsert(flower.getId()));
        long id = database.getFlowersDao().insert(flower);
        flower.setId(id);
    }

    public List<Flower> getAllFlowers() {
        return database.getFlowersDao().getAllFlowers();
    }

    // endregion FLOWER
}
