package com.swp.PodBookingSystem.dto.request.Order;


import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailUpdateRoomRequest;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderUpdateRequest {
    String id;
    OrderStatus status;
    Account orderHandler;
    List<OrderDetailUpdateRoomRequest> orderDetails;
}
