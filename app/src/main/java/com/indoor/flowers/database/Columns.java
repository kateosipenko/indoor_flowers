package com.indoor.flowers.database;

import android.provider.BaseColumns;

public @interface Columns {
    String ID = BaseColumns._ID;
    String NAME = "name";
    String IMAGE_PATH = "image_path";
    String TEMPERATURE = "temperature";
    String HUMIDITY = "humidity";
    String BRIGHTNESS = "brightness";
    String ROOM_ID = "room_id";
    String ACTIVE_FROM = "active_from";
    String ACTIVE_TO = "active_to";
    String PASSIVE_FROM = "passive_from";
    String PASSIVE_TO = "passive_to";
    String LAST_WATERING_DATE = "last_watering_date";
}
