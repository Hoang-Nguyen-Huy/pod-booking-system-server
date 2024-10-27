package com.swp.PodBookingSystem.dto.respone.Order;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NumberOrderByBuildingDto {
    Integer buildingNumber;
    String address;
    Long numberOrders;
}
