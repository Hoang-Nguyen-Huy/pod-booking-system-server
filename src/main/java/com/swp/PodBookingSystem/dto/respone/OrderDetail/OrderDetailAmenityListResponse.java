package com.swp.PodBookingSystem.dto.respone.OrderDetail;

import com.swp.PodBookingSystem.entity.OrderDetail;
import com.swp.PodBookingSystem.entity.OrderDetailAmenity;
import com.swp.PodBookingSystem.entity.Room;
import com.swp.PodBookingSystem.enums.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailAmenityListResponse {
    String id;
    String customerId;
    int buildingId;
    int roomId;
    String roomName;
    String orderId;
    private List<OrderDetailAmenity> amenities;
    int servicePackageId;
    String orderHandledId;
    double priceRoom;
    OrderStatus status;
    LocalDateTime startTime;
    LocalDateTime endTime;
    LocalDateTime createdAt;
}
