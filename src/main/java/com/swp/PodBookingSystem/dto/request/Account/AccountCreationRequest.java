package com.swp.PodBookingSystem.dto.request.Account;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountCreationRequest {
    @Size(min = 5, message = "NAME_INVALID")
    String name;
    String email;
    String password;
    String role;
    int status;
}

