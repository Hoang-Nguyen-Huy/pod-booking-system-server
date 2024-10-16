package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.OrderDetail;
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
    List<OrderDetail> findByCustomer_Id(String customerId);

    List<OrderDetail> findByOrderId(String orderId);

    @Query("SELECT o FROM OrderDetail o WHERE FUNCTION('DATE', o.startTime) BETWEEN FUNCTION('DATE', :startOfDay) AND FUNCTION('DATE', :endOfDay)")
    List<OrderDetail> findAllOrderDetailsByDay(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Modifying
    @Transactional
    @Query("DELETE FROM OrderDetail od WHERE od.order.id = :orderId")
    void deleteByOrderId(String orderId);
}