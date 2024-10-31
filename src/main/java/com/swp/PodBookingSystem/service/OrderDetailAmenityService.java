package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.OrderDetailAmenity.OrderDetailAmenityRequest;
import com.swp.PodBookingSystem.dto.request.OrderDetailAmenity.OrderDetailAmenityCreationRequest;
import com.swp.PodBookingSystem.dto.request.OrderDetailAmenity.OrderDetailAmenityUpdateReq;
import com.swp.PodBookingSystem.dto.respone.Amenity.AmenityManagementResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetailAmenity.OrderDetailAmenityResponse;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.entity.Amenity;
import com.swp.PodBookingSystem.entity.OrderDetail;
import com.swp.PodBookingSystem.entity.OrderDetailAmenity;
import com.swp.PodBookingSystem.enums.AmenityType;
import com.swp.PodBookingSystem.enums.OrderDetailAmenityStatus;
import com.swp.PodBookingSystem.mapper.AmenityMapper;
import com.swp.PodBookingSystem.mapper.OrderDetailAmenityMapper;
import com.swp.PodBookingSystem.repository.AccountRepository;
import com.swp.PodBookingSystem.repository.AmenityRepository;
import com.swp.PodBookingSystem.repository.OrderDetailAmenityRepository;
import com.swp.PodBookingSystem.repository.OrderDetailRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderDetailAmenityService {
    OrderDetailAmenityRepository orderDetailAmenityRepository;
    AmenityRepository amenityRepository;
    OrderDetailRepository orderDetailRepository;
    OrderDetailAmenityMapper orderDetailAmenityMapper;
    AmenityMapper amenityMapper;
    private final AccountRepository accountRepository;

    //GET:
    public List<AmenityManagementResponse> getOrderDetailAmenitiesByOrderDetailId(String orderDetailId) {
        return orderDetailAmenityRepository.findByOrderDetailId(orderDetailId).stream().map(amenity -> AmenityManagementResponse.builder()
                .id(amenity.getAmenity().getId())
                .name(amenity.getAmenity().getName())
                .price(amenity.getPrice())
                .quantity(amenity.getQuantity())
                .build()).collect(Collectors.toList());
    }

    public List<OrderDetailAmenity> getOrderDetailAmenitiesAllInfoByOrderDetailId(String orderDetailId) {
        return orderDetailAmenityRepository.findByOrderDetailId(orderDetailId);
    }

    //CREATE:
    public void createOrderDetailAmenity(OrderDetailAmenityRequest request) {
        Optional<OrderDetail> orderDetail = orderDetailRepository.findById(request.getOrderDetailId());
        Optional<Amenity> amenity = amenityRepository.findById(request.getAmenityId());
        if(orderDetail.isEmpty() || amenity.isEmpty()) {
            throw new RuntimeException("Order detail or amenity not found");
        }
        OrderDetailAmenity orderDetailAmenity = new OrderDetailAmenity();
        orderDetailAmenity.setStatus(OrderDetailAmenityStatus.Paid);
        orderDetailAmenity.setCreatedAt(LocalDateTime.now());
        orderDetailAmenity.setUpdatedAt(LocalDateTime.now());
        orderDetailAmenity.setId(UUID.randomUUID().toString());
        orderDetailAmenity.setQuantity(request.getQuantity());
        orderDetailAmenity.setPrice(amenity.get().getPrice());
        orderDetailAmenity.setOrderDetail(orderDetail.get());
        orderDetailAmenity.setAmenity(amenity.get());

        updateAmenityQuantityAfterCreateODA(orderDetailAmenity);
    }

    public void updateAmenityQuantityAfterCreateODA(OrderDetailAmenity orderDetailAmenity) {
        Optional<Amenity> amenityOptional = amenityRepository.findById(orderDetailAmenity.getAmenity().getId());
        if(amenityOptional.isEmpty()) {
            throw new RuntimeException("Amenity not found");
        }
        Amenity amenity = amenityOptional.get();
        if(amenity.getQuantity() < orderDetailAmenity.getQuantity()) {
            throw new RuntimeException("Not enough quantity");
        }
        amenity.setQuantity(amenity.getQuantity() - orderDetailAmenity.getQuantity());
        amenityRepository.save(amenity);
        orderDetailAmenityRepository.save(orderDetailAmenity);
    }

    /*
    [POST]: /order-detail-amenity
     */
    public OrderDetailAmenityResponse createOrderDetailAmenityApi(OrderDetailAmenityCreationRequest request){
        Optional<Amenity> amenity = amenityRepository.findById(request.getAmenityId());
        Optional<OrderDetail> orderDetail = orderDetailRepository.findById(request.getOrderDetailId());
        if (orderDetail.isEmpty()) {
            throw new RuntimeException("Order Detail not found");
        }
        if (amenity.isEmpty()) {
            throw new RuntimeException("Amenity not found");
        }
        Amenity updatedAmenity = amenity.get();
        if(updatedAmenity.getQuantity() < request.getQuantity()){
            throw new RuntimeException("Not enough quantity");
        }
        updatedAmenity.setQuantity(updatedAmenity.getQuantity() - request.getQuantity());
        amenityRepository.save(updatedAmenity);

        OrderDetail savedOrderDetail= orderDetail.get();

        OrderDetailAmenity newOrderDetailAmenity = orderDetailAmenityMapper.toOrderDetailAmenity(request);
        newOrderDetailAmenity.setAmenity(updatedAmenity);
        newOrderDetailAmenity.setOrderDetail(savedOrderDetail);


        OrderDetailAmenity savedOrderDetailAmenity = orderDetailAmenityRepository.save(newOrderDetailAmenity);

        return OrderDetailAmenityResponse.builder()
                .id(savedOrderDetailAmenity.getId())
                .quantity(savedOrderDetailAmenity.getQuantity())
                .price(savedOrderDetailAmenity.getPrice())
                .orderDetailId(savedOrderDetailAmenity.getOrderDetail().getId())
                .amenity(amenityMapper.toAmenityResponseDTO(updatedAmenity))
                .build();
    }

    //UPDATE:
    public void updateOrderDetailAmenityStatus(OrderDetailAmenityUpdateReq request) {
        Optional<OrderDetailAmenity> orderDetailAmenity = orderDetailAmenityRepository.findById(request.getId());
        if (orderDetailAmenity.isEmpty()) {
            throw new RuntimeException("Order detail amenity not found");
        }
        OrderDetailAmenity updateOrderDetailAmenity = orderDetailAmenity.get();
        OrderDetail od = updateOrderDetailAmenity.getOrderDetail();
        od.setUpdatedAt(LocalDateTime.now());
        orderDetailRepository.save(od);
        if(request.getStatus() == OrderDetailAmenityStatus.Canceled){
            Account customer = od.getCustomer();
            customer.setBalance(customer.getBalance() + updateOrderDetailAmenity.getPrice() * updateOrderDetailAmenity.getQuantity());
            accountRepository.save(customer);
        }
        updateOrderDetailAmenity.setStatus(request.getStatus());
        orderDetailAmenityRepository.save(updateOrderDetailAmenity);
    }

    //DELETE:
    @Transactional
    public double deleteOrderDetailAmenityByOrderDetailId(String orderDetailId) {
        double total = 0;
        List<OrderDetailAmenity> orderDetailAmenities = orderDetailAmenityRepository.findByOrderDetailId(orderDetailId);
        for (OrderDetailAmenity orderDetailAmenity : orderDetailAmenities) {
            total += orderDetailAmenity.getPrice() * orderDetailAmenity.getQuantity();
        }
        orderDetailAmenityRepository.deleteByOrderDetailId(orderDetailId);
        return total;
    }

    //UTILS:
    public void restoreAmenityQuantity(String orderDetailId) {
        List<OrderDetailAmenity> orderDetailAmenities = orderDetailAmenityRepository.findByOrderDetailId(orderDetailId);
        for(OrderDetailAmenity orderDetailAmenity : orderDetailAmenities){
            if(orderDetailAmenity.getAmenity().getType() == AmenityType.Office) {
                Optional<Amenity> amenity = amenityRepository.findById(orderDetailAmenity.getAmenity().getId());
                if (amenity.isEmpty()) {
                    throw new RuntimeException("Amenity not found");
                }
                Amenity updateAmenity = amenity.get();
                updateAmenity.setQuantity(updateAmenity.getQuantity() + orderDetailAmenity.getQuantity());
                amenityRepository.save(updateAmenity);
            }
        }
    }
}