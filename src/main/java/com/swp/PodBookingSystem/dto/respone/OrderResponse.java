package com.swp.PodBookingSystem.dto.respone;

import com.swp.PodBookingSystem.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    private String id;
    private String customerId;
    private int buildingId;
    private int roomId;
    private String orderId;
    private int servicePackageId;
    private String orderHandledId;
    private double priceRoom;
    private OrderStatus  status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;

}