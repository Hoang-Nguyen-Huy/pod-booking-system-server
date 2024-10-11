package com.swp.PodBookingSystem.dto.request.Room;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swp.PodBookingSystem.entity.Amenity;
import com.swp.PodBookingSystem.entity.Room;
import com.swp.PodBookingSystem.entity.RoomType;
import com.swp.PodBookingSystem.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomWithAmenitiesDTO {
    Integer id;
    String name;
    double price;
    String image;
    @JsonProperty("amenities")
    List<Amenity> amenities;

}
