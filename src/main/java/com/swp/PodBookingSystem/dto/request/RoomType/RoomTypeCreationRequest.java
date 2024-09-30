package com.swp.PodBookingSystem.dto.request.RoomType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomTypeCreationRequest {
    String name;
    int quantity;
    int capacity;
    Integer buildingId;
}
