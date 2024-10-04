package com.swp.PodBookingSystem.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CalendarRequest {
    String subject;
    String description;
    String to;
    String summary;
    LocalDateTime eventDateTime;
}
