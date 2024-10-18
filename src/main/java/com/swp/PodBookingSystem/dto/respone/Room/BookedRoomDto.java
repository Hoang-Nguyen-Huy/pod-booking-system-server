package com.swp.PodBookingSystem.dto.respone.Room;

import com.swp.PodBookingSystem.entity.RoomType;
import com.swp.PodBookingSystem.entity.ServicePackage;
import com.swp.PodBookingSystem.enums.RoomStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookedRoomDto {
    Integer id;
    String name;
    String description;
    String image;
    RoomStatus status;
    LocalDateTime startTime;
    LocalDateTime endTime;
    ServicePackage servicePackage;
    RoomType roomType;
}
