package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {
    List<OrderDetail> findByCustomer_Id(String customerId);

}