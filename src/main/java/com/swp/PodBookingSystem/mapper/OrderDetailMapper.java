package com.swp.PodBookingSystem.mapper;

import com.swp.PodBookingSystem.dto.request.Order.OrderCreationRequest;
import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailCreationRequest;
import com.swp.PodBookingSystem.dto.respone.OrderDetailResponse;
import com.swp.PodBookingSystem.entity.OrderDetail;
import com.swp.PodBookingSystem.enums.OrderStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface OrderDetailMapper {
    @Mapping(target = "buildingId", source = "building.id") // Assuming building is a ManyToOne relation
    @Mapping(target = "roomId", source = "room.id") // Assuming room is a OneToOne relation
    @Mapping(target = "servicePackageId", source = "servicePackage.id") // Assuming servicePackage is a ManyToOne relation
    @Mapping(target = "orderId", source = "order.id") // Assuming order is a ManyToOne relation
    @Mapping(target = "orderHandledId", source = "orderHandler.id") // Corrected mapping for orderHandledId
    OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail);

    @Named("stringToOrderStatus")
    default OrderStatus stringToOrderStatus(String status) {
        return OrderStatus.valueOf(status); // Converts a string to an enum
    }

    OrderDetail toOrderDetail(OrderDetailCreationRequest request);
}