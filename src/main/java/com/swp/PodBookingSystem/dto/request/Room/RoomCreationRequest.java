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
    Optional<String> image;
    Optional<String> status;
    Optional<Integer> roomTypeId;
}
