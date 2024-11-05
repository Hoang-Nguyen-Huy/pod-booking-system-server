package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.Order.OrderUpdateRequest;
import com.swp.PodBookingSystem.dto.request.Order.OrderUpdateStaffRequest;
import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailCreationRequest;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.respone.Order.OrderManagementResponse;
import com.swp.PodBookingSystem.dto.respone.OrderResponse;
import com.swp.PodBookingSystem.dto.respone.PaginationResponse;
import com.swp.PodBookingSystem.entity.*;
import com.swp.PodBookingSystem.enums.OrderStatus;
import com.swp.PodBookingSystem.service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderController {
    OrderService orderService;
    OrderDetailService orderDetailService;
    AccountService accountService;

    @GetMapping
    public ApiResponse<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        logOrders(orders);
        return ApiResponse.<List<OrderResponse>>builder()
                .data(orders)
                .build();
    }

    @GetMapping("/order-info/{orderId}")
    public ApiResponse<OrderManagementResponse> getInfoOrder(@PathVariable String orderId) {
        return ApiResponse.<OrderManagementResponse>builder()
                .message("Lấy thông tin đơn hàng thành công")
                .data(orderService.getInfoOrder(orderId))
                .build();
    }


    @GetMapping("/page")
    public PaginationResponse<List<OrderManagementResponse>> getOrdersByRole(
            @RequestHeader("Authorization") String token,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status) {
        String accountId = accountService.extractAccountIdFromToken(token);
        Account user = accountService.getAccountById(accountId);
        LocalDateTime startDateTime = orderService.parseDateTime(startDate);
        LocalDateTime endDateTime = orderService.parseDateTime(endDate);

        return orderService.getOrdersByRole(page, size, startDateTime, endDateTime, user, status);
    }

    @GetMapping("/search")
    public PaginationResponse<List<OrderManagementResponse>> searchOrders(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "10") int size) {
        return orderService.searchOrdersByKeyword(page, size, keyword);
    }

    @GetMapping("/{accountId}")
    public PaginationResponse<List<OrderManagementResponse>> getOrdersByAccountId(@PathVariable String accountId, @RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "5") int take, @RequestParam(defaultValue = "Successfully") String status) {
        return orderService.getOrdersByAccountCustomerId(page, take, accountId, status);
    }

    //Check room available -> yes: create order Status: Successfully
    //                     -> no: create order Status: Pending
    @PostMapping
    public ApiResponse<String> createOrderByRequest(
            @RequestBody OrderDetailCreationRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            String accountId = accountService.extractAccountIdFromToken(token);
            Account account = accountService.getAccountById(accountId);
            Order orderCreated = orderService.createOrderByRequest(account, request);
            boolean isSomeRoomWasBook = orderDetailService.processOrderDetails(request, orderCreated, account);

            String status = isSomeRoomWasBook ? "Pending" : "Successfully";
            String message = isSomeRoomWasBook ?
                    "Order and order details created successfully but some rooms were booked" :
                    "Order and order details created successfully";
            return ApiResponse.<String>builder()
                    .data(status)
                    .message(message)
                    .code(HttpStatus.CREATED.value())
                    .build();
        } catch (Exception e) {
            log.error("Error creating order: ", e);
            return ApiResponse.<String>builder()
                    .message("Failed to create order: " + e.getMessage())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }

    @PutMapping
    ApiResponse<OrderResponse> updateOrder(@RequestBody OrderUpdateRequest request) {
        orderService.updateOrderUpdateAt(request.getId());
        return ApiResponse.<OrderResponse>builder()
                .data(orderService.updateOrder(request))
                .message("Cập nhật hóa đơn thành công")
                .build();
    }

    @PutMapping("/staff")
    ApiResponse<OrderResponse> updateStaffWithOrder(@RequestBody OrderUpdateStaffRequest request) {
        return ApiResponse.<OrderResponse>builder()
                .data(orderService.updateOrderHandlerWithOrder(request.getId(), request))
                .message("Update order successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable String id) {
        String deletedOrder = orderService.deleteOrder(id);
        return ResponseEntity.status(HttpStatus.OK).body(deletedOrder);
    }

    private void logOrders(List<OrderResponse> orders) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        log.info("Number of orders retrieved: {}", orders.size());
        orders.forEach(order -> log.info("Order ID: {}, Account ID: {}", order.getId(), order.getAccountId()));
    }

    @GetMapping("/number-order-current-day")
    ApiResponse<Integer> countCurrentlyOrder() {
        return ApiResponse.<Integer>builder()
                .message("Số đơn hàng trong ngày")
                .data(orderService.countCurrentlyOrder())
                .build();
    }

    @GetMapping("/number-order")
    ApiResponse<Integer> countOrder(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm");
        LocalDateTime start = startTime != null ? LocalDateTime.parse(startTime, formatter) : null;
        LocalDateTime end = endTime != null ? LocalDateTime.parse(endTime, formatter) : null;

        return ApiResponse.<Integer>builder()
                .message("Số đơn hàng từ " + start + " đến " + end)
                .data(orderService.countOrder(start, end))
                .build();
    }
}