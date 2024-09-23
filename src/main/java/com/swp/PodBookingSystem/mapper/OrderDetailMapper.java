package com.swp.PodBookingSystem.mapper;

import com.swp.PodBookingSystem.dto.respone.OrderDetailResponse;
import com.swp.PodBookingSystem.entity.OrderDetail;
import com.swp.PodBookingSystem.enums.OrderStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface OrderDetailMapper {
    @Mapping(target = "buildingId", source = "orderDetail.building.id")
    @Mapping(target = "roomId", source = "orderDetail.room.id")
    @Mapping(target = "servicePackageId", source = "orderDetail.servicePackage.id")
    OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail);

    @Named("stringToOrderStatus")
    default OrderStatus stringToOrderStatus(String status) {
        return OrderStatus.valueOf(status); // Converts a string to an enum
    }
}
