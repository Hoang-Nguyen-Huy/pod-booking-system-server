package com.swp.PodBookingSystem.dto.request.OrderDetailAmenity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailAmenityCreationRequest {
    int quantity;
    double price;
    String orderDetailId;
    int amenityId;
}
