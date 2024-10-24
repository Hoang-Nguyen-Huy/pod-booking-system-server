package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.Order.OrderUpdateRequest;
import com.swp.PodBookingSystem.dto.request.Order.OrderUpdateStaffRequest;
import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailCreationRequest;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.respone.Order.OrderManagementResponse;
import com.swp.PodBookingSystem.dto.respone.OrderResponse;
import com.swp.PodBookingSystem.dto.respone.PaginationResponse;
import com.swp.PodBookingSystem.entity.*;
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

    @GetMapping("/page")
    public ApiResponse<PaginationResponse<List<OrderManagementResponse>>> getOrdersByRole(
            @RequestHeader("Authorization") String token,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String accountId = accountService.extractAccountIdFromToken(token);
        Account user = accountService.getAccountById(accountId);
        LocalDateTime startDateTime = orderService.parseDateTime(startDate);
        LocalDateTime endDateTime = orderService.parseDateTime(endDate);

        return ApiResponse.<PaginationResponse<List<OrderManagementResponse>>>builder()
                .data(orderService.getOrdersByRole(page, size, startDateTime, endDateTime, user))
                .message("get paging order successfully")
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<PaginationResponse<List<OrderManagementResponse>>> searchOrders(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PaginationResponse<List<OrderManagementResponse>>>builder()
                .data(orderService.searchOrdersByKeyword(page, size, keyword))
                .message("search order successfully")
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

    //Check room available -> yes: create order Status: Successfully
    //                     -> no: create order Status: Pending
    @PostMapping
    public ApiResponse<String> createOrderByRequest(
            @RequestBody OrderDetailCreationRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            String accountId = accountService.extractAccountIdFromToken(token);
            Account account = accountService.getAccountById(accountId);
            Order orderCreated = orderService.createOrderByRequest(account);
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
    ApiResponse<OrderResponse> updateOrder(@RequestBody OrderUpdateRequest request){
        orderService.updateOrderUpdateAt(request.getId());
        return ApiResponse.<OrderResponse>builder()
                .data(orderService.updateOrder(request))
                .message("Update order successfully")
                .build();
    }

    @PutMapping("/staff")
    ApiResponse<OrderResponse> updateStaffWithOrder(@RequestBody OrderUpdateStaffRequest request){
        return ApiResponse.<OrderResponse> builder()
                .data(orderService.updateOrderHandlerWithOrder(request.getId(),request))
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
}