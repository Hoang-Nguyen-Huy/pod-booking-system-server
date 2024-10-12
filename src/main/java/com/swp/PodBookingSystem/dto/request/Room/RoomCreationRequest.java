package com.swp.PodBookingSystem.dto.request.Room;

import com.swp.PodBookingSystem.enums.RoomStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomCreationRequest {
    String name;
    String description;
    String image;
    RoomStatus status;
    Integer roomTypeId;
}
