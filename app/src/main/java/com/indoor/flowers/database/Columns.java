package com.indoor.flowers.database;

import android.provider.BaseColumns;

public @interface Columns {
    String ID = BaseColumns._ID;
    String SETTING_ID = "setting_id";
    String NAME = "name";
    String IMAGE_PATH = "image_path";
    String GROUP_ID = "group_id";
    String LAST_WATERING_DATE = "last_watering_date";
    String PREFERRED_TIME = "preferred_time";
    String WATERING_PERIOD = "watering_period";
    String SETTING_DATA_ID = "setting_data_id";
    String USE_COMMON_SETTINGS = "use_common_settings";
    String LAST_NUTRITION_DATE = "last_nutrition_date";
    String NUTRITION_FREQ = "nutrition_freq";
    String LAST_TRANSPLANTING_DATE = "last_transplanting_date";
    String NEXT_TRANSPLANTING_DATE = "next_transplanting_date";
    String FLOWER_ID = "flower_id";
    String EVENT_TYPE = "event_type";
    String TARGET_ID = "target_id";
    String CREATION_DATE = "creation_date";
    String EVENT_DATE = "event_date";
    String TARGET_TABLE = "target_table";
    String FREQUENCY = "frequency";
    String END_DATE = "end_date";
    String TITLE = "title";
}
