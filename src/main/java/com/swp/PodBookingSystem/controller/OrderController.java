package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.Order.OrderCreationRequest;
import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailCreationRequest;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetailResponse;
import com.swp.PodBookingSystem.dto.respone.OrderResponse;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.entity.Order;
import com.swp.PodBookingSystem.entity.Room;
import com.swp.PodBookingSystem.enums.OrderStatus;
import com.swp.PodBookingSystem.exception.AppException;
import com.swp.PodBookingSystem.exception.ErrorCode;
import com.swp.PodBookingSystem.repository.AccountRepository;
import com.swp.PodBookingSystem.service.AccountService;
import com.swp.PodBookingSystem.service.OrderDetailService;
import com.swp.PodBookingSystem.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
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


    @Autowired
            private AccountService accountService;
    JwtDecoder jwtDecoder;

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
    public ApiResponse<List<OrderDetailResponse>> createOrderByRequest(@RequestBody OrderDetailCreationRequest request, @RequestHeader("Authorization") String token) {
        try {
            //Check còn phòng kh
            //Còn phòng auto succes
            //Trống thì trả pending
            //Trả về FE status order success hay đang pending để hiện

            if (token == null || !token.startsWith("Bearer ")) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }

            token = token.substring(7);

            Jwt jwt = jwtDecoder.decode(token);
            String accountId = jwt.getClaimAsString("accountId");

            Account account = accountService.getAccountById(accountId);

                // Step 2: Iterate over each room and status to create OrderDetails
                List<Room> selectedRooms = request.getSelectedRooms();


                List<OrderDetailResponse> orderDetails = new ArrayList<>();
                Order orderCreated = new Order();// Step 2: Parse start and end times// Step 2: Parse start and end times

                String startTime = request.getStartTime().toString();
                String endTime = request.getEndTime().toString();

                int servicePackageId = request.getServicePackage().getId();

                switch (servicePackageId){

                    // 4 week, same day in week
                    case 1:
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                        LocalDateTime originalStartTime = LocalDateTime.parse(startTime, formatter);
                        LocalDateTime originalEndTime = LocalDateTime.parse(endTime, formatter);

                        // Loop through the 4 weeks
                        for (int week = 0; week < 4; week++) {
                            // Add 'week' weeks to the original start and end time to get new dates
                            LocalDateTime newStartTime = originalStartTime.plusWeeks(week);
                            LocalDateTime newEndTime = originalEndTime.plusWeeks(week);


                            for (int i = 0; i < selectedRooms.size(); i++) {
                                OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
                                orderCreated = orderService.createOrderByRequest(request, account);

                                Room room = selectedRooms.get(i);

                                // Create order detail for each week
                                orderDetailResponse = orderDetailService.createOrderDetail(
                                        request, orderCreated, room, OrderStatus.Successfully, account, newStartTime, newEndTime);
                                orderDetails.add(orderDetailResponse);
                            }
                        }
                        break;

                        //30 day
                    case 2:
                        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                        originalStartTime = LocalDateTime.parse(startTime, formatter);
                        originalEndTime = LocalDateTime.parse(endTime, formatter);

                        // Loop for 30 days
                        for (int day = 0; day < 30; day++) {
                            // Increment the start time by the current day
                            LocalDateTime newStartTime = originalStartTime.plusDays(day);
                            LocalDateTime newEndTime = originalEndTime.plusDays(day);

                            for (Room room : selectedRooms) {
                                orderCreated = orderService.createOrderByRequest(request, account);
                                OrderDetailResponse orderDetailResponse = orderDetailService.createOrderDetail(
                                        request, orderCreated, room, OrderStatus.Successfully, account, newStartTime, newEndTime);
                                orderDetails.add(orderDetailResponse);
                            }
                        }
                        break;

                        //standard
                    case 3:
                        for (int i = 0; i < selectedRooms.size(); i++) {
                            OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
                            orderCreated = orderService.createOrderByRequest(request, account);

                            Room room = selectedRooms.get(i);

                            orderDetailResponse = orderDetailService.createOrderDetail(request, orderCreated, room, OrderStatus.Successfully , account, request.getStartTime(), request.getEndTime());
                            orderDetails.add(orderDetailResponse);
                        }
                        break;

                    default:
                        throw new AppException(ErrorCode.INVALID_KEY);
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
