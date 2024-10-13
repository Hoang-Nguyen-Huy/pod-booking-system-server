    package com.swp.PodBookingSystem.service;

    import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailCreationRequest;
    import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailRequestDTO;
    import com.swp.PodBookingSystem.dto.respone.Amenity.AmenityManagementResponse;
    import com.swp.PodBookingSystem.dto.respone.OrderDetail.OrderDetailManagementResponse;
    import com.swp.PodBookingSystem.dto.respone.OrderDetail.OrderDetailResponse;
    import com.swp.PodBookingSystem.entity.*;
    import com.swp.PodBookingSystem.enums.OrderStatus;
    import com.swp.PodBookingSystem.mapper.OrderDetailMapper;
    import com.swp.PodBookingSystem.repository.*;
    import org.hibernate.validator.internal.util.stereotypes.Lazy;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.LocalTime;
    import java.util.List;
    import java.util.Optional;
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
        private ServicePackageRepository servicePackageRepository;

        @Autowired
        private OrderDetailMapper orderDetailMapper;

        @Autowired
        private OrderDetailAmenityService orderDetailAmenityService;

        private static final Logger log = LoggerFactory.getLogger(OrderService.class);
        @Autowired
        private AccountService accountService;
        @Autowired
        private ServicePackageService servicePackageService;
        @Autowired
        private OrderService orderService;



        public List<OrderDetailResponse> getAllOrders() {
            List<OrderDetail> orders = orderDetailRepository.findAll();
            return orders.stream()
                    .map(orderDetailMapper::toOrderDetailResponse)
                    .collect(Collectors.toList());
        }

        public List<OrderDetail> getOrderDetailsByOrderId(String orderId) {
            return orderDetailRepository.findByOrderId(orderId);
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
                        .orderHandler(accountService.toAccountResponse(orderDetail.getOrderHandler()))
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



        public OrderDetailResponse createOrderDetail(OrderDetailCreationRequest request, Order order, Room room, OrderStatus status, Account account, LocalDateTime startTime, LocalDateTime endTime) {
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
                response.setStartTime(startTime);
                response.setEndTime(endTime);
                response.setCreatedAt(LocalDateTime.now());
                response.setUpdatedAt(LocalDateTime.now());
                response.setStatus(status);

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

        public List<OrderDetail> getNextDayBookings(LocalDate dayNow){
            // Calculate the start of the next day (00:00:00)
            LocalDateTime startOfDay = dayNow.plusDays(1).atStartOfDay();

            // Calculate the end of the next day (23:59:59.999)
            LocalDateTime endOfDay = dayNow.plusDays(1).atTime(LocalTime.MAX);

            // Fetch all bookings between start and end of tomorrow
            return orderDetailRepository.findAllOrderDetailsByDay(startOfDay, endOfDay);
        }


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


            orderService.updateOrderByUpdateOrderDetail(existingOrderDetail.getOrder().getId(),existingOrderDetail.getUpdatedAt());
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

            List<OrderDetail> orderDetails = this.getOrderDetailsByOrderId(orderId);

            for(OrderDetail od : orderDetails){
                od.setOrderHandler(accountHandler);
            }

            orderDetailRepository.saveAll(orderDetails);
        }

    }
