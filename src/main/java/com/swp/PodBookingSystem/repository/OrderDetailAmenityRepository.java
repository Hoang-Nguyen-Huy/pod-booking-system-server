package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.OrderDetail;
import com.swp.PodBookingSystem.entity.OrderDetailAmenity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailAmenityRepository extends JpaRepository<OrderDetailAmenity, String> {
    List<OrderDetailAmenity> findByOrderDetailId(String orderDetailId);

    @Modifying
    @Transactional
    @Query("DELETE FROM OrderDetailAmenity a WHERE a.orderDetail.id = :orderDetailId")
    void deleteByOrderDetailId(String orderDetailId);

    @Query("SELECT od FROM OrderDetail od " +
            "JOIN od.orderDetailAmenity ode " +
            "JOIN ode.amenity a " +
            "JOIN od.building b " +
            "WHERE (LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND od.startTime >= :startDate AND od.endTime <= :endDate " +
            "AND (:buildingNumber IS NULL OR od.building.id = :buildingNumber)")
    Page<OrderDetail> searchByAmenityKeywordAndTimeRange(
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("buildingNumber") Integer buildingNumber,
            Pageable pageable);





}
