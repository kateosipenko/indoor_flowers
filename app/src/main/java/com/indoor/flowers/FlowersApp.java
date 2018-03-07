package com.indoor.flowers;

import android.app.Application;

import com.evgeniysharafan.utils.Res;
import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.database.FlowersDatabase;

import java.util.Locale;

public class FlowersApp extends Application {

    private static FlowersDatabase flowersDatabase;

    public static FlowersDatabase getDatabase() {
        return flowersDatabase;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this, BuildConfig.DEBUG);

        Locale locale = Locale.forLanguageTag("ru-RU");
        Locale.setDefault(locale);

        Res.get().getConfiguration().setLocale(locale);

        flowersDatabase = FlowersDatabase.createInstance(this);
    }
}
