package com.swp.PodBookingSystem.dto.request.Room;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomCreationRequest {
    String name;
    int price;
    String description;
    String image;
    String status;
    Integer roomTypeId;
}
