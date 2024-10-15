package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailCreationRequest;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.respone.Order.OrderManagementResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetail.OrderDetailResponse;
import com.swp.PodBookingSystem.dto.respone.OrderResponse;
import com.swp.PodBookingSystem.dto.respone.Page.CustomPage;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.entity.Order;
import com.swp.PodBookingSystem.entity.Room;
import com.swp.PodBookingSystem.enums.OrderStatus;
import com.swp.PodBookingSystem.exception.AppException;
import com.swp.PodBookingSystem.exception.ErrorCode;
import com.swp.PodBookingSystem.service.AccountService;
import com.swp.PodBookingSystem.service.OrderDetailService;
import com.swp.PodBookingSystem.service.OrderService;
import com.swp.PodBookingSystem.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private RoomService roomService;

    JwtDecoder jwtDecoder;

    @GetMapping
    public ApiResponse<List<OrderResponse>> getAllOrders(){
        List<OrderResponse> orders = orderService.getAllOrders();
        logOrders(orders);
        return ApiResponse.<List<OrderResponse>>builder()
                .data(orders)
                .build();
    }

    @GetMapping("/page")
    public ResponseEntity<CustomPage<OrderManagementResponse>> getOrdersByRole(
            @RequestHeader("Authorization") String token,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        token = token.substring(7);
        Jwt jwt = jwtDecoder.decode(token);
        String accountId = jwt.getClaimAsString("accountId");
        Account user = accountService.getAccountById(accountId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getOrdersByRole(page, size, startDateTime, endDateTime, user));
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
    public ApiResponse<String> createOrderByRequest(@RequestBody OrderDetailCreationRequest request, @RequestHeader("Authorization") String token) {
        try {
            //Check còn phòng kh
            //Còn phòng auto succes
            //Trống thì trả pending
            //Trả về FE status order success hay đang pending để hiện
            //Trừ quanlity các amenities, reset sau khi qua ngày



            if (token == null || !token.startsWith("Bearer ")) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }

            token = token.substring(7);
            Jwt jwt = jwtDecoder.decode(token);
            String accountId = jwt.getClaimAsString("accountId");
            Account account = accountService.getAccountById(accountId);
            List<Room> selectedRooms = request.getSelectedRooms();

            List<OrderDetailResponse> orderDetails = new ArrayList<>();
            Order orderCreated = orderService.createOrderByRequest(request, account);
            String startTime = request.getStartTime().toString();
            String endTime = request.getEndTime().toString();
            int servicePackageId = request.getServicePackage().getId();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime originalStartTime = LocalDateTime.parse(startTime, formatter);
            LocalDateTime originalEndTime = LocalDateTime.parse(endTime, formatter);
            boolean isSomeRoomWasBook = false;

                switch (servicePackageId){
                    // 4 week, same day in week
                    case 1:
                        for (int week = 0; week < 4; week++) {
                            LocalDateTime newStartTime = originalStartTime.plusWeeks(week);
                            LocalDateTime newEndTime = originalEndTime.plusWeeks(week);
                            for (int i = 0; i < selectedRooms.size(); i++) {
                                OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
                                Room room = selectedRooms.get(i);
                                boolean isAvailable = roomService.isRoomAvailable(room.getId(), newStartTime, newEndTime);
                                if (isAvailable){
                                    orderDetailResponse = orderDetailService.createOrderDetail(
                                            request, orderCreated, room, OrderStatus.Successfully, account, newStartTime, newEndTime);
                                } else {
                                    isSomeRoomWasBook = true;
                                    orderDetailResponse = orderDetailService.createOrderDetail(
                                            request, orderCreated, room, OrderStatus.Pending, account, newStartTime, newEndTime);
                                }
                                orderDetails.add(orderDetailResponse);
                            }
                        }
                        break;

                        //30 day
                    case 2:
                        for (int day = 0; day < 30; day++) {
                            LocalDateTime newStartTime = originalStartTime.plusDays(day);
                            LocalDateTime newEndTime = originalEndTime.plusDays(day);
                            for (Room room : selectedRooms) {
                                OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
                                boolean isAvailable = roomService.isRoomAvailable(room.getId(), newStartTime, newEndTime);
                                if (isAvailable){
                                    orderDetailResponse = orderDetailService.createOrderDetail(
                                            request, orderCreated, room, OrderStatus.Successfully, account, newStartTime, newEndTime);
                                } else {
                                    isSomeRoomWasBook = true;
                                    orderDetailResponse = orderDetailService.createOrderDetail(
                                            request, orderCreated, room, OrderStatus.Pending, account, newStartTime, newEndTime);
                                }
                                orderDetails.add(orderDetailResponse);
                            }
                        }
                        break;

                        //standard
                    case 3:
                        for (int i = 0; i < selectedRooms.size(); i++) {
                            OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
                            Room room = selectedRooms.get(i);
                            boolean isAvailable = roomService.isRoomAvailable(room.getId(), request.getStartTime(), request.getEndTime());
                            if (isAvailable){
                                orderDetailResponse = orderDetailService.createOrderDetail(
                                        request, orderCreated, room, OrderStatus.Successfully, account, request.getStartTime(), request.getEndTime());
                            } else {
                                isSomeRoomWasBook = true;
                                orderDetailResponse = orderDetailService.createOrderDetail(
                                        request, orderCreated, room, OrderStatus.Pending, account, request.getStartTime(), request.getEndTime());
                            }
                            orderDetails.add(orderDetailResponse);
                        }
                        break;

                    default:
                        throw new AppException(ErrorCode.INVALID_KEY);
                }

                if (isSomeRoomWasBook) {
                    String status = "Pending";
                    return ApiResponse.<String>builder()
                            .data(status)
                            .message("Order and order details created successfully but some room was book")
                            .code(HttpStatus.CREATED.value())
                            .build();
                } else {
                    String status = "Successfully";
                    return ApiResponse.<String>builder()
                            .data(status)
                            .message("Order and order details created successfully  ")
                            .code(HttpStatus.CREATED.value())
                            .build();
                }


        } catch (Exception e) {
            log.error("Error creating order: ", e);
            return ApiResponse.<String>builder()
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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable String id) {
        String deletedOrder = orderService.deleteOrder(id);
        return ResponseEntity.status(HttpStatus.OK).body(deletedOrder);
    }

    @GetMapping("/search")
    public ResponseEntity<CustomPage<OrderManagementResponse>> searchOrders(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size) {
        CustomPage<OrderManagementResponse> list = orderService.searchOrdersByKeyword(page, size, keyword);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }
}
