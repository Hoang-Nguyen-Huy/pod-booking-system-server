package com.swp.PodBookingSystem.dto.respone;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    private String id;
    private String accountId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
