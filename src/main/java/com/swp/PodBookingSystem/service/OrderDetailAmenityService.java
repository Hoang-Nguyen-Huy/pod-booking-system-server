package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.respone.Amenity.AmenityManagementResponse;
import com.swp.PodBookingSystem.entity.Amenity;
import com.swp.PodBookingSystem.entity.OrderDetailAmenity;
import com.swp.PodBookingSystem.repository.AmenityRepository;
import com.swp.PodBookingSystem.repository.OrderDetailAmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderDetailAmenityService {
    private final OrderDetailAmenityRepository orderDetailAmenityRepository;
    private final AmenityRepository amenityRepository;

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
    public void createOrderDetailAmenity(OrderDetailAmenity orderDetailAmenity){
        Optional<Amenity> amenity = amenityRepository.findById(orderDetailAmenity.getAmenity().getId());
        if (amenity.isEmpty()) {
            throw new RuntimeException("Amenity not found");
        }
        Amenity updatedAmenity = amenity.get();
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
}