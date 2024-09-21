package com.swp.PodBookingSystem.dto.respone.Room;

import com.swp.PodBookingSystem.entity.RoomType;
import com.swp.PodBookingSystem.enums.RoomStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomResponse {
    Integer id;
    String name;
    int price;
    String description;
    String image;
    RoomStatus status;
    LocalDate createdAt;
    LocalDate updatedAt;
    RoomType roomType;
}
