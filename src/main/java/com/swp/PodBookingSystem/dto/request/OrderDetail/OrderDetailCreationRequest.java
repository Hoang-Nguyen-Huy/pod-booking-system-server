package com.swp.PodBookingSystem.dto.request.OrderDetail;

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
    private String roomTypeId;
    private List<Integer> selectedRooms;
    private String date;
    private List<Integer> timeSlots;
    private String servicePackageId;
    private String customerId;
}
