package com.swp.PodBookingSystem.dto.request.OrderDetail;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailUpdateRoomRequest {
    String id;
    int roomId;
}