package com.swp.PodBookingSystem.dto.respone.Building;

import com.swp.PodBookingSystem.enums.BuildingStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingResponse {
    Integer id;
    String address;
    String description;
    String hotlineNumber;
    BuildingStatus status;
    LocalDate createdAt;
    LocalDate updatedAt;
}
