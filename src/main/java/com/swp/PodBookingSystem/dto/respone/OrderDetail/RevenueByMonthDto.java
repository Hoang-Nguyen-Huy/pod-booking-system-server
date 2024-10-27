package com.swp.PodBookingSystem.dto.respone.OrderDetail;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueByMonthDto {
    String date;
    Double revenue;
}
