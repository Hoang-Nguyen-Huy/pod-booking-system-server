package com.swp.PodBookingSystem.dto.request.Assignment;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentCreationRequest {
    String staffId;
    String slot;
    String weekDate;
}