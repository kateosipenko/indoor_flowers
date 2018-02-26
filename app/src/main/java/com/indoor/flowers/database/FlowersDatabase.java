package com.indoor.flowers.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.IntDef;

import com.indoor.flowers.database.dao.EventActionDao;
import com.indoor.flowers.database.dao.FlowersDao;
import com.indoor.flowers.database.dao.GroupDao;
import com.indoor.flowers.database.dao.NotificationDao;
import com.indoor.flowers.model.EventAction;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.model.GroupFlower;
import com.indoor.flowers.model.Notification;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Database(version = FlowersDatabase.DATABASE_VERSION,
        entities = {
                Group.class,
                Flower.class,
                GroupFlower.class,
                Notification.class,
                EventAction.class
        })
public abstract class FlowersDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "indoorFlowers.db";

    static final int DATABASE_VERSION = Versions.VERSION_1;

    public static FlowersDatabase createInstance(Context context) {
        return Room.databaseBuilder(context, FlowersDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .addMigrations(FlowersDatabase.getMigrations())
                .build();
    }

    public static Migration[] getMigrations() {
        return new Migration[]{
        };
    }

    public abstract FlowersDao getFlowersDao();

    public abstract GroupDao getGroupDao();

    public abstract NotificationDao getNotificationDao();

    public abstract EventActionDao getEventActionDao();

    @IntDef({Versions.VERSION_1})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Versions {
        int VERSION_1 = 1;
    }
}