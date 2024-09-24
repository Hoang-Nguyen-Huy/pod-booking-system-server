package com.swp.PodBookingSystem.dto.respone.RoomType;

import com.swp.PodBookingSystem.entity.Building;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomTypeResponse {
    Integer id;
    String name;
    int quantity;
    int capacity;
    Building building;
}
