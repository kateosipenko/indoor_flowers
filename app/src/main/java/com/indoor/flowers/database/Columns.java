package com.indoor.flowers.database;

import android.provider.BaseColumns;

public @interface Columns {
    String ID = BaseColumns._ID;
    String NAME = "name";
    String IMAGE_PATH = "image_path";
    String HUMIDITY = "humidity";
    String BRIGHTNESS = "brightness";
    String GROUP_ID = "group_id";
    String ACTIVE_FROM = "active_from";
    String ACTIVE_TO = "active_to";
    String PASSIVE_FROM = "passive_from";
    String PASSIVE_TO = "passive_to";
    String LAST_WATERING_DATE = "last_watering_date";
    String WATERING_PERIOD = "watering_period";
    String SETTING_DATA_ID = "setting_data_id";
    String USE_COMMON_SETTINGS = "use_common_settings";
    String SETTINGS_PREFIX = "stg_";
}
