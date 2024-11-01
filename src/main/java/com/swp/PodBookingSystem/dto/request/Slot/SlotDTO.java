package com.swp.PodBookingSystem.dto.request.Slot;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlotDTO {
    LocalDateTime startTime;
    LocalDateTime endTime;
}
