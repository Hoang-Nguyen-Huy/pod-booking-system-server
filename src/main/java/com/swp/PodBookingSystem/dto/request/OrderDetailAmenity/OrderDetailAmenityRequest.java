package com.swp.PodBookingSystem.dto.request.OrderDetailAmenity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailAmenityRequest {
    private String orderDetailId;
    private int quantity;
    private int amenityId;
}
