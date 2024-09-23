package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.respone.OrderResponse;
import com.swp.PodBookingSystem.service.OrderDetailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/order-detail")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping
    public ApiResponse<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderDetailService.getAllOrders();
        logOrders(orders);
        return ApiResponse.<List<OrderResponse>>builder()
                .data(orders)
                .build();
    }

    @GetMapping("/{customerId}")
    public ApiResponse<List<OrderResponse>> getOrdersByCustomerId(@PathVariable String customerId) {
        List<OrderResponse> orders = orderDetailService.getOrdersByCustomerId(customerId);
        logOrders(orders);
        return ApiResponse.<List<OrderResponse>>builder()
                .data(orders)
                .build();
    }

    private void logOrders(List<OrderResponse> orders) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        log.info("Number of orders retrieved: {}", orders.size());
        orders.forEach(order -> log.info("Order ID: {}, Customer ID: {}", order.getId(), order.getCustomerId()));
    }

}
