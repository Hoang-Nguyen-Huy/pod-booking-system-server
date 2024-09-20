package com.swp.PodBookingSystem.entity;

import com.swp.PodBookingSystem.enums.Building.BuildingStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "building")
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    BuildingStatus status;
    String address;
    String description;

    @Column(name = "hotlineNumber")
    String hotlineNumber;

    @Column(name = "createdAt")
    LocalDate createdAt;

    @Column(name = "updatedAt")
    LocalDate updatedAt;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<RoomType> roomTypes;
}
