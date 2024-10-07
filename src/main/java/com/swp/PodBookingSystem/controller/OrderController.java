package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.Order.OrderCreationRequest;
import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailCreationRequest;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetailResponse;
import com.swp.PodBookingSystem.dto.respone.OrderResponse;
import com.swp.PodBookingSystem.entity.Order;
import com.swp.PodBookingSystem.entity.Room;
import com.swp.PodBookingSystem.enums.OrderStatus;
import com.swp.PodBookingSystem.service.OrderDetailService;
import com.swp.PodBookingSystem.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

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

    @PostMapping
    public ApiResponse<List<OrderDetailResponse>> createOrderByRequest(@RequestBody OrderDetailCreationRequest request) {
        try {
                // Step 2: Iterate over each room and status to create OrderDetails
                List<Room> selectedRooms = request.getSelectedRooms();
                List<OrderStatus> statuses = request.getStatus();

                if (selectedRooms == null || selectedRooms.isEmpty() || statuses == null || statuses.isEmpty()) {
                    return ApiResponse.<List<OrderDetailResponse>>builder()
                            .message("Selected rooms or statuses cannot be empty")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .build();
                }

                if (selectedRooms.size() != statuses.size()) {
                    return ApiResponse.<List<OrderDetailResponse>>builder()
                            .message("The number of selected rooms must be equal to the number of statuses")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .build();
                }

                List<OrderDetailResponse> orderDetails = new ArrayList<>();
                Order orderCreated = new Order();// Step 2: Parse start and end times// Step 2: Parse start and end times

            // Determine booking schedule based on service package ID
            int servicePackageId = request.getServicePackage().getId();
                // Loop through selected rooms and statuses to create order details
                for (int i = 0; i < selectedRooms.size(); i++) {
                    OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
                    orderCreated = orderService.createOrderByRequest(request);

                    Room room = selectedRooms.get(i);
                    OrderStatus status = statuses.get(i);

                    orderDetailResponse = orderDetailService.createOrderDetail(request, orderCreated, room, status);
                    orderDetails.add(orderDetailResponse);
                }

                // Return response after successfully creating order details
                return ApiResponse.<List<OrderDetailResponse>>builder()
                        .data(orderDetails)
                        .message("Order and order details created successfully")
                        .code(HttpStatus.CREATED.value())
                        .build();

        } catch (Exception e) {
            log.error("Error creating order: ", e);
            return ApiResponse.<List<OrderDetailResponse>>builder()
                    .message("Failed to create order: " + e.getMessage())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }



    private void logOrders(List<OrderResponse> orders) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        log.info("Number of orders retrieved: {}", orders.size());
        orders.forEach(order -> log.info("Order ID: {}, Account ID: {}", order.getId(), order.getAccountId()));
    }
}
