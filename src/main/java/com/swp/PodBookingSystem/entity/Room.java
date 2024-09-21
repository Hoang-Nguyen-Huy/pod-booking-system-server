package com.swp.PodBookingSystem.entity;

import com.swp.PodBookingSystem.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;
    int price;
    String description;

    @Column(nullable = true)
    String image;

    @Enumerated(EnumType.STRING)
    RoomStatus status;

    @Column(name = "createdAt")
    LocalDate createdAt;

    @Column(name = "updatedAt")
    LocalDate updatedAt;

    @ManyToOne
    @JoinColumn(name = "typeId", nullable = true)
    RoomType roomType;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        if (this.status == null) {
            this.status = RoomStatus.Available;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
    }
}
