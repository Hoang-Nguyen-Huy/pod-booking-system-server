package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.Order.OrderUpdateRequest;
import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailCreationRequest;
import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailUpdateRoomRequest;
import com.swp.PodBookingSystem.dto.request.OrderDetailAmenity.OrderDetailAmenityUpdateReq;
import com.swp.PodBookingSystem.dto.request.Room.RoomWithAmenitiesDTO;
import com.swp.PodBookingSystem.dto.respone.Amenity.AmenityManagementResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetail.*;
import com.swp.PodBookingSystem.dto.respone.Order.NumberOrderByBuildingDto;
import com.swp.PodBookingSystem.dto.respone.OrderDetailAmenity.OrderDetailAmenityResponseDTO;
import com.swp.PodBookingSystem.dto.respone.PaginationResponse;
import com.swp.PodBookingSystem.entity.*;
import com.swp.PodBookingSystem.enums.AccountRole;
import com.swp.PodBookingSystem.enums.OrderDetailAmenityStatus;
import com.swp.PodBookingSystem.enums.OrderStatus;
import com.swp.PodBookingSystem.exception.AppException;
import com.swp.PodBookingSystem.exception.ErrorCode;
import com.swp.PodBookingSystem.mapper.OrderDetailMapper;
import com.swp.PodBookingSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final RoomRepository roomRepository;
    private final OrderDetailAmenityRepository orderDetailAmenityRepository;

    //GET:
    public List<OrderDetailResponse> getAllOrders() {
        List<OrderDetail> orders = orderDetailRepository.findAll();
        return orders.stream()
                .map(orderDetailMapper::toOrderDetailResponse)
                .collect(Collectors.toList());
    }

    public OrderDetailFullInfoResponse getOrderDetailByOrderDetailId(String orderDetailId) {
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId).orElse(null);
        if (orderDetail == null) {
            throw new AppException(ErrorCode.ORDER_DETAIL_NOT_EXIST);
        }
        List<AmenityManagementResponse> amenities = orderDetailAmenityService.getOrderDetailAmenitiesByOrderDetailId(orderDetail.getId());
        return OrderDetailFullInfoResponse.builder()
                .id(orderDetail.getId())
                .roomId(orderDetail.getRoom().getId())
                .roomName(orderDetail.getRoom().getName())
                .roomImage(orderDetail.getRoom().getImage())
                .roomPrice(orderDetail.getPriceRoom())
                .buildingAddress(orderDetail.getBuilding().getAddress())
                .buildingId(orderDetail.getBuilding().getId())
                .servicePackage(servicePackageService.toServicePackageResponse(orderDetail.getServicePackage()))
                .status(orderDetail.getStatus().name())
                .orderHandler(Optional.ofNullable(orderDetail.getOrderHandler())
                        .map(accountService::toAccountResponse)
                        .orElse(null))
                .customer(Optional.ofNullable(orderDetail.getCustomer())
                        .map(accountService::toAccountResponse)
                        .orElse(null))
                .startTime(orderDetail.getStartTime())
                .endTime(orderDetail.getEndTime())
                .amenities(amenities)
                .build();
    }

    public List<OrderDetailManagementResponse> getOrderDetailByOrderId(String orderId, String status) {
        return orderDetailRepository.findByOrderIdAndStatus(orderId, OrderStatus.valueOf(status)).stream().map(orderDetail -> {
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
                    .customer(Optional.ofNullable(orderDetail.getCustomer())
                            .map(accountService::toAccountResponse)
                            .orElse(null))
                    .servicePackage(servicePackageService.toServicePackageResponse(orderDetail.getServicePackage()))
                    .status(orderDetail.getStatus().name())
                    .startTime(orderDetail.getStartTime())
                    .endTime(orderDetail.getEndTime())
                    .amenities(amenities)
                    .build();
        }).collect(Collectors.toList());
    }

    public List<OrderDetailManagementResponse> getOrderDetailById(String orderId) {
        return orderDetailRepository.findByOrderId(orderId).stream().map(orderDetail -> {
            List<AmenityManagementResponse> amenities = orderDetailAmenityService.getOrderDetailAmenitiesByOrderDetailId(orderDetail.getId());
            return OrderDetailManagementResponse.builder()
                    .id(orderDetail.getId())
                    .roomId(orderDetail.getRoom().getId())
                    .roomImage(orderDetail.getRoom().getImage())
                    .roomName(orderDetail.getRoom().getName())
                    .roomTypeName(orderDetail.getRoom().getRoomType().getName())
                    .roomPrice(orderDetail.getPriceRoom())
                    .buildingAddress(orderDetail.getBuilding().getAddress())
                    .buildingId(orderDetail.getBuilding().getId())
                    .roomId(orderDetail.getRoom().getId())
                    .orderHandler(Optional.ofNullable(orderDetail.getOrderHandler())
                            .map(accountService::toAccountResponse)
                            .orElse(null))
                    .customer(Optional.ofNullable(orderDetail.getCustomer())
                            .map(accountService::toAccountResponse)
                            .orElse(null))
                    .servicePackage(servicePackageService.toServicePackageResponse(orderDetail.getServicePackage()))
                    .status(orderDetail.getStatus().name())
                    .startTime(orderDetail.getStartTime())
                    .endTime(orderDetail.getEndTime())
                    .amenities(amenities)
                    .build();
        }).collect(Collectors.toList());
    }


    public Page<OrderDetailResponse> getOrdersByCustomerId(String customerId, String status, int page, int take) {
        Pageable pageable = PageRequest.of(page - 1, take);

        Page<OrderDetail> orderDetails = orderDetailRepository.findByCustomer_Id(customerId, OrderStatus.valueOf(status), pageable);

        return orderDetails.map(orderDetail -> {
            OrderDetailResponse response = orderDetailMapper.toOrderDetailResponse(orderDetail);

            // Gán amenities cho từng OrderDetailResponse
            response.setAmenities(orderDetailAmenityService.getOrderDetailAmenitiesByOrderDetailId(response.getId()));

            return response;
        });
    }

    public PaginationResponse<List<OrderDetailAmenityListResponse>> getPagedOrderDetails(Account user, LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Page<OrderDetail> orderDetailPage;
        if (user.getRole() == AccountRole.Admin) {
            orderDetailPage = orderDetailRepository.findAllWithTimeRange(startDate, endDate, PageRequest.of(page, size));
        } else if (user.getRole() == AccountRole.Staff || user.getRole() == AccountRole.Manager) {
            orderDetailPage = orderDetailRepository.findOrdersByBuildingNumberAndTimeRange(user.getBuildingNumber(), startDate, endDate, PageRequest.of(page, size));
        } else {
            throw new RuntimeException("Only admin, staff and manager can access this API");
        }

        List<OrderDetailAmenityListResponse> orderDetailResponses = orderDetailPage.getContent().stream()
                .map(orderDetail -> {
                    List<OrderDetailAmenityResponseDTO> amenities =
                            orderDetailAmenityService.getOrderDetailAmenitiesAllInfoByOrderDetailId(orderDetail.getId()).stream()
                                    .map(oda -> OrderDetailAmenityResponseDTO.builder()
                                            .id(oda.getId())
                                            .quantity(oda.getQuantity())
                                            .price(oda.getPrice())
                                            .orderDetailId(oda.getOrderDetail().getId())
                                            .amenityId(oda.getAmenity().getId())
                                            .amenityName(oda.getAmenity().getName())
                                            .amenityType(oda.getAmenity().getType())
                                            .status(Optional.ofNullable(oda.getStatus())
                                                    .orElse(null))
                                            .statusDescription(Optional.ofNullable(oda.getStatus())
                                                    .map(OrderDetailAmenityStatus::getDescription)
                                                    .orElse(null))
                                            .createdAt(oda.getCreatedAt())
                                            .updatedAt(oda.getUpdatedAt())
                                            .build())
                                    .collect(Collectors.toList());
                    return OrderDetailAmenityListResponse.builder()
                            .id(orderDetail.getId())
                            .customerId(Optional.ofNullable(orderDetail.getCustomer())
                                    .map(Account::getId)
                                    .orElse(null))
                            .customerName(Optional.ofNullable(orderDetail.getCustomer())
                                    .map(Account::getName)
                                    .orElse(null))
                            .orderHandledId(Optional.ofNullable(orderDetail.getOrderHandler())
                                    .map(Account::getId)
                                    .orElse(null))
                            .buildingId(orderDetail.getBuilding().getId())
                            .buildingAddress(orderDetail.getBuilding().getAddress())
                            .roomId(orderDetail.getRoom().getId())
                            .roomName(orderDetail.getRoom().getName())
                            .orderId(orderDetail.getOrder().getId())
                            .orderDetailAmenities(amenities)
                            .servicePackageId(orderDetail.getServicePackage().getId())
                            .orderHandledId(Optional.ofNullable(orderDetail.getOrderHandler())
                                    .map(Account::getId)
                                    .orElse(null))
                            .priceRoom(orderDetail.getPriceRoom())
                            .status(orderDetail.getStatus())
                            .startTime(orderDetail.getStartTime())
                            .endTime(orderDetail.getEndTime())
                            .createdAt(orderDetail.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());

        return PaginationResponse.<List<OrderDetailAmenityListResponse>>builder()
                .data(orderDetailResponses)
                .currentPage(orderDetailPage.getNumber())
                .totalPage(orderDetailPage.getTotalPages())
                .recordPerPage(orderDetailPage.getSize())
                .totalRecord((int) orderDetailPage.getTotalElements())
                .build();
    }

    //CREATE:
    public boolean processOrderDetails(OrderDetailCreationRequest request, Order order, Account account) {
        if (account.getRole() != AccountRole.Customer) {
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
        for (int i = 0; i < request.getStartTime().size(); i++) {
            LocalDateTime startTime = request.getStartTime().get(i);
            LocalDateTime endTime = request.getEndTime().get(i);
            for (int week = 0; week < 4; week++) {
                LocalDateTime newStartTime = startTime.plusWeeks(week);
                LocalDateTime newEndTime = endTime.plusWeeks(week);
                isSomeRoomWasBook |= createOrderDetailsForRooms(request, selectedRooms, order, account, newStartTime, newEndTime);
            }
        }
        return isSomeRoomWasBook;
    }

    private boolean handleDailyBooking(List<RoomWithAmenitiesDTO> selectedRooms,
                                       OrderDetailCreationRequest request,
                                       Order order, Account account) {
        boolean isSomeRoomWasBook = false;
        for (int i = 0; i < request.getStartTime().size(); i++) {
            LocalDateTime startTime = request.getStartTime().get(i);
            LocalDateTime endTime = request.getEndTime().get(i);
            for (int day = 0; day < 30; day++) {
                LocalDateTime newStartTime = startTime.plusDays(day);
                LocalDateTime newEndTime = endTime.plusDays(day);

                isSomeRoomWasBook |= createOrderDetailsForRooms(request, selectedRooms, order, account, newStartTime, newEndTime);
            }
        }
        return isSomeRoomWasBook;
    }

    private boolean handleStandardBooking(List<RoomWithAmenitiesDTO> selectedRooms,
                                          OrderDetailCreationRequest request,
                                          Order order, Account account) {
        boolean isSomeRoomWasBook = false;
        for (int i = 0; i < request.getStartTime().size(); i++) {
            LocalDateTime startTime = request.getStartTime().get(i);
            LocalDateTime endTime = request.getEndTime().get(i);
            isSomeRoomWasBook = createOrderDetailsForRooms(request, selectedRooms, order, account,
                    startTime, endTime);
        }
        return isSomeRoomWasBook;
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
            orderDetailAmenity.setPrice(amenity.getPrice());
            orderDetailAmenity.setOrderDetail(orderDetail);
            orderDetailAmenity.setAmenity(amenity);

            orderDetailAmenityService.updateAmenityQuantityAfterCreateODA(orderDetailAmenity);
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
            response.setDiscountPercentage(servicePackage.getDiscountPercentage());
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
    public void updateOrderDetail(OrderUpdateRequest request) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(request.getId());
        for (OrderDetail od : orderDetails) {
            if (request.getStatus() != null) {
                od.setStatus(request.getStatus());
                if (request.getStatus().equals(OrderStatus.Rejected)) {
                    double total = 0;
                    int countService = 0;
                    if (od.getServicePackage().getId() == 1) {
                        countService = 4;
                    } else if (od.getServicePackage().getId() == 2) {
                        countService = 30;
                    } else {
                        countService = 1;
                    }
                    total += od.getPriceRoom() * (100 - od.getDiscountPercentage()) * countService / 100;
                    List<OrderDetailAmenity> listOda = orderDetailAmenityRepository.findByOrderDetailId(od.getId());
                    for (OrderDetailAmenity oda : listOda) {
                        total += oda.getPrice() * oda.getQuantity() * (100 - od.getDiscountPercentage()) * countService / 100;
                        orderDetailAmenityService.updateOrderDetailAmenityStatus(new OrderDetailAmenityUpdateReq(oda.getId(), OrderDetailAmenityStatus.Canceled));
                    }
                    Account customer = od.getCustomer();
                    if (customer != null) {
                        customer.setBalance(customer.getBalance() + total);
                        accountRepository.save(customer);
                    }
                }
            }
            if (request.getOrderHandler() != null) {
                Account orderHandler = accountService.getAccountById(request.getOrderHandler().getId());
                od.setOrderHandler(orderHandler);
            }
            if (request.getCancelReason() != null) {
                od.setCancelReason(request.getCancelReason());
            }
            if (request.getOrderDetails() != null && !request.getOrderDetails().isEmpty()) {
                for (OrderDetailUpdateRoomRequest odr : request.getOrderDetails()) {
                    if (odr.getId().equals(od.getId())) {
                        Optional<Room> room = roomRepository.findById(odr.getRoomId());
                        if (room.isEmpty()) {
                            throw new RuntimeException("Room not found with id: " + odr.getRoomId());
                        }
                        if (room.get().getRoomType().equals(od.getRoom().getRoomType())) {
                            od.setRoom(room.get());
                        } else {
                            throw new RuntimeException("Room type not match");
                        }
                    }
                }
            }
            orderDetailRepository.updateOrderDetailUpdatedAt(od.getId(), LocalDateTime.now());
        }
    }

    public void updateOrderHandlerOrderDetail(String orderId, Account accountHandler) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
        Account orderHandler = accountService.getAccountById(accountHandler.getId());
        for (OrderDetail od : orderDetails) {
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

    //UTILS:
    @Scheduled(cron = "0 0 */2 * * ?")
    public void restoreAmenityQuantityIfOrderDetailExpired() {
        LocalDateTime now = LocalDateTime.now();
        List<OrderDetail> expiredOrderDetails = orderDetailRepository.findByEndTime(now);
        for (OrderDetail od : expiredOrderDetails) {
            orderDetailAmenityService.restoreAmenityQuantity(od.getId());
        }
    }

    /*
    [GET]: /order-detail/revenue-current-day
     */
    public double calculateRevenueCurrentDay() {
        return orderDetailRepository.calculateRevenueCurrentDay();
    }

    /*
    [GET]: /order-detail/revenue?
     */
    public double calculateRevenue(LocalDateTime startTime, LocalDateTime endTime) {
        return orderDetailRepository.calculateRevenueBetweenDateTime(startTime, endTime).orElse(0.0);
    }

    /*
    [GET]: /order-detail/revenue-chart
     */
    public List<RevenueChartDto> calculateRevenueByMonth(LocalDateTime startTime, LocalDateTime endTime, String viewWith) {
        if (startTime == null) {
            startTime = LocalDate.now().atStartOfDay();
        }
        if (endTime == null) {
            endTime = LocalDate.now().atTime(LocalTime.MAX);
        }
        if (viewWith == null) {
            return Collections.singletonList(orderDetailRepository.calculateRevenueForSingleDay(startTime));
        }
        switch (viewWith.toLowerCase()) {
            case "day":
                return Collections.singletonList(orderDetailRepository.calculateRevenueForSingleDay(startTime));
            case "month":
                return calculateRevenueByMonth(startTime, endTime);
            case "quarter":
                return orderDetailRepository.calculateRevenueByQuarter(startTime, endTime);
            default:
                return Collections.singletonList(orderDetailRepository.calculateRevenueForSingleDay(startTime));
        }
    }

    public List<RevenueChartDto> calculateRevenueByMonth(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null) {
            startTime = LocalDate.now().atStartOfDay();
        }
        if (endTime == null) {
            endTime = LocalDate.now().atTime(LocalTime.MAX);
        }

        // Step 1: Generate all dates from startTime to endTime
        List<LocalDate> dateRange = startTime.toLocalDate()
                .datesUntil(endTime.toLocalDate().plusDays(1))
                .collect(Collectors.toList());

        // Step 2: Fetch actual revenue data from the repository
        List<RevenueChartDto> actualRevenueData = orderDetailRepository.calculateRevenueByMonth(startTime, endTime);

        // Step 3: Convert actual revenue data to a map for fast lookup
        Map<String, Double> revenueMap = actualRevenueData.stream()
                .collect(Collectors.toMap(RevenueChartDto::getDate, RevenueChartDto::getRevenue));

        // Step 4: Populate the result with all dates in range, setting missing data to zero
        List<RevenueChartDto> result = dateRange.stream()
                .map(date -> {
                    String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    return new RevenueChartDto(formattedDate, revenueMap.getOrDefault(formattedDate, 0.0));
                })
                .collect(Collectors.toList());

        return result;
    }


    /*
    [GET]: /order-detail/number-order-by-building
     */
    public List<NumberOrderByBuildingDto> getNumberOrderByBuilding() {
        return orderDetailRepository.countOrdersByBuilding();
    }
}
