package com.swp.PodBookingSystem.mapper;

import com.swp.PodBookingSystem.dto.request.OrderDetailAmenity.OrderDetailAmenityCreationRequest;
import com.swp.PodBookingSystem.dto.respone.OrderDetailAmenity.OrderDetailAmenityResponse;
import com.swp.PodBookingSystem.entity.OrderDetailAmenity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderDetailAmenityMapper {
    OrderDetailAmenity toOrderDetailAmenity(OrderDetailAmenityCreationRequest request);

    OrderDetailAmenityResponse toOrderDetailAmenityResponse(OrderDetailAmenity orderDetailAmenity);
}
