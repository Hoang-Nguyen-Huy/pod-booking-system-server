package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.respone.OrderResponse;
import com.swp.PodBookingSystem.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ApiResponse<List<OrderResponse>> getAllOrders(){
        List<OrderResponse> orders = orderService.getAllOrders();
        logOrders(orders);
        return ApiResponse.<List<OrderResponse>>builder()
                .data(orders)
                .build();
    }   

    @GetMapping("/{accountId}")
    public ApiResponse<List<OrderResponse>> getOrdersByAccountId(@PathVariable String accountId) {
        List<OrderResponse> orders = orderService.getOrdersByAccountId(accountId);
        logOrders(orders);
        return ApiResponse.<List<OrderResponse>>builder()
                .data(orders)
                .build();
    }


    private void logOrders(List<OrderResponse> orders) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        log.info("Number of orders retrieved: {}", orders.size());
        orders.forEach(order -> log.info("Order ID: {}, Account ID: {}", order.getId(), order.getAccountId()));
    }
}
