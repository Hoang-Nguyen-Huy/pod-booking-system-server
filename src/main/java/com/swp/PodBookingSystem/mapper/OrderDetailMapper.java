package com.swp.PodBookingSystem.mapper;

import com.swp.PodBookingSystem.dto.respone.OrderDetail.OrderDetailResponse;
import com.swp.PodBookingSystem.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {


    @Mapping(target = "buildingId", source = "building.id") // Map building ID to response
    @Mapping(target = "roomId", source = "room.id") // Map room ID to response
    @Mapping(target = "servicePackageId", source = "servicePackage.id") // Map service package ID to response
    @Mapping(target = "orderId", source = "order.id") // Map order ID to response
    @Mapping(target = "orderHandledId", source = "orderHandler.id") // Map order handled ID to response
    OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail);

}
