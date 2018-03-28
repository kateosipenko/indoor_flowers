package com.indoor.flowers.database;

import android.provider.BaseColumns;

public @interface Columns {
    String ID = BaseColumns._ID;
    String NAME = "name";
    String IMAGE_PATH = "image_path";
    String GROUP_ID = "group_id";
    String FLOWER_ID = "flower_id";
    String TYPE = "type";
    String TARGET_ID = "target_id";
    String EVENT_DATE = "event_date";
    String TARGET_TABLE = "target_table";
    String FREQUENCY = "frequency";
    String END_DATE = "end_date";
    String TITLE = "title";
    String COMMENT = "comment";
    String DATE = "date";
    String NOTIFICATION_ID = "notification_id";
    String ACTIVE = "active";
}
