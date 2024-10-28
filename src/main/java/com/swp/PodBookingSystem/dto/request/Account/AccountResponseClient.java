package com.swp.PodBookingSystem.dto.request.Account;

import com.swp.PodBookingSystem.enums.AccountRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountResponseClient {
    @Id
    String id;
    String name;
    String email;
    String avatar;
    String phoneNumber;
    double balance;
    String rankingName;
    int point;
    @Enumerated(EnumType.STRING)
    AccountRole role;
    int buildingNumber;
}
