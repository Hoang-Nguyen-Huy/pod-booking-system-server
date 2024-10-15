package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.OrderDetailAmenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailAmenityRepository extends JpaRepository<OrderDetailAmenity, String> {
    List<OrderDetailAmenity> findByOrderDetailId(String orderDetailId);

    @Modifying
    @Transactional
    @Query("DELETE FROM OrderDetailAmenity a WHERE a.orderDetail.id = :orderDetailId")
    void deleteByOrderDetailId(String orderDetailId);
}
