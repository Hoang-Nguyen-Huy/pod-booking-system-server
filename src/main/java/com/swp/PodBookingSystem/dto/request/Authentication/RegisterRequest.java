package com.swp.PodBookingSystem.dto.request.Authentication;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    @Size(min = 5, message = "NAME_INVALID")
    String email;
    String password;
    String name;
}
