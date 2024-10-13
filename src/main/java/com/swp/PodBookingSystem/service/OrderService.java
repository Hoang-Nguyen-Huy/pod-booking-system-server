package com.swp.PodBookingSystem.service;
import com.swp.PodBookingSystem.dto.request.Order.OrderUpdateStaffRequest;
import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailCreationRequest;
import com.swp.PodBookingSystem.dto.respone.Amenity.AmenityManagementResponse;
import com.swp.PodBookingSystem.dto.respone.Order.OrderManagementResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetail.OrderDetailManagementResponse;
import com.swp.PodBookingSystem.dto.respone.OrderResponse;
import com.swp.PodBookingSystem.dto.respone.Page.CustomPage;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.entity.Order;
import com.swp.PodBookingSystem.entity.OrderDetail;
import com.swp.PodBookingSystem.enums.AccountRole;
import com.swp.PodBookingSystem.mapper.OrderMapper;
import com.swp.PodBookingSystem.repository.AccountRepository;
import com.swp.PodBookingSystem.repository.OrderDetailAmenityRepository;
import com.swp.PodBookingSystem.repository.OrderDetailRepository;
import com.swp.PodBookingSystem.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderDetailService orderDetailService;

    @Autowired
    public OrderService(@Lazy OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @Autowired
    private OrderRepository orderRepository;

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OrderDetailAmenityService orderDetailAmenityService;

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersByAccountId(String accountId) {
        List<Order> orders = orderRepository.findByAccountId(accountId);

        System.out.println("Orders found for account " + accountId + ": " + orders.size());

        for (Order order : orders) {
            System.out.println("Order ID    : " + order.getId());
            System.out.println("Created At: " + order.getCreatedAt());
            System.out.println("Updated At: " + order.getUpdatedAt());
            System.out.println("Account Id: " + order.getAccount().getId());
            System.out.println();
            System.out.println("-------------------------------");
        }

        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    public Order createOrderByRequest(OrderDetailCreationRequest request, Account account) {
        try {
            Order order = new Order();
            order.setAccount(account); // Set the account
            order.setId(UUID.randomUUID().toString());
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());

            // Save the order to the repository
            order = orderRepository.save(order);

            return order;
        } catch (Exception e) {
            // Log the error message and handle the exception appropriately
            log.error("Error creating order: ", e);
            throw new RuntimeException("Failed to create order: " + e.getMessage());
        }
    }


    public CustomPage<OrderManagementResponse> getOrdersByRole(int page, int size, LocalDateTime startDate, LocalDateTime endDate, Account user) {
        Page<Order> ordersPage;
        if (user.getRole().equals(AccountRole.Admin)) {
            ordersPage = orderRepository.findAllWithTimeRange(startDate, endDate, PageRequest.of(page, size));
        } else if (user.getRole().equals(AccountRole.Staff) || user.getRole().equals(AccountRole.Manager)) {
            int buildingNumber = user.getBuildingNumber();
            ordersPage = orderRepository.findOrdersByBuildingNumberAndTimeRange(buildingNumber, startDate, endDate, PageRequest.of(page, size));
        } else {
            return null;
        }

        return convertToCustomPage(ordersPage);
    }

    private CustomPage<OrderManagementResponse> convertToCustomPage(Page<Order> ordersPage) {
        List<OrderManagementResponse> orderResponses = ordersPage.getContent().stream().map(order -> {
            List<OrderDetailManagementResponse> orderDetailDTOs = orderDetailService.getOrderDetailById(order.getId());
            return OrderManagementResponse.builder()
                    .id(order.getId())
                    .createdAt(order.getCreatedAt())
                    .updatedAt(order.getUpdatedAt())
                    .orderDetails(orderDetailDTOs)
                    .build();
        }).collect(Collectors.toList());

        return CustomPage.<OrderManagementResponse>builder()
                .data(orderResponses)
                .pageNumber(ordersPage.getNumber())
                .pageSize(ordersPage.getSize())
                .totalElements(ordersPage.getTotalElements())
                .build();
    }

    public void updateOrderByUpdateOrderDetail(String id, LocalDateTime updateAt){
        Order existingOrder = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found with id: " + id));


        existingOrder.setUpdatedAt(updateAt);

        orderRepository.save(existingOrder);
    }

    public OrderResponse updateOrderHandlerWithOrder(String id, OrderUpdateStaffRequest request){
        Order existingOrder = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        if (request.getOrderHandler() == null) {
            throw new RuntimeException("Account handler cannot be null");
        }

        orderDetailService.updateOrderHandlerOrderDetail(existingOrder.getId(), request.getOrderHandler());
        return OrderResponse.builder()
                .id(existingOrder.getId())
                .accountId(existingOrder.getAccount().getId())
                .createdAt(existingOrder.getCreatedAt())
                .updatedAt(existingOrder.getUpdatedAt())
                .build();
    }

    public void updateOrderUpdateAt(String orderId){
        orderRepository.updateOrderUpdatedAt(orderId, LocalDateTime.now());
    }

    @Transactional
    public String deleteOrder(String orderId) {
        orderDetailService.deleteOrderDetailsByOrderId(orderId);
        orderRepository.deleteById(orderId);
        return orderId;
    }

    public CustomPage<OrderManagementResponse> searchOrdersByKeyword(int page, int size, String keyword) {
        Page<Order> ordersPage;
        ordersPage = orderRepository.searchByKeyword(keyword, PageRequest.of(page, size));
        return convertToCustomPage(ordersPage);
    }
}

