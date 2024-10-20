package com.swp.PodBookingSystem.dto.request.OrderDetail;

import com.swp.PodBookingSystem.entity.*;
import com.swp.PodBookingSystem.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailCreationRequest {
    private Building building;
    private List<Room> selectedRooms;
    private ServicePackage servicePackage;
    private Account orderHandler; // If online default null
    private double priceRoom;
    private int discountPercentage;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
