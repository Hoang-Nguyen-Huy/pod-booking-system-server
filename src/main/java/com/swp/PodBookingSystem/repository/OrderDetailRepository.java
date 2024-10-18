package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {
    @Query("SELECT od FROM OrderDetail od " +
            "WHERE (od.customer.id = :customerId)")
    Page<OrderDetail> findByCustomer_Id(@Param("customerId") String customerId, Pageable pageable);

    List<OrderDetail> findByOrderId(String orderId);

    List<OrderDetail> findByEndTime(LocalDateTime endTime);

    @Query("SELECT o FROM OrderDetail o WHERE FUNCTION('DATE', o.startTime) BETWEEN FUNCTION('DATE', :startOfDay) AND FUNCTION('DATE', :endOfDay)")
    List<OrderDetail> findAllOrderDetailsByDay(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Modifying
    @Transactional
    @Query("UPDATE OrderDetail od SET od.updatedAt = :updatedAt WHERE od.id = :orderDetailId")
    void updateOrderDetailUpdatedAt(String orderDetailId, LocalDateTime updatedAt);

    @Modifying
    @Transactional
    @Query("DELETE FROM OrderDetail od WHERE od.order.id = :orderId")
    void deleteByOrderId(String orderId);
}