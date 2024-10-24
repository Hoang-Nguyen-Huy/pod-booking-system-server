package com.swp.PodBookingSystem.dto.respone.OrderDetailAmenity;

import com.swp.PodBookingSystem.enums.AmenityType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AmenityResponseDTO {
    Integer id;
    String name;
    AmenityType type;
    String imageUrl;
}
