package com.swp.PodBookingSystem.dto.respone.RoomImage;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomImageCustomResponse {
    Integer id;
    String imageUrl;
    Integer roomId;
}
