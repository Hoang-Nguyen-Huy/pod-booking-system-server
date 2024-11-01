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
@Table(name = "ranking")
public class Ranking {
    @Id
    @Column(nullable = false, unique = true)
    String rankingName;

    @Column(name = "maxPoint")
    int maxPoint;

    @Column(name = "minPoint")
    int minPoint;

    @Column(name = "discountPercentage")
    int discountPercentage;
}
