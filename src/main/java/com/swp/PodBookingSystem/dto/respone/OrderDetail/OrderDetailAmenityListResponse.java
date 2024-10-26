package com.swp.PodBookingSystem.dto.respone.OrderDetail;

import com.swp.PodBookingSystem.dto.respone.OrderDetailAmenity.OrderDetailAmenityResponseDTO;
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
    String customerName;
    int buildingId;
    String buildingAddress;
    int roomId;
    String roomName;
    String orderId;
    private List<OrderDetailAmenityResponseDTO> orderDetailAmenities;
    int servicePackageId;
    String orderHandledId;
    double priceRoom;
    OrderStatus status;
    LocalDateTime startTime;
    LocalDateTime endTime;
    LocalDateTime createdAt;
}
