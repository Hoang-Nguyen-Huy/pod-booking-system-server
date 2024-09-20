package com.swp.PodBookingSystem.dto.respone;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntrospectResponse {
    //    boolean valid;
    String sub;
    String accountId;
    String role;
    String iss;
    long iat;
    long exp;
}
