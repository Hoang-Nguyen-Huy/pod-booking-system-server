package com.swp.PodBookingSystem.dto.respone.OrderDetail;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueByMonthDto {
    LocalDate date;
    Double revenue;
}
