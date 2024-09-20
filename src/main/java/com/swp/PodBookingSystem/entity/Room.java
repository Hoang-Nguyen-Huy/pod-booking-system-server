package com.swp.PodBookingSystem.entity;

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
    String image;

    @Value("${status:Available}")
    String status;

    @Column(name = "createdAt")
    LocalDate createdAt;

    @Column(name = "updatedAt")
    LocalDate updatedAt;
    int typeId;

}
