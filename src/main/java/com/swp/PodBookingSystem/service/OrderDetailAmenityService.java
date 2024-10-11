package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.entity.OrderDetailAmenity;
import com.swp.PodBookingSystem.repository.OrderDetailAmenityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailAmenityService {
    @Autowired
    private OrderDetailAmenityRepository orderDetailAmenityRepository;

    public List<OrderDetailAmenity> getOrderDetailAmenitiesByOrderDetailId(String orderDetailId) {
        return orderDetailAmenityRepository.findByOrderDetailId(orderDetailId);
    }
}
