package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.OrderDetailAmenity.OrderDetailAmenityCreationRequest;
import com.swp.PodBookingSystem.dto.respone.Amenity.AmenityManagementResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetailAmenity.OrderDetailAmenityResponse;
import com.swp.PodBookingSystem.entity.Amenity;
import com.swp.PodBookingSystem.entity.OrderDetail;
import com.swp.PodBookingSystem.entity.OrderDetailAmenity;
import com.swp.PodBookingSystem.enums.AmenityType;
import com.swp.PodBookingSystem.mapper.AmenityMapper;
import com.swp.PodBookingSystem.mapper.OrderDetailAmenityMapper;
import com.swp.PodBookingSystem.repository.AmenityRepository;
import com.swp.PodBookingSystem.repository.OrderDetailAmenityRepository;
import com.swp.PodBookingSystem.repository.OrderDetailRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
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

    //GET:
    public List<AmenityManagementResponse> getOrderDetailAmenitiesByOrderDetailId(String orderDetailId) {
        return orderDetailAmenityRepository.findByOrderDetailId(orderDetailId).stream().map(amenity -> AmenityManagementResponse.builder()
                .id(amenity.getAmenity().getId())
                .name(amenity.getAmenity().getName())
                .price(amenity.getPrice())
                .quantity(amenity.getQuantity())
                .build()).collect(Collectors.toList());
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

    //CREATE in orderDetail
    public void createOrderDetailAmenity(OrderDetailAmenity orderDetailAmenity){
        Optional<Amenity> amenity = amenityRepository.findById(orderDetailAmenity.getAmenity().getId());
        if (amenity.isEmpty()) {
            throw new RuntimeException("Amenity not found");
        }
        Amenity updatedAmenity = amenity.get();
        if(updatedAmenity.getQuantity() < orderDetailAmenity.getQuantity()){
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