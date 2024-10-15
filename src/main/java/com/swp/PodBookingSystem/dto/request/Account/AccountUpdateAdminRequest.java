package com.swp.PodBookingSystem.dto.request.Account;

import com.swp.PodBookingSystem.enums.AccountRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountUpdateAdminRequest {
    String name;
    AccountRole role;
    int buildingNumber;
    int status;
}
