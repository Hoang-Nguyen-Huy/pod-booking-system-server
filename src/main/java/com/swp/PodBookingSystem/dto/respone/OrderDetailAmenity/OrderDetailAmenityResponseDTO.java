package com.swp.PodBookingSystem.dto.respone.OrderDetailAmenity;

import com.swp.PodBookingSystem.entity.Amenity;
import com.swp.PodBookingSystem.enums.AmenityType;
import com.swp.PodBookingSystem.enums.OrderDetailAmenityStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailAmenityResponseDTO {
    String id;
    int quantity;
    double price;
    String orderDetailId;
    int amenityId;
    String amenityName;
    AmenityType amenityType;
    OrderDetailAmenityStatus status;
    String statusDescription;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}