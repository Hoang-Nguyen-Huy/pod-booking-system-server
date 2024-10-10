package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
public class SocketController {
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        log.info("Received message: {}", chatMessage.getContent());
        chatMessage.setTimestamp(new Date());
        return chatMessage;
    }
}
