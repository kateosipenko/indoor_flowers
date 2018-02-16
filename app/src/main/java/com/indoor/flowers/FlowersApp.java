package com.indoor.flowers;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.os.StrictMode;

import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.database.FlowersDatabase;

public class FlowersApp extends Application {

    private static FlowersDatabase flowersDatabase;

    public static FlowersDatabase getDatabase() {
        return flowersDatabase;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this, BuildConfig.DEBUG);
        flowersDatabase = Room.databaseBuilder(this, FlowersDatabase.class,
                FlowersDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .addMigrations(FlowersDatabase.getMigrations())
                .build();
        turnOnStrictMode();
    }

    private void turnOnStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build());
    }
}
