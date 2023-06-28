package com.flux.integration.controller;

import com.flux.integration.model.ChatMessage;
import com.flux.integration.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.flux.integration.model.ChatMessage.MessageType.CHAT;

@Controller
public class ChatController {

    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    public ChatController(MessageService messageService, SimpMessagingTemplate simpMessagingTemplate) {
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, @Header("simpSessionId") String sessionId) {
        logger.info(chatMessage.getContent(), "Received a new message: {}");

        return messageService.receive(chatMessage);
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

}