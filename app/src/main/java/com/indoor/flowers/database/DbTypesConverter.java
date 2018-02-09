package com.indoor.flowers.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Calendar;

public class DbTypesConverter {
    @TypeConverter
    public static long dateToLong(Calendar calendar) {
        return calendar != null ? calendar.getTimeInMillis() : 0;
    }

    @TypeConverter
    public static Calendar longToDate(long value) {
        Calendar result = null;
        if (value != 0) {
            result = Calendar.getInstance();
            result.setTimeInMillis(value);
        }

        return result;
    }
}
