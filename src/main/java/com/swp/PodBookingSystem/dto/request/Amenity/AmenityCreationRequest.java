package com.swp.PodBookingSystem.dto.request.Amenity;

import com.swp.PodBookingSystem.enums.AmenityType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AmenityCreationRequest {

    String name;
    double price;
    int quantity;
    AmenityType type;
    int buildingId;
}
