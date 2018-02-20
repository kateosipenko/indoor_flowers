package com.indoor.flowers.util;

import android.support.annotation.ColorInt;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.model.Event;
import com.indoor.flowers.model.EventType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class EventsUtils {

    @ColorInt
    public static int getColorForEventType(@EventType int eventType) {
        @ColorInt int result = Res.getColor(R.color.material_white);
        switch (eventType) {
            case EventType.CREATED:
                result = Res.getColor(R.color.event_created);
                break;
            case EventType.NUTRITION:
                result = Res.getColor(R.color.event_nutrition);
                break;
            case EventType.TRANSPLANTING:
                result = Res.getColor(R.color.event_transplantation);
                break;
            case EventType.WATERING:
                result = Res.getColor(R.color.event_watering);
                break;
        }

        return result;
    }

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
