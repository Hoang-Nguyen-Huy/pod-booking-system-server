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
@Table(name = "assignment")
public class Assignment {
    @Id
    String id;

    @OneToOne
    @JoinColumn(name = "staffId", referencedColumnName = "id", nullable = false)
    Account staff;

    String slot;

    String weekDate;

}
