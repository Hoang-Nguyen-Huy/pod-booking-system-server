package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.OrderDetailAmenity.OrderDetailAmenityRequest;
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
import java.util.List;

@RestController
@RequestMapping("/order-detail-amenity")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderDetailAmenityController {
    OrderDetailAmenityService orderDetailAmenityService;
    OrderDetailService orderDetailService;
    private final AccountService accountService;
    private final OrderService orderService;

    @GetMapping("/page")
    public ApiResponse<PaginationResponse<List<OrderDetailAmenityListResponse>>> getOrderDetailAndAmenity(
            @RequestHeader("Authorization") String token,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String accountId = accountService.extractAccountIdFromToken(token);
        Account user = accountService.getAccountById(accountId);
        LocalDateTime startDateTime = orderService.parseDateTime(startDate);
        LocalDateTime endDateTime = orderService.parseDateTime(endDate);
        return ApiResponse.<PaginationResponse<List<OrderDetailAmenityListResponse>>>builder()
                .data(orderDetailService.getPagedOrderDetails(user, startDateTime, endDateTime, page, size))
                .message("get paging order detail successfully")
                .build();
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

    //Update
}
