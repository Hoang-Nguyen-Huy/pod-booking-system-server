package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailCreationRequest;
import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailRequestDTO;
import com.swp.PodBookingSystem.dto.request.Room.RoomWithAmenitiesDTO;
import com.swp.PodBookingSystem.dto.respone.Amenity.AmenityManagementResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetail.OrderDetailManagementResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetail.OrderDetailResponse;
import com.swp.PodBookingSystem.entity.*;
import com.swp.PodBookingSystem.enums.AccountRole;
import com.swp.PodBookingSystem.enums.OrderStatus;
import com.swp.PodBookingSystem.exception.AppException;
import com.swp.PodBookingSystem.exception.ErrorCode;
import com.swp.PodBookingSystem.mapper.OrderDetailMapper;
import com.swp.PodBookingSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderDetailService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderDetailRepository orderDetailRepository;
    private final AccountRepository accountRepository;
    private final BuildingRepository buildingRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final OrderDetailMapper orderDetailMapper;
    private final OrderDetailAmenityService orderDetailAmenityService;
    private final AccountService accountService;
    private final ServicePackageService servicePackageService;
    private final RoomService roomService;

    //GET:
    public List<OrderDetailResponse> getAllOrders() {
        List<OrderDetail> orders = orderDetailRepository.findAll();
        return orders.stream()
                .map(orderDetailMapper::toOrderDetailResponse)
                .collect(Collectors.toList());
    }

    public List<OrderDetailManagementResponse> getOrderDetailById(String orderId) {
        return orderDetailRepository.findByOrderId(orderId).stream().map(orderDetail -> {
            List<AmenityManagementResponse> amenities = orderDetailAmenityService.getOrderDetailAmenitiesByOrderDetailId(orderDetail.getId());
            return OrderDetailManagementResponse.builder()
                    .id(orderDetail.getId())
                    .roomId(orderDetail.getRoom().getId())
                    .roomName(orderDetail.getRoom().getName())
                    .roomPrice(orderDetail.getPriceRoom())
                    .buildingAddress(orderDetail.getBuilding().getAddress())
                    .buildingId(orderDetail.getBuilding().getId())
                    .roomId(orderDetail.getRoom().getId())
                    .orderHandler(Optional.ofNullable(orderDetail.getOrderHandler())
                            .map(accountService::toAccountResponse)
                            .orElse(null))
                    .customer(accountService.toAccountResponse(orderDetail.getCustomer()))
                    .servicePackage(servicePackageService.toServicePackageResponse(orderDetail.getServicePackage()))
                    .status(orderDetail.getStatus().name())
                    .startTime(orderDetail.getStartTime())
                    .endTime(orderDetail.getEndTime())
                    .amenities(amenities)
                    .build();
        }).collect(Collectors.toList());
    }

    public List<OrderDetailResponse> getOrdersByCustomerId(String customerId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByCustomer_Id(customerId);
        System.out.println("Orders found for customer " + customerId + ": " + orderDetails.size());
        var orderDetailResponses = orderDetails.stream()
                .map(orderDetailMapper::toOrderDetailResponse) // Use the mapper for conversion
                .toList();
        for (OrderDetailResponse orderDetail : orderDetailResponses) {
            System.out.println("Order Detail: " + orderDetailAmenityService.getOrderDetailAmenitiesByOrderDetailId(orderDetail.getOrderId()));
            orderDetail.setAmenities(orderDetailAmenityService.getOrderDetailAmenitiesByOrderDetailId(orderDetail.getId()));
        }
        return orderDetailResponses;
    }

    //CREATE:
    public boolean processOrderDetails(OrderDetailCreationRequest request, Order order, Account account) {
        if(account.getRole() != AccountRole.Customer){
            account = request.getCustomer();
        }
        List<RoomWithAmenitiesDTO> selectedRooms = request.getSelectedRooms();
        return switch (request.getServicePackage().getId()) {
            case 1 -> handleWeeklyBooking(selectedRooms, request, order, account);
            case 2 -> handleDailyBooking(selectedRooms, request, order, account);
            case 3 -> handleStandardBooking(selectedRooms, request, order, account);
            default -> throw new AppException(ErrorCode.INVALID_KEY);
        };
    }

    private boolean handleWeeklyBooking(List<RoomWithAmenitiesDTO> selectedRooms,
                                        OrderDetailCreationRequest request,
                                        Order order, Account account) {
        boolean isSomeRoomWasBook = false;
        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getEndTime();

        for (int week = 0; week < 4; week++) {
            LocalDateTime newStartTime = startTime.plusWeeks(week);
            LocalDateTime newEndTime = endTime.plusWeeks(week);
            isSomeRoomWasBook |= createOrderDetailsForRooms(request, selectedRooms, order, account, newStartTime, newEndTime);
        }
        return isSomeRoomWasBook;
    }

    private boolean handleDailyBooking(List<RoomWithAmenitiesDTO> selectedRooms,
                                       OrderDetailCreationRequest request,
                                       Order order, Account account) {
        boolean isSomeRoomWasBook = false;
        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getEndTime();

        for (int day = 0; day < 30; day++) {
            LocalDateTime newStartTime = startTime.plusDays(day);
            LocalDateTime newEndTime = endTime.plusDays(day);

            isSomeRoomWasBook |= createOrderDetailsForRooms(request, selectedRooms, order, account, newStartTime, newEndTime);
        }
        return isSomeRoomWasBook;
    }

    private boolean handleStandardBooking(List<RoomWithAmenitiesDTO> selectedRooms,
                                          OrderDetailCreationRequest request,
                                          Order order, Account account) {
        return createOrderDetailsForRooms(request, selectedRooms, order, account,
                request.getStartTime(), request.getEndTime());
    }

    private boolean createOrderDetailsForRooms(OrderDetailCreationRequest request, List<RoomWithAmenitiesDTO> selectedRooms,
                                               Order order, Account account,
                                               LocalDateTime startTime, LocalDateTime endTime) {
        boolean isSomeRoomWasBook = false;
        for (RoomWithAmenitiesDTO roomWithAmenities : selectedRooms) {
            Room room = roomService.getRoomByIdV2(roomWithAmenities.getId());
            boolean isAvailable = roomService.isRoomAvailable(room.getId(), startTime, endTime);

            OrderStatus status = isAvailable ? OrderStatus.Successfully : OrderStatus.Pending;
            isSomeRoomWasBook |= !isAvailable;

            OrderDetail orderDetail = createOrderDetail(
                    request, order, room, status, account, startTime, endTime);

            createOrderDetailAmenities(orderDetail, roomWithAmenities.getAmenities());
        }
        return isSomeRoomWasBook;
    }

    private void createOrderDetailAmenities(OrderDetail orderDetail, List<Amenity> amenities) {
        for (Amenity amenity : amenities) {
            OrderDetailAmenity orderDetailAmenity = new OrderDetailAmenity();
            orderDetailAmenity.setId(UUID.randomUUID().toString());
            orderDetailAmenity.setQuantity(amenity.getQuantity());
            orderDetailAmenity.setPrice(amenity.getPrice() * amenity.getQuantity());
            orderDetailAmenity.setOrderDetail(orderDetail);
            orderDetailAmenity.setAmenity(amenity);

            orderDetailAmenityService.createOrderDetailAmenity(orderDetailAmenity);
        }
    }

    public OrderDetail createOrderDetail(OrderDetailCreationRequest request, Order order, Room room, OrderStatus status, Account account, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            ServicePackage servicePackage = servicePackageRepository.findById(request.getServicePackage().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Service package not found"));
            Building building = buildingRepository.findById(request.getBuilding().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Building not found"));

            OrderDetail response = new OrderDetail();

            response.setCustomer(account);
            response.setId(UUID.randomUUID().toString());
            response.setOrder(order);
            response.setBuilding(building);
            response.setRoom(room);
            response.setServicePackage(servicePackage);
            response.setOrderHandler(null);
            response.setPriceRoom(request.getPriceRoom());
            response.setDiscountPercentage(request.getDiscountPercentage());
            response.setStartTime(startTime);
            response.setEndTime(endTime);
            response.setCreatedAt(LocalDateTime.now());
            response.setUpdatedAt(LocalDateTime.now());
            response.setStatus(status);

            return orderDetailRepository.save(response);
        } catch (IllegalArgumentException e) {
            log.error("Validation error creating order detail: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating order detail", e);
            throw new RuntimeException("Failed to create order detail: " + e.getMessage(), e);
        }
    }

    public List<OrderDetail> getNextDayBookings(LocalDate dayNow) {
        LocalDateTime startOfDay = dayNow.plusDays(1).atStartOfDay();
        LocalDateTime endOfDay = dayNow.plusDays(1).atTime(LocalTime.MAX);
        return orderDetailRepository.findAllOrderDetailsByDay(startOfDay, endOfDay);
    }

    //UPDATE:
    public OrderDetailResponse updateOrderDetail(String orderDetailId, OrderDetailRequestDTO request){
        Optional<OrderDetail> existingOrderDetailOptional  = orderDetailRepository.findById(orderDetailId);
        if (existingOrderDetailOptional .isEmpty()) {
            throw new RuntimeException("OrderDetail not found with id: " + orderDetailId);
        }
        OrderDetail existingOrderDetail = existingOrderDetailOptional.get();
        existingOrderDetail.setCustomer(request.getCustomer());
        existingOrderDetail.setBuilding(request.getBuilding());
        existingOrderDetail.setRoom(request.getRoom());
        existingOrderDetail.setOrder(request.getOrder());
        existingOrderDetail.setServicePackage(request.getServicePackage());
        existingOrderDetail.setOrderHandler(request.getOrderHandler());
        existingOrderDetail.setPriceRoom(request.getPriceRoom());
        existingOrderDetail.setDiscountPercentage(request.getDiscountPercentage());
        existingOrderDetail.setStatus(request.getStatus());
        existingOrderDetail.setStartTime(request.getStartTime());
        existingOrderDetail.setEndTime(request.getEndTime());
        existingOrderDetail.setUpdatedAt(LocalDateTime.now());

        //orderService.updateOrderByUpdateOrderDetail(existingOrderDetail.getOrder().getId(),existingOrderDetail.getUpdatedAt());
        OrderDetail updatedOrderDetail = orderDetailRepository.save(existingOrderDetail);
        return OrderDetailResponse.builder()
                .id(updatedOrderDetail.getId())
                .customerId(updatedOrderDetail.getCustomerId())
                .buildingId(updatedOrderDetail.getBuilding().getId())
                .roomId(updatedOrderDetail.getRoom().getId())
                .orderId(updatedOrderDetail.getOrder().getId())
                .servicePackageId(updatedOrderDetail.getServicePackage() != null ? updatedOrderDetail.getServicePackage().getId() : 0)
                .orderHandledId(updatedOrderDetail.getOrderHandler() != null ? updatedOrderDetail.getOrderHandler().getId() : null)
                .priceRoom(updatedOrderDetail.getPriceRoom())
                .status(updatedOrderDetail.getStatus())
                .startTime(updatedOrderDetail.getStartTime())
                .endTime(updatedOrderDetail.getEndTime())
                .createdAt(updatedOrderDetail.getCreatedAt())
                .build();
    }

    public void updateOrderHandlerOrderDetail(String orderId, Account accountHandler){
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
        Account orderHandler = accountService.getAccountById(accountHandler.getId());
        for(OrderDetail od : orderDetails){
            od.setOrderHandler(orderHandler);
        }
        orderDetailRepository.saveAll(orderDetails);
    }

    //DELETE:
    @Transactional
    public void deleteOrderDetailsByOrderId(String orderId) {
        double total = 0;
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
        for (OrderDetail orderDetail : orderDetails) {
            total += orderDetail.getPriceRoom() * orderDetail.getDiscountPercentage() / 100;
            total += orderDetailAmenityService.deleteOrderDetailAmenityByOrderDetailId(orderDetail.getId()) * orderDetail.getDiscountPercentage() / 100;
        }
        Account customer = orderDetails.getFirst().getCustomer();
        customer.setBalance(customer.getBalance() + total);
        accountRepository.save(customer);
        orderDetailRepository.deleteByOrderId(orderId);
    }
}
