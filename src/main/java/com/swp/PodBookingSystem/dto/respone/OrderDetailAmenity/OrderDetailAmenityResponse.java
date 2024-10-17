package com.swp.PodBookingSystem.dto.respone.OrderDetailAmenity;

import com.swp.PodBookingSystem.entity.Amenity;
import lombok.*;

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
}
