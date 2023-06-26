package com.flux.integration.google.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CalendarEventService {

    @Autowired
    private Calendar calendar;

    public List<Event> getAllEvents() throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = calendar.events().list("primary")
                .setMaxResults(1000)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        events.getAccessRole();
        return events.getItems();
    }

    public String addEvent(Map<String, String> eventsParameters) throws IOException {
        Event event = new Event()
                .setSummary(eventsParameters.get("Name"))
                .setLocation("800 Howard St., San Francisco, CA 94103")
                .setDescription("A chance to hear more about Google's developer products.");

        DateTimeFormatter isoDtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime dateTime = LocalDateTime.parse(String.format("20%s-%s-%sT%s:00",eventsParameters.get("Year"),
                eventsParameters.get("Month"),
                eventsParameters.get("Day"),
                eventsParameters.get("Time")), isoDtf);
        DateTimeFormatter outputFormatter = DateTimeFormatter
                .ofPattern("uuuu-MM-dd'T'HH:mm:ss");


        DateTime startDateTime = new DateTime(dateTime.format(outputFormatter));
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setStart(start);

        DateTime endDateTime = new DateTime(dateTime.plusHours(2).format(outputFormatter));
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setEnd(end);

        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };

        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
        event = calendar.events().insert(calendarId, event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());
        return String.format("Event created: %s\n", event.getHtmlLink());
    }
}
