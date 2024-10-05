package com.swp.PodBookingSystem.dto.request.Room;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomAvailabilityDTO {
     Integer roomId;
     String name;
     LocalDateTime startTime;
     LocalDateTime endTime;
}
