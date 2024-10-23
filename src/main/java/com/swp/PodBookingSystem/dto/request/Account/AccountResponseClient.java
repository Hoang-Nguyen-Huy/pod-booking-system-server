package com.swp.PodBookingSystem.dto.request.Account;

import com.swp.PodBookingSystem.enums.AccountRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountResponseClient {
    String name;
    String email;
    String avatar;
    @Enumerated(EnumType.STRING)
    AccountRole role;
    int buildingNumber;
}
