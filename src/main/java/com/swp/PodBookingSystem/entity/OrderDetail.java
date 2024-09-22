package com.swp.PodBookingSystem.entity;

import com.swp.PodBookingSystem.enums.OrderStatus;
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
@Table(name = "orderDetail")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", referencedColumnName = "id")
    Account customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buildingNumber", referencedColumnName = "id")
    Building building;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomId", referencedColumnName = "id", unique = true)
    Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", referencedColumnName = "id", nullable = false)
    Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicePackageId", referencedColumnName = "id")
    ServicePackage servicePackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderHandlerId", referencedColumnName = "id")
    Account orderHandler;

    @Column(name = "priceRoom", nullable = false)
    double priceRoom;

    @Column(name = "discountPercentage", nullable = false)
    int discountPercentage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    OrderStatus status;

    @Column(name = "startTime", nullable = false)
    LocalDateTime startTime;

    @Column(name = "endTime", nullable = false)
    LocalDateTime endTime;

    @Column(name = "createdAt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    LocalDateTime createdAt;

    @Column(name = "updatedAt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
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
