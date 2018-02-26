package com.indoor.flowers.util;

import com.evgeniysharafan.utils.PrefUtils;

import java.util.Calendar;

public final class Prefs {

    private static final String KEY_PREFERRED_NOTIFICATION_TIME = "key_preferred_notification_time";
    private static final String KEY_HIDE_FLOWERS_WITH_GROUPS = "key_hide_flowers_with_group";

    public static boolean getHideFlowersWithGroups() {
        return PrefUtils.getBool(KEY_HIDE_FLOWERS_WITH_GROUPS, true);
    }

    public static void setHideFlowersWithGroup(boolean needHide) {
        PrefUtils.put(KEY_HIDE_FLOWERS_WITH_GROUPS, needHide);
    }

    public static Calendar getPreferredNotificationTime() {
        long saved = PrefUtils.getLong(KEY_PREFERRED_NOTIFICATION_TIME, 0);
        Calendar result = Calendar.getInstance();
        if (saved != 0) {
            result.setTimeInMillis(saved);
        }

        return result;
    }

    public static void setPreferredNotificationTime(Calendar time) {
        PrefUtils.put(KEY_PREFERRED_NOTIFICATION_TIME, time.getTimeInMillis());
    }
}
