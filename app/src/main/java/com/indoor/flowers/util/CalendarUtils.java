package com.indoor.flowers.util;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CalendarUtils {

    public static String getNameForMonth(int month) {
        String result = null;
        Calendar temp = Calendar.getInstance();
        temp.clear();
        if (month >= temp.getActualMinimum(Calendar.MONTH)
                && month <= temp.getActualMaximum(Calendar.MONTH)) {
            temp.set(Calendar.MONTH, month);
            result = String.format(Locale.getDefault(), "%1$tb", temp);
        }

        return result;
    }

    public static String getDaysToWateringText(Calendar start, Calendar end) {
        long startDays = TimeUnit.MILLISECONDS.toDays(start.getTimeInMillis());
        long endDays = TimeUnit.MILLISECONDS.toDays(end.getTimeInMillis());
        long daysDiff = endDays - startDays;
        return Res.getString(R.string.days_to_watering_format, daysDiff);
    }
}
