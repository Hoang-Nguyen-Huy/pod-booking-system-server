package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.Building.BuildingPaginationDTO;
import com.swp.PodBookingSystem.dto.respone.OrderDetail.OrderDetailResponse;
import com.swp.PodBookingSystem.dto.respone.PaginationResponse;
import com.swp.PodBookingSystem.entity.Building;
import com.swp.PodBookingSystem.service.OrderDetailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;

import org.springframework.data.domain.Page;
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

    private OrderDetailService orderDetailService;

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
    PaginationResponse<List<OrderDetailResponse>> getBuildings(@RequestParam(defaultValue = "1", name = "page") int page,
                                                               @RequestParam(defaultValue = "3", name = "take") int take,
                                                               @PathVariable String customerId
    ) {
        BuildingPaginationDTO dto = new BuildingPaginationDTO(page, take);
        Page<OrderDetailResponse> buildingPage = orderDetailService.getOrdersByCustomerId(customerId, dto.page, dto.take);
        return PaginationResponse.<List<OrderDetailResponse>>builder()
                .data(buildingPage.getContent())
                .currentPage(buildingPage.getNumber() + 1)
                .totalPage(buildingPage.getTotalPages())
                .recordPerPage(buildingPage.getNumberOfElements())
                .totalRecord((int) buildingPage.getTotalElements())
                .build();
    }

    private void logOrders(List<OrderDetailResponse> orders) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        log.info("Number of orders retrieved: {}", orders.size());
        orders.forEach(order -> log.info("Order ID: {}, Customer ID: {}", order.getId(), order.getCustomerId()));
    }
}