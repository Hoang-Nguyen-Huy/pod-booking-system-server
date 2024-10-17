package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.respone.Amenity.AmenityManagementResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetailAmenity.OrderDetailAmenityResponse;
import com.swp.PodBookingSystem.entity.Amenity;
import com.swp.PodBookingSystem.entity.OrderDetailAmenity;
import com.swp.PodBookingSystem.enums.AmenityType;
import com.swp.PodBookingSystem.repository.AmenityRepository;
import com.swp.PodBookingSystem.repository.OrderDetailAmenityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderDetailAmenityService {
    private OrderDetailAmenityRepository orderDetailAmenityRepository;
    private AmenityRepository amenityRepository;

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
    public void createOrderDetailAmenity(OrderDetailAmenity orderDetailAmenity) {
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

    //UTILS:
    public void restoreAmenityQuantity(String orderDetailId) {
        List<OrderDetailAmenity> orderDetailAmenities = orderDetailAmenityRepository.findByOrderDetailId(orderDetailId);
        for (OrderDetailAmenity orderDetailAmenity : orderDetailAmenities) {
            if (orderDetailAmenity.getAmenity().getType() == AmenityType.Office) {
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

    public Page<OrderDetailAmenityResponse> getOrderDetailAmenities(int page, int take) {
        Pageable pageable = PageRequest.of(page - 1, take);
        Page<OrderDetailAmenity> orderDetailAmenityPage = orderDetailAmenityRepository.findAll(pageable);

        List<OrderDetailAmenityResponse> responseList = orderDetailAmenityPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responseList, pageable, orderDetailAmenityPage.getTotalElements());
    }

    private OrderDetailAmenityResponse mapToResponse(OrderDetailAmenity orderDetailAmenity) {
        return OrderDetailAmenityResponse.builder()
                .id(orderDetailAmenity.getId())
                .quantity(orderDetailAmenity.getQuantity())
                .price(orderDetailAmenity.getPrice())
                .amenity(orderDetailAmenity.getAmenity())
                .build();
    }
}