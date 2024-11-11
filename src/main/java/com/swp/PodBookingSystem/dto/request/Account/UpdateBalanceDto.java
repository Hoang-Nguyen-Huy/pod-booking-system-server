package com.swp.PodBookingSystem.dto.request.Account;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateBalanceDto {
    String accountId;
    double usedBalance;
}
