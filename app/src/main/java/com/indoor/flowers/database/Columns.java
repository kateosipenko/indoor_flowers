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
    String PERIOD = "period";
}
