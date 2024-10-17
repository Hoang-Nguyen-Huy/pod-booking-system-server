package com.swp.PodBookingSystem.dto.request.Order;

import com.swp.PodBookingSystem.entity.Account;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderUpdateStaffRequest {
    private String id;
    private Account orderHandler;
}
