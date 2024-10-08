package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailCreationRequest;
import com.swp.PodBookingSystem.dto.respone.OrderDetailResponse;
import com.swp.PodBookingSystem.dto.respone.OrderResponse;
import com.swp.PodBookingSystem.entity.*;
import com.swp.PodBookingSystem.enums.OrderStatus;
import com.swp.PodBookingSystem.mapper.OrderDetailMapper;
import com.swp.PodBookingSystem.mapper.OrderMapper;
import com.swp.PodBookingSystem.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ServicePackageRepository servicePackageRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);


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



    public OrderDetailResponse createOrderDetail(OrderDetailCreationRequest request, Order order, Room room, OrderStatus status, Account account) {
        try {

            // Step 1: Create a new OrderDetail entity
            OrderDetail response = new OrderDetail();
            response.setCustomer(account);


            response.setId(UUID.randomUUID().toString());

            response.setOrder(order);
            // Set building
            Building building = buildingRepository.findById(request.getBuilding().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Building not found"));
            response.setBuilding(building);

            // Set selected room (assuming only one room is selected here for simplicity)
            if (room != null) {
                response.setRoom(room);
            }

            // Set service package
            ServicePackage servicePackage = servicePackageRepository.findById(request.getServicePackage().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Service package not found"));
            response.setServicePackage(servicePackage);

            // Set order handler
            if (request.getOrderHandler() != null) {
                Account orderHandler = accountRepository.findById(request.getOrderHandler().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Order handler not found"));
                response.setOrderHandler(orderHandler);
            }


            // Set other fields
            response.setPriceRoom(request.getPriceRoom());
            response.setDiscountPercentage(request.getDiscountPercentage());
            response.setStatus(status != null ? status : OrderStatus.Pending);
            response.setStartTime(request.getStartTime());
            response.setEndTime(request.getEndTime());
            response.setCreatedAt(LocalDateTime.now());
            response.setUpdatedAt(LocalDateTime.now());

            // Save the OrderDetail and return response
            return orderDetailMapper.toOrderDetailResponse(orderDetailRepository.save(response));

        } catch (IllegalArgumentException e) {
            // Handle specific validation errors
            log.error("Validation error creating order detail: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            // Handle general exceptions
            log.error("Unexpected error creating order detail", e);
            throw new RuntimeException("Failed to create order detail: " + e.getMessage(), e);
        }
    }



}
