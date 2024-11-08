package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.OrderDetailAmenity.OrderDetailAmenityRequest;
import com.swp.PodBookingSystem.dto.request.OrderDetailAmenity.OrderDetailAmenityUpdateReq;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.request.OrderDetailAmenity.OrderDetailAmenityCreationRequest;
import com.swp.PodBookingSystem.dto.respone.OrderDetail.OrderDetailAmenityListResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetailAmenity.OrderDetailAmenityResponse;
import com.swp.PodBookingSystem.dto.respone.PaginationResponse;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.service.AccountService;
import com.swp.PodBookingSystem.service.OrderDetailAmenityService;
import com.swp.PodBookingSystem.service.OrderDetailService;
import com.swp.PodBookingSystem.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RestController
@RequestMapping("/order-detail-amenity")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderDetailAmenityController {
    OrderDetailAmenityService orderDetailAmenityService;
    OrderDetailService orderDetailService;
    private final AccountService accountService;
    private final OrderService orderService;
    private static final Logger log = LoggerFactory.getLogger(OrderDetailAmenityController.class);

    @GetMapping("/page")
    public PaginationResponse<List<OrderDetailAmenityListResponse>> getOrderDetailAndAmenity(
            @RequestHeader("Authorization") String token,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int take) {
        String accountId = accountService.extractAccountIdFromToken(token);
        Account user = accountService.getAccountById(accountId);
        LocalDateTime startDateTime = orderService.parseDateTime(startDate);
        LocalDateTime endDateTime = orderService.parseDateTime(endDate);
        return orderDetailService.getPagedOrderDetails(user, startDateTime, endDateTime, page, take);
    }

    @PostMapping
    public ApiResponse<OrderDetailAmenityResponse> createOrderDetailAmenity(@RequestBody OrderDetailAmenityCreationRequest request) {
        return ApiResponse.<OrderDetailAmenityResponse>builder()
                .data(orderDetailAmenityService.createOrderDetailAmenityApi(request))
                .message("Tạo đơn hàng tiện ích thành công")
                .build();
    }

    @PostMapping("/create")
    public ApiResponse<String> createOrderDetailAmenity (@RequestBody OrderDetailAmenityRequest orderDetailAmenityRequest) {
        try{
            orderDetailAmenityService.createOrderDetailAmenity(orderDetailAmenityRequest);
            return ApiResponse.<String>builder()
                    .message("Create order detail amenity successfully")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .message("Failed to create order detail amenity: " + e.getMessage())
                    .build();
        }
    }

    @PutMapping
    public ApiResponse<String> updateOrderDetailAmenity(@RequestBody OrderDetailAmenityUpdateReq request) {
        try {
            orderDetailAmenityService.updateOrderDetailAmenityStatus(request);
            return ApiResponse.<String>builder()
                    .message("Update order detail amenity successfully")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .message("Failed to update order detail amenity: " + e.getMessage())
                    .build();
        }
    }


    @GetMapping("/search")
    public PaginationResponse<List<OrderDetailAmenityListResponse>> searchOrders(
                                                                                @RequestHeader("Authorization") String token,
                                                                                @RequestParam String searchParams,
                                                                                @RequestParam String startDate,
                                                                                @RequestParam String endDate,
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int take) {


        Account user = accountService.getAccountById(accountService.extractAccountIdFromToken(token));

        LocalDateTime startDateTime = orderService.parseDateTime(startDate);
        LocalDateTime endDateTime = orderService.parseDateTime(endDate);
        return orderDetailAmenityService.searchOrderDetailAmenityByKeyword(page, take, searchParams, user, startDateTime, endDateTime);
    }

}
