package com.swp.PodBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swp.PodBookingSystem.enums.OrderDetailAmenityStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderDetailAmenityStatus status;

    @Column(name = "createdAt")
    LocalDateTime createdAt;

    @Column(name = "updatedAt")
    LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
