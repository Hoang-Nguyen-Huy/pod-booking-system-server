package com.swp.PodBookingSystem.dto.respone.Account;

import com.swp.PodBookingSystem.enums.AccountRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountOrderResponse {
    String id;
    String name;
    String email;
    String avatar;
    int point;
    AccountRole role;
    double balance;
    int buildingNumber;
    String rankingName;
    LocalDate createdAt;
}