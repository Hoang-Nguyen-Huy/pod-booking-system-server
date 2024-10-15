
package com.swp.PodBookingSystem.dto.respone.OrderDetail;

import com.swp.PodBookingSystem.dto.respone.Amenity.AmenityManagementResponse;
import com.swp.PodBookingSystem.dto.respone.AmenityResponse;
import com.swp.PodBookingSystem.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailResponse {
    String id;
    String customerId;
    int buildingId;
    int roomId;
    String roomName;
    String orderId;
    List<AmenityManagementResponse> amenities;
    int servicePackageId;
    String orderHandledId;
    double priceRoom;
    OrderStatus status;
    LocalDateTime startTime;
    LocalDateTime endTime;
    LocalDateTime createdAt;
}
