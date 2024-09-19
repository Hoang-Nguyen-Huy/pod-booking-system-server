package com.swp.PodBookingSystem.dto.respone;

import com.swp.PodBookingSystem.enums.AccountRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountResponse {
    String id;
    String name;
    String email;
    String password;
    String avatar;
    int point;
    AccountRole role;
    double balance;
    int buildingNumber;
    String rankingName;
    LocalDate createdAt;
}
