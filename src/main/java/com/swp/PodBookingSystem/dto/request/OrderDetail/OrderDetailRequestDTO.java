package com.swp.PodBookingSystem.dto.request.OrderDetail;

import com.swp.PodBookingSystem.entity.*;
import com.swp.PodBookingSystem.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailRequestDTO {
    private String id;
    private Account customer;
    private Building building;
    private Room room;
    private Order order;
    private ServicePackage servicePackage;
    private Account orderHandler;
    private double priceRoom;
    private int discountPercentage;
    private OrderStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
