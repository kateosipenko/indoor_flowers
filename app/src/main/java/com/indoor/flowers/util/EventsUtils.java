package com.indoor.flowers.util;

import com.indoor.flowers.model.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class EventsUtils {

    public static HashMap<Integer, List<Event>> groupEventsByDays(List<Event> events, Calendar minDate,
                                                                  Calendar maxDate) {
        HashMap<Integer, List<Event>> eventsByDays = new HashMap<>();
        int daysCount = CalendarUtils.getDaysDiff(minDate, maxDate);
        int startDayOfYear = minDate.get(Calendar.DAY_OF_YEAR);
        for (int i = 0; i < daysCount; i++) {
            eventsByDays.put(startDayOfYear, new ArrayList<Event>());
            ++startDayOfYear;
        }

        for (Event event : events) {
            if (event.getFrequency() != null) {
                if (event.getEventDate().after(maxDate)) {
                    continue;
                }

                int firstEventDay = event.getEventDate().get(Calendar.DAY_OF_YEAR);
                if (!eventsByDays.containsKey(firstEventDay)) {
                    long daysDiff = CalendarUtils.getDaysDiff(event.getEventDate(), minDate);
                    firstEventDay = (int) (daysDiff % event.getFrequency()
                            + minDate.get(Calendar.DAY_OF_YEAR));
                }
                List<Event> eventsPerDay = eventsByDays.get(firstEventDay);
                if (eventsPerDay != null) {
                    do {
                        eventsPerDay.add(event);
                        firstEventDay += event.getFrequency();
                        eventsPerDay = eventsByDays.get(firstEventDay);
                    } while (eventsPerDay != null);
                }
            } else {
                List<Event> eventsPerDay = eventsByDays.get(event.getEventDate().get(Calendar.DAY_OF_YEAR));
                eventsPerDay.add(event);
            }
        }

        return eventsByDays;
    }

}
