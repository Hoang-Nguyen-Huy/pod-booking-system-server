package com.swp.PodBookingSystem.dto.respone.Order;

import com.swp.PodBookingSystem.dto.respone.OrderDetail.OrderDetailManagementResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderManagementResponse {
        private String id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<OrderDetailManagementResponse> orderDetails;
    }
