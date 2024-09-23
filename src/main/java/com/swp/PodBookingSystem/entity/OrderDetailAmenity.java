package com.swp.PodBookingSystem.entity;

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
@Table(name = "orderDetailAmentity")
public class OrderDetailAmenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    int quantity;

    double price;

    @ManyToOne
    @JoinColumn(name = "OrderDetailId", nullable = false)
    OrderDetail orderDetail;

    @ManyToOne
    @JoinColumn(name = "AmentityId", nullable = false)
    Amenity amenity;
}
