package com.swp.PodBookingSystem.dto.respone;

import com.swp.PodBookingSystem.enums.AmenityType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AmenityResponse {
    private Integer id;
    private String name;
    private double price;
    private int quantity;
    private AmenityType type;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
