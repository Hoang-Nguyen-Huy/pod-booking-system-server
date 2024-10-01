package com.swp.PodBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.annotation.JsonIgnore;


import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "servicePackage")
public class ServicePackage {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "name")
    String name;

    @Column(name = "discountPercentage")
    int discountPercentage;

    @JsonIgnore
    @OneToMany(mappedBy = "servicePackage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    List<OrderDetail> orderDetails;
}
