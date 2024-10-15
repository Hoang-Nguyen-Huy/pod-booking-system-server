package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.respone.Amenity.AmenityManagementResponse;
import com.swp.PodBookingSystem.entity.OrderDetailAmenity;
import com.swp.PodBookingSystem.repository.OrderDetailAmenityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderDetailAmenityService {
    @Autowired
    private OrderDetailAmenityRepository orderDetailAmenityRepository;

//    public List<OrderDetailAmenity> getOrderDetailAmenitiesByOrderDetailId(String orderDetailId) {
//        return orderDetailAmenityRepository.findByOrderDetailId(orderDetailId);
//    }

    public List<OrderDetailAmenity> getAll() {
        return orderDetailAmenityRepository.findAll();
    }

    public List<AmenityManagementResponse> getOrderDetailAmenitiesByOrderDetailId(String orderDetailId) {
        return orderDetailAmenityRepository.findByOrderDetailId(orderDetailId).stream().map(amenity -> {
            return AmenityManagementResponse.builder()
                    .id(amenity.getAmenity().getId())
                    .name(amenity.getAmenity().getName())
                    .price(amenity.getPrice())
                    .quantity(amenity.getQuantity())
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public void deleteOrderDetailAmenityByOrderDetailId(String orderDetailId) {
        orderDetailAmenityRepository.deleteByOrderDetailId(orderDetailId);
    }
}
