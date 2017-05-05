package com.indoor.flowers;

import android.app.Application;

import com.evgeniysharafan.utils.Utils;

public class FlowersApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this, BuildConfig.DEBUG);
    }
}
