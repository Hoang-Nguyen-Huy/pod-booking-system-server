package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteByAccountId(String accountId);

    void deleteByToken(String refreshToken);

    Optional<RefreshToken> findByToken(String refreshToken);
}
