package com.indoor.flowers.util;

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

    public static int getDaysDiff(Calendar start, Calendar end) {
        long startDays = TimeUnit.MILLISECONDS.toDays(start.getTimeInMillis());
        long endDays = TimeUnit.MILLISECONDS.toDays(end.getTimeInMillis());
        long diff = endDays - startDays;
        return (int) diff;
    }

    public static boolean isToday(Calendar calendar) {
        if (calendar == null)
            return false;

        Calendar current = Calendar.getInstance();
        return isSameDate(calendar, current);
    }

    public static boolean isOldDate(Calendar date) {
        if (date == null)
            return false;

        Calendar current = Calendar.getInstance();
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);
        return date.before(current);
    }

    public static boolean isSameDate(Calendar first, Calendar second) {
        return first != null && second != null
                && first.get(Calendar.YEAR) == second.get(Calendar.YEAR)
                && first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR);

    }
}
