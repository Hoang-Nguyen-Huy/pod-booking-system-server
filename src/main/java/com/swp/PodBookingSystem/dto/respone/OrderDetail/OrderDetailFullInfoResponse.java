package com.swp.PodBookingSystem.dto.respone.OrderDetail;

import com.swp.PodBookingSystem.dto.respone.Account.AccountOrderResponse;
import com.swp.PodBookingSystem.dto.respone.Amenity.AmenityManagementConfigResponse;
import com.swp.PodBookingSystem.dto.respone.Amenity.AmenityManagementResponse;
import com.swp.PodBookingSystem.dto.respone.ServicePackage.ServicePackageResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailFullInfoResponse {
    String id;
    int roomId;
    String roomName;
    String roomImage;
    double roomPrice;
    String status;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String buildingAddress;
    int buildingId;
    ServicePackageResponse servicePackage;
    AccountOrderResponse customer;
    AccountOrderResponse orderHandler;
    List<AmenityManagementConfigResponse> amenities;
}
