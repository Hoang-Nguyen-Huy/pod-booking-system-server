package com.swp.PodBookingSystem.entity;

import com.swp.PodBookingSystem.enums.AccountRole;
import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
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
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;
    @Email
    String email;
    String password;
    String avatar;
    int point;

    @Enumerated(EnumType.STRING)
    AccountRole role;

    double balance;

    @Column(name = "buildingNumber")
    int buildingNumber;

    @Column(name = "rankingName")
    String rankingName;

    @Column(name = "createdAt")
    LocalDate createdAt;

    @OneToMany(mappedBy = "account")
    List<RefreshToken> refreshTokens;



    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }

}
