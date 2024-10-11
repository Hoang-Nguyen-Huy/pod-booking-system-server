package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.service.OrderDetailAmenityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailAmenityController {
    @Autowired
    private OrderDetailAmenityService orderDetailAmenityService;
}
