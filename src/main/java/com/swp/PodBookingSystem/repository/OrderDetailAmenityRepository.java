package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.OrderDetailAmenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailAmenityRepository extends JpaRepository<OrderDetailAmenity, String> {

}
