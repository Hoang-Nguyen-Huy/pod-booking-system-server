package com.swp.PodBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "orderDetailAmenity")
public class OrderDetailAmenity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    int quantity;

    double price;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "orderDetailId", nullable = false)
    OrderDetail orderDetail;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "amenityId", nullable = false)
    Amenity amenity;
}
