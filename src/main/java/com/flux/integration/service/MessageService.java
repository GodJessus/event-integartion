package com.flux.integration.service;

import com.flux.integration.google.service.CalendarEventService;
import com.flux.integration.model.ChatMessage;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MessageService {

    private final CalendarEventService calendarEventService;

    public MessageService(CalendarEventService calendarEventService) {
        this.calendarEventService = calendarEventService;
    }

    @SneakyThrows
    public ChatMessage receive(ChatMessage chatMessage) {

        Pattern pattern = Pattern.compile("^([1-9]|[012][0-9]|3[01])\\.([0]{0,1}[1-9]|1[012])\\.(\\d\\d) ([012]{0,1}[0-9]:[0-6][0-9]) ([aA-zZ]*)");
        Matcher matcher = pattern.matcher(chatMessage.getContent());
        if (matcher.find()) {
            String event = calendarEventService.addEvent(populateEvent(matcher));
            return buildResponseMessage(chatMessage, event);
        }

        return chatMessage;
    }

    private ChatMessage buildResponseMessage(ChatMessage chatMessage, String s) {
        chatMessage.setContent("Created new event for: ".concat(chatMessage.getContent()));
        chatMessage.setLink(s);
        return chatMessage;
    }

    private Map<String, String> populateEvent(Matcher matcher) {
        return Map.of(
                "Day", matcher.group(1),
                "Month", matcher.group(2),
                "Year", matcher.group(3),
                "Time", matcher.group(4),
                "Name", matcher.group(5)
        );
    }
}
