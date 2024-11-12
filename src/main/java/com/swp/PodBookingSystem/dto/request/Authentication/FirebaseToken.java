package com.swp.PodBookingSystem.dto.request.Authentication;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FirebaseToken {
    String email;
    String name;
    String avatar;
}
