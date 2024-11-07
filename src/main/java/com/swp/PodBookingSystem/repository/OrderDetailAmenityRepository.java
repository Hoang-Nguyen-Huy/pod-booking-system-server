package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.OrderDetailAmenity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT o FROM OrderDetailAmenity o " +
            "JOIN o.orderDetail od " +
            "JOIN o.amenity a " +
            "WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.type) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<OrderDetailAmenity> searchByAmenityKeyword(@Param("keyword") String keyword, Pageable pageable);



}
