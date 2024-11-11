package com.swp.PodBookingSystem.dto.respone.Amenity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmenityManagementConfigResponse {
    private Integer id;
    private String name;
    private double price;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
