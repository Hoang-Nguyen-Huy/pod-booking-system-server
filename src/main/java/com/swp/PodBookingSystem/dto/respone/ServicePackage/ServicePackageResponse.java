package com.swp.PodBookingSystem.dto.respone.ServicePackage;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ServicePackageResponse {
    Integer id;
    String name;
    int discountPercentage;
}
