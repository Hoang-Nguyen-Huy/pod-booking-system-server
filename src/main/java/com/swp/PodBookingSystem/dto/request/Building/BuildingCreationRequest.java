package com.swp.PodBookingSystem.dto.request.Building;

import com.swp.PodBookingSystem.enums.BuildingStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingCreationRequest {
    String address;
    String description;
    String hotlineNumber;
    BuildingStatus status;
}
