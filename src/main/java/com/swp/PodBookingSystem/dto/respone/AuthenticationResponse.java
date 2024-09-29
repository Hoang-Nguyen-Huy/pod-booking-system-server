package com.swp.PodBookingSystem.dto.respone;

import com.swp.PodBookingSystem.dto.request.Account.AccountResponseClient;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    String accessToken;
    String refreshToken;
    AccountResponseClient account;
}
