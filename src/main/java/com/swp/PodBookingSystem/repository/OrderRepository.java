package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository  extends JpaRepository<Order, String> {
    List<Order> findByAccountId(String accountId);
}
