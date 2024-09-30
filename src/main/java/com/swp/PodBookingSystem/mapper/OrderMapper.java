package com.swp.PodBookingSystem.mapper;

import com.swp.PodBookingSystem.dto.respone.OrderResponse;
import com.swp.PodBookingSystem.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "order.account.id", target = "accountId")
    OrderResponse toOrderResponse(Order order);

}
