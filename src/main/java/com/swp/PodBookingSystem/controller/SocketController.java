package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.socketPayload.UpdateOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SocketController {

    @MessageMapping("/payments")
    @SendTo("/topic/payments")
    public UpdateOrder updateOrder(@Payload UpdateOrder updateOrder) {
        log.info("Received message: {}", updateOrder.getId());
        return updateOrder;
    }
}
