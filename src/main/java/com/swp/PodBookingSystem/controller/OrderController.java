package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailCreationRequest;
import com.swp.PodBookingSystem.dto.request.Room.RoomWithAmenitiesDTO;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetail.OrderDetailResponse;
import com.swp.PodBookingSystem.dto.respone.OrderResponse;
import com.swp.PodBookingSystem.entity.*;
import com.swp.PodBookingSystem.enums.OrderStatus;
import com.swp.PodBookingSystem.exception.AppException;
import com.swp.PodBookingSystem.exception.ErrorCode;
import com.swp.PodBookingSystem.service.*;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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



    @Autowired
    private OrderDetailAmenityService orderDetailAmenityService;

    JwtDecoder jwtDecoder;

    @GetMapping
    public ApiResponse<List<OrderResponse>> getAllOrders() {
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
            List<RoomWithAmenitiesDTO> selectedRoomsWithAmenities = request.getSelectedRooms();

            List<OrderDetail> orderDetails = new ArrayList<>();
            Order orderCreated = orderService.createOrderByRequest(request, account);
            String startTime = request.getStartTime().toString();
            String endTime = request.getEndTime().toString();
            int servicePackageId = request.getServicePackage().getId();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime originalStartTime = LocalDateTime.parse(startTime, formatter);
            LocalDateTime originalEndTime = LocalDateTime.parse(endTime, formatter);
            boolean isSomeRoomWasBook = false;

            switch (servicePackageId) {
                // 4 week, same day in week
                case 1:
                    for (int week = 0; week < 4; week++) {
                        LocalDateTime newStartTime = originalStartTime.plusWeeks(week);
                        LocalDateTime newEndTime = originalEndTime.plusWeeks(week);
                        for (int i = 0; i < selectedRoomsWithAmenities.size(); i++) {
                            OrderDetail orderDetail;
                            RoomWithAmenitiesDTO roomWithAmenitiesDTO = selectedRoomsWithAmenities.get(i);
                            Room room = roomService.getRoomByIdV2(roomWithAmenitiesDTO.getId());
                            boolean isAvailable = roomService.isRoomAvailable(room.getId(), newStartTime, newEndTime);
                            if (isAvailable) {
                                orderDetail = orderDetailService.createOrderDetail(
                                        request, orderCreated, room, OrderStatus.Successfully, account, newStartTime, newEndTime);
                            } else {
                                isSomeRoomWasBook = true;
                                orderDetail = orderDetailService.createOrderDetail(
                                        request, orderCreated, room, OrderStatus.Pending, account, newStartTime, newEndTime);
                            }
                            List<Amenity> amenities = roomWithAmenitiesDTO.getAmenities();
                            for(Amenity amenity : amenities){
                                OrderDetailAmenity orderDetailAmenity = new OrderDetailAmenity();

                                // Set the properties using setters
                                orderDetailAmenity.setId(UUID.randomUUID().toString());
                                orderDetailAmenity.setQuantity(amenity.getQuantity());
                                orderDetailAmenity.setPrice(amenity.getPrice() * amenity.getQuantity());
                                orderDetailAmenity.setOrderDetail(orderDetail);
                                orderDetailAmenity.setAmenity(amenity);
                                orderDetailAmenityService.createOrderDetailAmenity(orderDetailAmenity);
                            }
                            orderDetails.add(orderDetail);
                        }
                    }
                    break;

                //30 day
                case 2:
                    for (int day = 0; day < 30; day++) {
                        LocalDateTime newStartTime = originalStartTime.plusDays(day);
                        LocalDateTime newEndTime = originalEndTime.plusDays(day);
                        for (RoomWithAmenitiesDTO roomWithAmenitiesDTO : selectedRoomsWithAmenities) {
                            OrderDetail orderDetail;
                            Room room = roomService.getRoomByIdV2(roomWithAmenitiesDTO.getId());
                            boolean isAvailable = roomService.isRoomAvailable(room.getId(), newStartTime, newEndTime);

                            if (isAvailable) {
                                orderDetail = orderDetailService.createOrderDetail(
                                        request, orderCreated, room, OrderStatus.Successfully, account, newStartTime, newEndTime);
                            } else {
                                isSomeRoomWasBook = true;
                                orderDetail = orderDetailService.createOrderDetail(
                                        request, orderCreated, room, OrderStatus.Pending, account, newStartTime, newEndTime);
                            }
                            List<Amenity> amenities = roomWithAmenitiesDTO.getAmenities();
                            for(Amenity amenity : amenities){
                                OrderDetailAmenity orderDetailAmenity = new OrderDetailAmenity();

                                // Set the properties using setters
                                orderDetailAmenity.setId(UUID.randomUUID().toString());
                                orderDetailAmenity.setQuantity(amenity.getQuantity());
                                orderDetailAmenity.setPrice(amenity.getPrice() * amenity.getQuantity());
                                orderDetailAmenity.setOrderDetail(orderDetail);
                                orderDetailAmenity.setAmenity(amenity);
                                orderDetailAmenityService.createOrderDetailAmenity(orderDetailAmenity);
                            }
                            orderDetails.add(orderDetail);
                        }
                    }
                    break;

                //standard
                case 3:
                    for (int i = 0; i < selectedRoomsWithAmenities.size(); i++) {
                        OrderDetail orderDetail;
                        RoomWithAmenitiesDTO roomWithAmenitiesDTO = selectedRoomsWithAmenities.get(i);
                        Room room = roomService.getRoomByIdV2(roomWithAmenitiesDTO.getId());
                        boolean isAvailable = roomService.isRoomAvailable(room.getId(), request.getStartTime(), request.getEndTime());
                        if (isAvailable) {
                            orderDetail = orderDetailService.createOrderDetail(
                                    request, orderCreated, room, OrderStatus.Successfully, account, request.getStartTime(), request.getEndTime());
                        } else {
                            isSomeRoomWasBook = true;
                            orderDetail = orderDetailService.createOrderDetail(
                                    request, orderCreated, room, OrderStatus.Pending, account, request.getStartTime(), request.getEndTime());
                        }
                        List<Amenity> amenities = roomWithAmenitiesDTO.getAmenities();
                        for (Amenity amenity : amenities) {

                            OrderDetailAmenity orderDetailAmenity = new OrderDetailAmenity();

                            // Set the properties using setters
                            orderDetailAmenity.setId(UUID.randomUUID().toString());
                            orderDetailAmenity.setQuantity(amenity.getQuantity());
                            orderDetailAmenity.setPrice(amenity.getPrice() * amenity.getQuantity());
                            orderDetailAmenity.setOrderDetail(orderDetail);
                            orderDetailAmenity.setAmenity(amenity);

                            orderDetailAmenityService.createOrderDetailAmenity(orderDetailAmenity);
                        }
                        orderDetails.add(orderDetail);
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
                        .message("Order and order details created successfully")
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
}