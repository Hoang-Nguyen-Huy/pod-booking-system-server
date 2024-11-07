package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.Order.OrderUpdateRequest;
import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailCreationRequest;
import com.swp.PodBookingSystem.dto.respone.Order.OrderManagementResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetail.OrderDetailManagementResponse;
import com.swp.PodBookingSystem.dto.respone.OrderResponse;
import com.swp.PodBookingSystem.dto.respone.PaginationResponse;
import com.swp.PodBookingSystem.entity.*;
import com.swp.PodBookingSystem.enums.AccountRole;
import com.swp.PodBookingSystem.enums.OrderStatus;
import com.swp.PodBookingSystem.exception.AppException;
import com.swp.PodBookingSystem.exception.ErrorCode;
import com.swp.PodBookingSystem.mapper.OrderMapper;
import com.swp.PodBookingSystem.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderDetailService orderDetailService;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, OrderDetailService orderDetailService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderDetailService = orderDetailService;
    }

    //GET:
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderManagementResponse getInfoOrder(String id) {
        OrderManagementResponse order = new OrderManagementResponse();
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        } else {
            order.setId(orderOptional.get().getId());
            order.setCreatedAt(orderOptional.get().getCreatedAt());
            order.setUpdatedAt(orderOptional.get().getUpdatedAt());
        }
        List<OrderDetailManagementResponse> orderDetailDTOs = orderDetailService.getOrderDetailById(order.getId());
        order.setOrderDetails(orderDetailDTOs);
        return order;
    }

    public List<OrderResponse> getOrdersByAccountId(String accountId) {
        List<Order> orders = orderRepository.findByAccountId(accountId);
        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    public PaginationResponse<List<OrderManagementResponse>> getOrdersByAccountCustomerId(int page, int take, String accountId, String status) {
        Page<Order> ordersPage;
        ordersPage = orderRepository.findByAccountCustomerId(accountId, OrderStatus.valueOf(status), PageRequest.of(page, take));
        return convertToPaginationResponse(ordersPage);
    }

    public PaginationResponse<List<OrderManagementResponse>> getOrdersByRole(
            int page, int size, LocalDateTime startDate, LocalDateTime endDate, Account user, OrderStatus status) {
        Page<Order> ordersPage;
        if (user.getRole().equals(AccountRole.Admin)) {
            ordersPage = orderRepository.findAllWithTimeRange(
                    startDate, endDate, status, PageRequest.of(page, size));
        } else if (user.getRole().equals(AccountRole.Manager)) {
            int buildingNumber = user.getBuildingNumber();
            ordersPage = orderRepository.findOrdersByBuildingNumberAndTimeRange(
                    buildingNumber, startDate, endDate, status, PageRequest.of(page, size));
        }else if (user.getRole().equals(AccountRole.Staff)) {
            String staffId = user.getId();
            ordersPage = orderRepository.findOrdersByStaffIdAndTimeRange(
                    staffId, startDate, endDate, status, PageRequest.of(page, size));
        }
        else {
            throw new IllegalArgumentException("User role not authorized to access orders.");
        }
        return convertToPaginationResponse(ordersPage);
    }

    public PaginationResponse<List<OrderManagementResponse>> searchOrdersByKeyword(int page, int size, String keyword) {
        Page<Order> ordersPage;
        ordersPage = orderRepository.searchByKeyword(keyword, PageRequest.of(page, size));
        return convertToPaginationResponse(ordersPage);
    }

    //CREATE:
    public Order createOrderByRequest(Account account, OrderDetailCreationRequest request) {
        try {
            Order order = new Order();
            order.setAccount(account);
            order.setId(renderOrderID(request,account));
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            return order;
        } catch (Exception e) {
            log.error("Error creating order: ", e);
            throw new RuntimeException("Failed to create order: " + e.getMessage());
        }
    }

    //UPDATE:
    public OrderResponse updateOrder(OrderUpdateRequest request) {
        Order existingOrder = orderRepository.findById(request.getId()).orElseThrow(() -> new RuntimeException("Order not found with id: " + request.getId()));
        orderDetailService.updateOrderDetail(request);
        return OrderResponse.builder()
                .id(existingOrder.getId())
                .accountId(existingOrder.getAccount().getId())
                .createdAt(existingOrder.getCreatedAt())
                .updatedAt(existingOrder.getUpdatedAt())
                .build();
    }

    public void updateOrderUpdateAt(String orderId) {
        orderRepository.updateOrderUpdatedAt(orderId, LocalDateTime.now());
    }

    //DELETE:
    @Transactional
    public String deleteOrder(String orderId) {
        orderDetailService.deleteOrderDetailsByOrderId(orderId);
        orderRepository.deleteById(orderId);
        return orderId;
    }

    //UTILS:
    private PaginationResponse<List<OrderManagementResponse>> convertToPaginationResponse(Page<Order> ordersPage) {
        List<OrderManagementResponse> orderResponses = ordersPage.getContent().stream().map(order -> {
            List<OrderDetailManagementResponse> orderDetailDTOs = orderDetailService.getOrderDetailById(order.getId());
            return OrderManagementResponse.builder()
                    .id(order.getId())
                    .createdAt(order.getCreatedAt())
                    .updatedAt(order.getUpdatedAt())
                    .orderDetails(orderDetailDTOs)
                    .build();
        }).collect(Collectors.toList());
        return PaginationResponse.<List<OrderManagementResponse>>builder()
                .message("Lấy danh sách hóa đơn thành công")
                .data(orderResponses)
                .currentPage(ordersPage.getNumber())
                .totalPage(ordersPage.getTotalPages())
                .recordPerPage(ordersPage.getSize())
                .totalRecord((int) ordersPage.getTotalElements())
                .build();
    }

    private PaginationResponse<List<OrderManagementResponse>> convertToPaginationQueryResponse(Page<Order> ordersPage, String status) {
        List<OrderManagementResponse> orderResponses = ordersPage.getContent().stream().map(order -> {
            List<OrderDetailManagementResponse> orderDetailDTOs = orderDetailService.getOrderDetailByOrderId(order.getId(), status);
            return OrderManagementResponse.builder()
                    .id(order.getId())
                    .createdAt(order.getCreatedAt())
                    .updatedAt(order.getUpdatedAt())
                    .orderDetails(orderDetailDTOs)
                    .build();
        }).filter(orderResponse -> !orderResponse.getOrderDetails().isEmpty()).collect(Collectors.toList());
        return PaginationResponse.<List<OrderManagementResponse>>builder()
                .message("Lấy danh sách hóa đơn thành công")
                .data(orderResponses)
                .currentPage(ordersPage.getNumber())
                .totalPage(ordersPage.getTotalPages())
                .recordPerPage(ordersPage.getSize())
                .totalRecord(orderResponses.size())
                .build();

    }

    public LocalDateTime parseDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return LocalDateTime.parse(dateTime, formatter);
    }

    public static String removeDiacritics(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replaceAll("[^\\p{L} ]", "");
    }

    public static String getRoomName(String roomName) {
        String pattern = removeDiacritics(roomName).replace("Room", "").trim();
        return String.join("", pattern.split("\\s+")).toUpperCase();
    }

    public static String getInitials(String name) {
        String[] words = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                initials.append(word.charAt(0));
            }
        }
        return initials.toString().toUpperCase();
    }

    public String renderOrderID(OrderDetailCreationRequest request, Account account) {
        String customerName;
        if(request.getCustomer() == null) {
            customerName = removeDiacritics(account.getName());
        }else {
            customerName = removeDiacritics(request.getCustomer().getName());
        }
        String roomNames = request.getSelectedRooms()
                .stream()
                .map(room -> getRoomName(room.getName()))
                .collect(Collectors.joining("-"));
        String uuid = UUID.randomUUID().toString();
        return "OD" + uuid.substring(0, 4).toUpperCase() + "-R" + roomNames.toUpperCase() + "-" + getInitials(customerName) + "-D"
                + request.getStartTime().getFirst().getDayOfMonth() + request.getStartTime().getFirst().getMonthValue();
    }

    /*
    [GET]: /order/number-order-current-day
     */
    public int countCurrentlyOrder() {
        return orderRepository.countCurrentlyOrder();
    }

    /*
    [GET]: /order/number-order
     */
    public int countOrder(LocalDateTime startTime, LocalDateTime endTime) {
        return orderRepository.countOrdersBetweenDatetime(startTime, endTime);
    }
}