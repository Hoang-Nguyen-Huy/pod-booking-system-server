package com.swp.PodBookingSystem.dto.request;

import com.swp.PodBookingSystem.enums.AccountRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
}

