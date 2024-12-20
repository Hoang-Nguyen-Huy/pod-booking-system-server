package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.Building.BuildingPaginationDTO;
import com.swp.PodBookingSystem.dto.request.Order.OrderDetailUpdateStaffRequest;
import com.swp.PodBookingSystem.dto.respone.Order.NumberOrderByBuildingDto;
import com.swp.PodBookingSystem.dto.respone.OrderDetail.OrderDetailFullInfoResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetail.OrderDetailResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetail.RevenueChartDto;
import com.swp.PodBookingSystem.dto.respone.OrderResponse;
import com.swp.PodBookingSystem.dto.respone.PaginationResponse;
import com.swp.PodBookingSystem.service.OrderDetailService;
import com.swp.PodBookingSystem.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/order-detail")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderDetailController {

    private OrderDetailService orderDetailService;
    private OrderService orderService;

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

    @GetMapping("/{orderDetailId}")
    public ApiResponse<OrderDetailFullInfoResponse> getOrderDetail(@PathVariable String orderDetailId) {
        OrderDetailFullInfoResponse orderDetail = orderDetailService.getOrderDetailByOrderDetailId(orderDetailId);
        return ApiResponse.<OrderDetailFullInfoResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy order detail thành công")
                .data(orderDetail)
                .build();

    }

    @GetMapping("/customer/{customerId}")
    PaginationResponse<List<OrderDetailResponse>> getBuildings(@RequestParam(defaultValue = "1", name = "page") int page,
                                                               @RequestParam(defaultValue = "3", name = "take") int take,
                                                               @RequestParam(defaultValue = "Successfully", name = "status") String status,
                                                               @PathVariable String customerId
    ) {
        BuildingPaginationDTO dto = new BuildingPaginationDTO(page, take);
        Page<OrderDetailResponse> buildingPage = orderDetailService.getOrdersByCustomerId(customerId, status, dto.page, dto.take);
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

    @GetMapping("/revenue-current-day")
    ApiResponse<Double> getRevenueCurrentDay() {
        return ApiResponse.<Double>builder()
                .message("Doanh thu trong ngày")
                .data(orderDetailService.calculateRevenueCurrentDay())
                .build();
    }

    @GetMapping("/revenue")
    ApiResponse<Double> getRevenue(@RequestParam(required = false) String startTime,
                                   @RequestParam(required = false) String endTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm");
        LocalDateTime start = startTime != null ? LocalDateTime.parse(startTime, formatter) : null;
        LocalDateTime end = endTime != null ? LocalDateTime.parse(endTime, formatter) : null;

        return ApiResponse.<Double>builder()
                .message("Doanh thu")
                .data(orderDetailService.calculateRevenue(start, end))
                .build();
    }

    @GetMapping("/revenue-chart")
    ApiResponse<List<RevenueChartDto>> getRevenueChart(@RequestParam(required = false) String startTime,
                                                       @RequestParam(required = false) String endTime,
                                                       @RequestParam(required = false) String viewWith) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm");
        LocalDateTime start = startTime != null ? LocalDateTime.parse(startTime, formatter) : null;
        LocalDateTime end = endTime != null ? LocalDateTime.parse(endTime, formatter) : null;

        return ApiResponse.<List<RevenueChartDto>>builder()
                .message("Doanh thu theo " + (viewWith != null ? viewWith : "ngày"))
                .data(orderDetailService.calculateRevenueChart(start, end, viewWith))
                .build();
    }

    @GetMapping("/number-order-by-building")
    ApiResponse<List<NumberOrderByBuildingDto>> getNumberOrderByBuilding() {
        return ApiResponse.<List<NumberOrderByBuildingDto>>builder()
                .message("Số đơn hàng theo chi nhánh")
                .data(orderDetailService.getNumberOrderByBuilding())
                .build();
    }

    @PutMapping("/staff")
    ApiResponse<Void> updateStaffWithOrderDetailId(@RequestBody OrderDetailUpdateStaffRequest request) {
        orderDetailService.updateOrderHandlerWithOrderDetail(request.getId(), request.getOrderHandler());
        orderService.updateOrderUpdateAtByOrderDetailId(request.getId());
        return ApiResponse.<Void>builder()
                .message("Update staff successfully")
                .build();
    }
}