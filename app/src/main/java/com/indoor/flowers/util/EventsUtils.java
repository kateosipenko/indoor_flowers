package com.indoor.flowers.util;

import android.support.annotation.ColorInt;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.model.Notification;
import com.indoor.flowers.model.NotificationType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class EventsUtils {

    public static String getTitleForEvent(@NotificationType int eventType) {
        String result = "";
        switch (eventType) {
            case NotificationType.CREATED:
                result = Res.getString(R.string.event_created);
                break;
            case NotificationType.FERTILIZER:
                result = Res.getString(R.string.event_fertilizer);
                break;
            case NotificationType.TRANSPLANTING:
                result = Res.getString(R.string.event_transplanting);
                break;
            case NotificationType.WATERING:
                result = Res.getString(R.string.event_watering);
                break;
        }
        return result;
    }

    @ColorInt
    public static int getColorForEventType(@NotificationType int eventType) {
        @ColorInt int result = Res.getColor(R.color.material_white);
        switch (eventType) {
            case NotificationType.CREATED:
                result = Res.getColor(R.color.event_created);
                break;
            case NotificationType.FERTILIZER:
                result = Res.getColor(R.color.event_fertilizer);
                break;
            case NotificationType.TRANSPLANTING:
                result = Res.getColor(R.color.event_transplantation);
                break;
            case NotificationType.WATERING:
                result = Res.getColor(R.color.event_watering);
                break;
        }

        return result;
    }

    public static List<Notification> createOrderedEventsWithPeriodically(List<Notification> events, Calendar minDate,
                                                                         Calendar maxDate, boolean includeOldEvents) {
        List<Notification> result = new ArrayList<>();
        for (Notification event : events) {
            if (event.getFrequency() != null) {
                if (event.getDate().after(maxDate)) {
                    continue;
                }

                Calendar eventDate = (Calendar) event.getDate().clone();
                if (eventDate.before(minDate) && !includeOldEvents) {
                    long daysDiff = CalendarUtils.getDaysDiff(event.getDate(), minDate);
                    eventDate = (Calendar) minDate.clone();
                    eventDate.add(Calendar.DAY_OF_YEAR, (int) (daysDiff % event.getFrequency()));
                }

                do {
                    Notification periodically = event.clone();
                    periodically.setDate(eventDate);
                    result.add(periodically);
                    eventDate = (Calendar) eventDate.clone();
                    eventDate.add(Calendar.DAY_OF_YEAR, event.getFrequency());
                } while (eventDate.before(maxDate));
            } else {
                result.add(event);
            }
        }

        Collections.sort(result, new EventDatesComparator());
        return result;
    }

    public static HashMap<Integer, List<Notification>> groupNotifications(List<Notification> notifications, Calendar minDate,
                                                                          Calendar maxDate) {
        HashMap<Integer, List<Notification>> eventsByDays = new HashMap<>();
        int daysCount = CalendarUtils.getDaysDiff(minDate, maxDate);
        int startDayOfYear = minDate.get(Calendar.DAY_OF_YEAR);
        for (int i = 0; i < daysCount; i++) {
            eventsByDays.put(startDayOfYear, new ArrayList<Notification>());
            ++startDayOfYear;
        }

        for (Notification notification : notifications) {
            if (notification.getFrequency() != null) {
                if (notification.getDate().after(maxDate)) {
                    continue;
                }

                int firstEventDay = notification.getDate().get(Calendar.DAY_OF_YEAR);
                if (!eventsByDays.containsKey(firstEventDay)) {
                    long daysDiff = CalendarUtils.getDaysDiff(notification.getDate(), minDate);
                    firstEventDay = (int) (daysDiff % notification.getFrequency()
                            + minDate.get(Calendar.DAY_OF_YEAR));
                }
                List<Notification> itemsPerDay = eventsByDays.get(firstEventDay);
                if (itemsPerDay != null) {
                    do {
                        itemsPerDay.add(notification);
                        firstEventDay += notification.getFrequency();
                        itemsPerDay = eventsByDays.get(firstEventDay);
                    } while (itemsPerDay != null);
                }
            } else {
                List<Notification> itemsPerDay = eventsByDays.get(notification.getDate().get(Calendar.DAY_OF_YEAR));
                itemsPerDay.add(notification);
            }
        }

        return eventsByDays;
    }

}
