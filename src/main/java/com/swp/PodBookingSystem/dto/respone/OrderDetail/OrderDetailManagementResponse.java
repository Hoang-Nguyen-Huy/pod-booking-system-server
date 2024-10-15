package com.swp.PodBookingSystem.dto.respone.OrderDetail;

import com.swp.PodBookingSystem.dto.respone.Account.AccountOrderResponse;
import com.swp.PodBookingSystem.dto.respone.Amenity.AmenityManagementResponse;
import com.swp.PodBookingSystem.dto.respone.ServicePackage.ServicePackageResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailManagementResponse {
    private String id;
    private int roomId;
    private String roomName;
    private double roomPrice;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String buildingAddress;
    private int buildingId;
    private ServicePackageResponse servicePackage;
    private AccountOrderResponse customer;
    private AccountOrderResponse orderHandler;
    private List<AmenityManagementResponse> amenities;
}
