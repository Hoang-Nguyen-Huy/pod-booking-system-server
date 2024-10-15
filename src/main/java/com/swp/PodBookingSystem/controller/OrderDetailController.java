package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.respone.Amenity.AmenityManagementResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetail.OrderDetailResponse;
import com.swp.PodBookingSystem.entity.OrderDetailAmenity;
import com.swp.PodBookingSystem.service.OrderDetailAmenityService;
import com.swp.PodBookingSystem.service.OrderDetailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;

import org.springframework.http.HttpStatus;
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
    @Autowired
    private OrderDetailAmenityService orderDetailAmenityService;

    @GetMapping
    public ApiResponse<List<OrderDetailResponse>> getAllOrders() {
        try {
            List<OrderDetailResponse> orders = orderDetailService.getAllOrders();
            logOrders(orders);
            return ApiResponse.<List<OrderDetailResponse>>builder()
                    .data(orders)
                    .build();
        } catch (Exception e) {
            log.error("Error creating order detail: ", e);
            return ApiResponse.<List<OrderDetailResponse>>builder()
                    .message("Failed to create order detail: " + e.getMessage())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }

    @GetMapping("/{customerId}")
    public ApiResponse<List<OrderDetailResponse>> getOrdersByCustomerId(@PathVariable String customerId) {
        List<OrderDetailResponse> orders = orderDetailService.getOrdersByCustomerId(customerId);
        logOrders(orders);
        return ApiResponse.<List<OrderDetailResponse>>builder()
                .data(orders)
                .build();
    }

//    @GetMapping("/order-amenity/{orderDetailId}")
//    public ApiResponse<List<AmenityManagementResponse>> getAmenitiesByOrdersByCustomerId(@PathVariable String orderDetailId) {
//        List<AmenityManagementResponse> orders = orderDetailAmenityService.getOrderDetailAmenitiesByOrderDetailId(orderDetailId);
//        return ApiResponse.<List<AmenityManagementResponse>>builder()
//                .data(orders)
//                .build();
//    }

//    @GetMapping("/order-amenitysAll")
//    public ApiResponse<List<OrderDetailAmenity>> getAll() {
//        List<OrderDetailAmenity> orders = orderDetailAmenityService.getAll();
//        return ApiResponse.<List<OrderDetailAmenity>>builder()
//                .data(orders)
//                .build();
//    }


    private void logOrders(List<OrderDetailResponse> orders) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        log.info("Number of orders retrieved: {}", orders.size());
        orders.forEach(order -> log.info("Order ID: {}, Customer ID: {}", order.getId(), order.getCustomerId()));
    }

}