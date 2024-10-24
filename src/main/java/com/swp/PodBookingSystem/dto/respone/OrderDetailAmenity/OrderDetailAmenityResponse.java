package com.swp.PodBookingSystem.dto.respone.OrderDetailAmenity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailAmenityResponse {
    String id;
    int quantity;
    double price;
    String orderDetailId;
    AmenityResponseDTO amenity;
}
