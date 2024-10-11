package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.OrderDetailAmenity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailAmenityRepository extends JpaRepository<OrderDetailAmenity, String> {
    List<OrderDetailAmenity> findByOrderDetailId(String orderDetailId);
}
