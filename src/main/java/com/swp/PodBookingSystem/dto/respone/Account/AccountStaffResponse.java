package com.swp.PodBookingSystem.dto.respone.Account;

import com.swp.PodBookingSystem.dto.respone.Building.BuildingResponse;
import com.swp.PodBookingSystem.enums.AccountRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountStaffResponse {
    String id;
    String name;
    String email;
    String password;
    String avatar;
    int point;
    AccountRole role;
    double balance;
    BuildingResponse building;
    String rankingName;
    LocalDate createdAt;
    int status;
}
