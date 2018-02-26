package com.indoor.flowers;

import android.app.Application;

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
        flowersDatabase = FlowersDatabase.createInstance(this);
    }
}
