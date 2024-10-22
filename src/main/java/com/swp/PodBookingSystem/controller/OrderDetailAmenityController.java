package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.OrderDetailAmenity.OrderDetailAmenityRequest;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.request.OrderDetailAmenity.OrderDetailAmenityCreationRequest;
import com.swp.PodBookingSystem.dto.respone.OrderDetailAmenity.OrderDetailAmenityResponse;
import com.swp.PodBookingSystem.service.OrderDetailAmenityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order-detail-amenity")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderDetailAmenityController {
    OrderDetailAmenityService orderDetailAmenityService;

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

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteOrderDetailAmenity (@PathVariable String id) {
        return ApiResponse.<String>builder()
                .data(orderDetailAmenityService.deleteOrderDetailAmenityById(id))
                .message("Delete order successfully")
                .build();
    }
}
