package com.swp.PodBookingSystem.dto.respone.OrderDetail;

import com.swp.PodBookingSystem.dto.respone.Amenity.AmenityManagementResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailManagementResponse {
    private String id;
    private double priceRoom;
    private int discountPercentage;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<AmenityManagementResponse> amenities;
}
