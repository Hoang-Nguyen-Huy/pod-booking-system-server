package com.swp.PodBookingSystem.dto.request.Order;

import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailCreationRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCreationRequest {
    String accountId; // ID of the account placing the order
    List<OrderDetailCreationRequest> orderDetails; // List of order details associated with the order
}
