package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.respone.OrderDetailResponse;
import com.swp.PodBookingSystem.entity.OrderDetail;
import com.swp.PodBookingSystem.mapper.OrderDetailMapper;
import com.swp.PodBookingSystem.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class OrderDetailService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    public List<OrderDetailResponse> getAllOrders() {
        List<OrderDetail> orders = orderDetailRepository.findAll();
        return orders.stream()
                .map(orderDetailMapper::toOrderDetailResponse)
                .collect(Collectors.toList());
    }


    public List<OrderDetailResponse> getOrdersByCustomerId(String customerId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByCustomer_Id(customerId);

        System.out.println("Orders found for customer " + customerId + ": " + orderDetails.size());
        for (OrderDetail orderDetail : orderDetails) {
            System.out.println("Order Detail ID: " + orderDetail.getId());
            System.out.println("Created At: " + orderDetail.getCreatedAt());
            System.out.println("Discount Percentage: " + orderDetail.getDiscountPercentage());
            System.out.println("End Time: " + orderDetail.getEndTime());
            System.out.println("Price Room: " + orderDetail.getPriceRoom());
            System.out.println("Start Time: " + orderDetail.getStartTime());
            System.out.println("Status: " + orderDetail.getStatus());
            System.out.println("Updated At: " + orderDetail.getUpdatedAt());
            System.out.println("Building Number: " + orderDetail.getBuilding().getId());
            System.out.println("Customer ID: " + orderDetail.getCustomerId());
            System.out.println("Order ID: " + orderDetail.getOrder().getId());
            System.out.println("Order Handler ID: " + orderDetail.getOrderHandler().getId());
            System.out.println("Room ID: " + orderDetail.getRoom().getId());
            System.out.println("Service Package ID: " + orderDetail.getServicePackage().getId());
            System.out.println("-------------------------------"); // Separator for better readability
        }

        return orderDetails.stream()
                .map(orderDetailMapper::toOrderDetailResponse) // Use the mapper for conversion
                .collect(Collectors.toList());
    }

}