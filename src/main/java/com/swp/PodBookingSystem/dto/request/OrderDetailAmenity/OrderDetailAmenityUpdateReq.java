package com.swp.PodBookingSystem.dto.request.OrderDetailAmenity;
import com.swp.PodBookingSystem.enums.OrderDetailAmenityStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailAmenityUpdateReq {
    String id;
    OrderDetailAmenityStatus status;
}
