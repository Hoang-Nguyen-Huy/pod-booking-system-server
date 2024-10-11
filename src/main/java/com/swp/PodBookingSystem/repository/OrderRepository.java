package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository  extends JpaRepository<Order, String> {
    List<Order> findByAccountId(String accountId);

    Page<Order> findAll(Pageable pageable);

    @Query(value = "SELECT DISTINCT o FROM Order o JOIN OrderDetail od ON o.id = od.order.id WHERE o.createdAt >= :startTime AND o.createdAt <= :endTime ORDER BY o.createdAt ASC")
    Page<Order> findAllWithTimeRange(@Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime,
                                     Pageable pageable);

    @Query(value = "SELECT DISTINCT o FROM Order o JOIN OrderDetail od ON o.id = od.order.id WHERE od.building.id = :buildingNumber ORDER BY o.createdAt ASC")
    Page<Order> findDistinctOrdersByBuildingNumber(@Param("buildingNumber") int buildingNumber, Pageable pageable);

    @Query(value = "SELECT DISTINCT o FROM Order o JOIN OrderDetail od ON o.id = od.order.id WHERE od.building.id = :buildingNumber AND o.createdAt >= :startTime AND o.createdAt <= :endTime ORDER BY o.createdAt ASC")
    Page<Order> findOrdersByBuildingNumberAndTimeRange(@Param("buildingNumber") int buildingNumber,
                                                       @Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime,
                                                       Pageable pageable);
}
