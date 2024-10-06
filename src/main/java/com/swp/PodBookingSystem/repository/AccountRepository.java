package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByEmail(String email);

    Optional<Account> findByEmail(String email);


}
