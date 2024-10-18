package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.OrderDetailAmenity.OrderDetailAmenityPaginationDTO;
import com.swp.PodBookingSystem.dto.respone.OrderDetailAmenity.OrderDetailAmenityResponse;
import com.swp.PodBookingSystem.dto.respone.PaginationResponse;

import com.swp.PodBookingSystem.entity.OrderDetailAmenity;
import com.swp.PodBookingSystem.service.OrderDetailAmenityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/amenity-orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderDetailAmenityController {
    OrderDetailAmenityService orderDetailAmenityService;

    @GetMapping
    PaginationResponse<List<OrderDetailAmenityResponse>> getOrderDetailAmenities(@RequestParam(defaultValue = "1", name = "page") int page,
                                                                                 @RequestParam(defaultValue = "10", name = "take") int take) {
        OrderDetailAmenityPaginationDTO dto = new OrderDetailAmenityPaginationDTO(page, take);
        Page<OrderDetailAmenityResponse> orderDetailAmenityPage = orderDetailAmenityService.getOrderDetailAmenities(dto.page, dto.take);
        return PaginationResponse.<List<OrderDetailAmenityResponse>>builder()
                .data(orderDetailAmenityPage.getContent())
                .currentPage(orderDetailAmenityPage.getNumber() + 1)
                .totalPage(orderDetailAmenityPage.getTotalPages())
                .recordPerPage(orderDetailAmenityPage.getNumberOfElements())
                .totalRecord((int) orderDetailAmenityPage.getTotalElements())
                .build();
    }
    @PostMapping
    public void createOrderDetailAmenity(@RequestBody OrderDetailAmenity orderDetailAmenity) {
        orderDetailAmenityService.createOrderDetailAmenity(orderDetailAmenity);
    }

    @PutMapping("/{id}")
    public void updateOrderDetailAmenity(@PathVariable String id, @RequestBody OrderDetailAmenity orderDetailAmenity) {
        orderDetailAmenityService.updateOrderDetailAmenity(id, orderDetailAmenity);
    }

    @DeleteMapping("/{orderDetailId}")
    public double deleteOrderDetailAmenity(@PathVariable String orderDetailId) {
        return orderDetailAmenityService.deleteOrderDetailAmenityByOrderDetailId(orderDetailId);
    }

}
