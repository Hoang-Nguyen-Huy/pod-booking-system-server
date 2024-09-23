package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.respone.OrderResponse;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.entity.OrderDetail;
import com.swp.PodBookingSystem.repository.AccountRepository;
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
    private AccountRepository accountRepository;

    public List<OrderResponse> getAllOrders() {
        List<OrderDetail> orders = orderDetailRepository.findAll();
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderDetailById(String id) {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("OrderDetail not found"));
        return convertToResponse(orderDetail);
    }

    public List<OrderResponse> getOrdersByCustomerId(String customerId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByCustomer_Id(customerId);

        System.out.println("Orders found for customer " + customerId + ": " + orderDetails.size());

        return orderDetails.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse convertToResponse(OrderDetail orderDetail) {
        OrderResponse response = new OrderResponse();
        response.setId(orderDetail.getId());
        response.setCustomerId(orderDetail.getCustomer().getId());
        response.setBuildingId(orderDetail.getBuilding().getId());
        response.setRoomId(orderDetail.getRoom().getId());
        response.setOrderId(orderDetail.getOrder().getId());
        response.setServicePackageId(orderDetail.getServicePackage().getId());
        response.setOrderHandledId(orderDetail.getOrderHandler().getId());
        response.setPriceRoom(orderDetail.getPriceRoom());
        response.setStatus(orderDetail.getStatus());
        response.setStartTime(orderDetail.getStartTime());
        response.setEndTime(orderDetail.getEndTime());
        response.setCreatedAt(orderDetail.getCreatedAt());
        return response;
    }
}
