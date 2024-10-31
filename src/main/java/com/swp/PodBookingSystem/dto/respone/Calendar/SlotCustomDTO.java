package com.swp.PodBookingSystem.dto.respone.Calendar;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlotCustomDTO {
    LocalDateTime startTime;
    LocalDateTime endTime;
    boolean available;
}
