package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.enums.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByEmail(String email);

    Optional<Account> findByEmail(String email);

    @Query("SELECT a FROM Account a WHERE a.role = :role")
    List<Account> findByRole(@Param("role") AccountRole role);

    @Query("""
            SELECT a FROM Account a 
            WHERE (:keyword IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
                   OR LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:role IS NULL OR a.role = :role)
            """)
    List<Account> searchAccounts(@Param("keyword") String keyword, @Param("role") AccountRole role);

    @Query("SELECT COUNT(DISTINCT a.id) FROM Account a " +
            "JOIN OrderDetail od ON od.customer.id = a.id " +
            "WHERE CURRENT_DATE BETWEEN CAST(od.startTime AS DATE) AND CAST(od.endTime AS DATE) " +
            "AND od.status = com.swp.PodBookingSystem.enums.OrderStatus.Successfully")
    int countCurrentCustomer();
}
