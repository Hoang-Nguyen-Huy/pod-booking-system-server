package com.swp.PodBookingSystem.dto.respone.Assignment;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentResponse {
    String id;
    String staffId;
    String slot;
    String weekDate;
    String nameStaff;
}
