package com.swp.PodBookingSystem.dto.respone.OrderDetailAmenity;

import com.swp.PodBookingSystem.entity.Amenity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailAmenityResponse {
    String id;
    int quantity;
    double price;
    Amenity amenity;
    String orderId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;


}
