package com.swp.PodBookingSystem.dto.respone.Amenity;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmenityManagementResponse {
        private Integer id;
        private String name;
        private double price;
        private int quantity;
}
