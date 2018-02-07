package com.indoor.flowers.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.IntDef;

import com.indoor.flowers.database.dao.FlowersDao;
import com.indoor.flowers.database.dao.RoomDao;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Room;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Database(version = FlowersDatabase.DATABASE_VERSION,
        entities = {
                Room.class,
                Flower.class
        })
public abstract class FlowersDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "indoorFlowers.db";
    static final int DATABASE_VERSION = Versions.VERSION_1;

    public static Migration[] getMigrations() {
        return new Migration[]{
        };
    }

    public abstract FlowersDao getFlowersDao();

    public abstract RoomDao getRoomDao();

    @IntDef({Versions.VERSION_1})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Versions {
        int VERSION_1 = 1;
    }
}