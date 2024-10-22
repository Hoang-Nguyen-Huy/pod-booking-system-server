package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.OrderDetailAmenity.OrderDetailAmenityRequest;
import com.swp.PodBookingSystem.dto.respone.Amenity.AmenityManagementResponse;
import com.swp.PodBookingSystem.entity.Amenity;
import com.swp.PodBookingSystem.entity.OrderDetail;
import com.swp.PodBookingSystem.entity.OrderDetailAmenity;
import com.swp.PodBookingSystem.enums.AmenityType;
import com.swp.PodBookingSystem.repository.AmenityRepository;
import com.swp.PodBookingSystem.repository.OrderDetailAmenityRepository;
import com.swp.PodBookingSystem.repository.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderDetailAmenityService {
    private final OrderDetailAmenityRepository orderDetailAmenityRepository;
    private final AmenityRepository amenityRepository;
    private final OrderDetailRepository orderDetailRepository;

    //GET:
    public List<AmenityManagementResponse> getOrderDetailAmenitiesByOrderDetailId(String orderDetailId) {
        return orderDetailAmenityRepository.findByOrderDetailId(orderDetailId).stream().map(amenity -> AmenityManagementResponse.builder()
                .id(amenity.getAmenity().getId())
                .name(amenity.getAmenity().getName())
                .price(amenity.getPrice())
                .quantity(amenity.getQuantity())
                .build()).collect(Collectors.toList());
    }

    //CREATE:
    public void createOrderDetailAmenity(OrderDetailAmenityRequest request) {
        Optional<OrderDetail> orderDetail = orderDetailRepository.findById(request.getOrderDetailId());
        Optional<Amenity> amenity = amenityRepository.findById(request.getAmenityId());
        if(orderDetail.isEmpty() || amenity.isEmpty()) {
            throw new RuntimeException("Order detail or amenity not found");
        }
        OrderDetailAmenity orderDetailAmenity = new OrderDetailAmenity();
        //orderDetailAmenity.setCreatedAt(LocalDateTime.now());
        //orderDetailAmenity.setUpdatedAt(LocalDateTime.now());
        orderDetailAmenity.setId(UUID.randomUUID().toString());
        orderDetailAmenity.setQuantity(request.getQuantity());
        orderDetailAmenity.setPrice(amenity.get().getPrice());
        orderDetailAmenity.setOrderDetail(orderDetail.get());
        orderDetailAmenity.setAmenity(amenity.get());

        updateAmenityQuantityAfterCreateODA(orderDetailAmenity);
    }

    public void updateAmenityQuantityAfterCreateODA(OrderDetailAmenity orderDetailAmenity) {
        Optional<Amenity> amenity = amenityRepository.findById(orderDetailAmenity.getAmenity().getId());
        if (amenity.isEmpty()) {
            throw new RuntimeException("Amenity not found");
        }
        Amenity updatedAmenity = amenity.get();
        if (updatedAmenity.getQuantity() < orderDetailAmenity.getQuantity()) {
            throw new RuntimeException("Not enough quantity");
        }
        updatedAmenity.setQuantity(updatedAmenity.getQuantity() - orderDetailAmenity.getQuantity());
        amenityRepository.save(updatedAmenity);

        orderDetailAmenityRepository.save(orderDetailAmenity);
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

    @Transactional
    public String deleteOrderDetailAmenityById(String orderDetailAmenityId) {
        Optional<OrderDetailAmenity> oda = orderDetailAmenityRepository.findById(orderDetailAmenityId);
        if (oda.isEmpty()) {
            return("Order detail amenity not found");
        }else {
            String odaId = oda.get().getId();
            orderDetailAmenityRepository.deleteById(orderDetailAmenityId);
            return("Order detail amenity with id: " + odaId + " has been deleted");
        }
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