package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.dto.respone.Order.NumberOrderByBuildingDto;
import com.swp.PodBookingSystem.dto.respone.OrderDetail.RevenueChartDto;
import com.swp.PodBookingSystem.entity.OrderDetail;
import com.swp.PodBookingSystem.enums.OrderStatus;
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
            "WHERE (od.customer.id = :customerId)" +
            "AND (od.status = :status)" +
            "ORDER BY od.startTime DESC")
    Page<OrderDetail> findByCustomer_Id(@Param("customerId") String customerId, @Param("status") OrderStatus status, Pageable pageable);

    List<OrderDetail> findByOrderId(String orderId);

    @Query("SELECT od FROM OrderDetail od " +
            "WHERE (od.order.id = :orderId)" +
            "AND (od.status = :status)" +
            "ORDER BY od.startTime DESC")
    List<OrderDetail> findByOrderIdAndStatus(@Param("orderId") String orderId, @Param("status") OrderStatus status);

    List<OrderDetail> findByEndTime(LocalDateTime endTime);

    @Query("""
    SELECT od FROM OrderDetail od
    WHERE od.startTime >= :startTime 
      AND od.endTime <= :endTime 
      AND EXISTS (
          SELECT 1 FROM OrderDetailAmenity oda 
          WHERE oda.orderDetail.id = od.id
      )
    ORDER BY od.updatedAt DESC
""")
    Page<OrderDetail> findAllWithTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable
    );

    @Query("""
    SELECT od FROM OrderDetail od 
    WHERE od.startTime >= :startTime 
      AND od.endTime <= :endTime 
      AND od.building.id = :buildingNumber 
      AND EXISTS (
          SELECT 1 FROM OrderDetailAmenity oda 
          WHERE oda.orderDetail.id = od.id
      )
    ORDER BY od.updatedAt DESC
""")
    Page<OrderDetail> findOrdersByBuildingNumberAndTimeRange(
            @Param("buildingNumber") int buildingNumber,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable
    );

    Page<OrderDetail> findAll(Pageable pageable);

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

    Optional<OrderDetail> findById(String id);

//    @Query("SELECT COUNT(od) FROM OrderDetail od WHERE od.building.id = :buildingId")
//    integer countByBuildingId(@Param("buildingId") Integer buildingId);

    @Query("SELECT SUM((od.priceRoom + COALESCE(amenityTotal.totalAmenityPrice, 0)) * " +
            "(1 - COALESCE(od.discountPercentage, 0) / 100.0) * (1 - COALESCE(sp.discountPercentage, 0) / 100.0)) as grandTotal " +
            "FROM OrderDetail od " +
            "LEFT JOIN od.servicePackage sp " +
            "LEFT JOIN (SELECT oda.orderDetail.id as orderDetailId, SUM(oda.price * oda.quantity) as totalAmenityPrice " +
            "           FROM OrderDetailAmenity oda GROUP BY oda.orderDetail.id) amenityTotal " +
            "ON od.id = amenityTotal.orderDetailId " +
            "WHERE CURRENT_DATE BETWEEN CAST(od.startTime AS DATE) AND CAST(od.endTime AS DATE) " +
            "AND od.status = com.swp.PodBookingSystem.enums.OrderStatus.Successfully")
    Double calculateRevenueCurrentDay();

    @Query("SELECT SUM((od.priceRoom + COALESCE(amenityTotal.totalAmenityPrice, 0)) * " +
            "(1 - COALESCE(od.discountPercentage, 0) / 100.0) * (1 - COALESCE(sp.discountPercentage, 0) / 100.0)) as grandTotal " +
            "FROM OrderDetail od " +
            "LEFT JOIN od.servicePackage sp " +
            "LEFT JOIN (SELECT oda.orderDetail.id as orderDetailId, SUM(oda.price * oda.quantity) as totalAmenityPrice " +
            "           FROM OrderDetailAmenity oda GROUP BY oda.orderDetail.id) amenityTotal " +
            "ON od.id = amenityTotal.orderDetailId " +
            "WHERE od.startTime >= :startTime AND od.endTime <= :endTime " +
            "AND od.status = com.swp.PodBookingSystem.enums.OrderStatus.Successfully")
    Optional<Double> calculateRevenueBetweenDateTime(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    @Query("SELECT NEW com.swp.PodBookingSystem.dto.respone.OrderDetail.RevenueChartDto(" +
            "CONCAT(YEAR(:startTime), '-', LPAD(CAST(MONTH(:startTime) AS string), 2, '0'), '-', LPAD(CAST(DAY(:startTime) AS string), 2, '0')), " +
            "SUM((od.priceRoom + COALESCE(amenityTotal.totalAmenityPrice, 0)) * " +
            "(1 - COALESCE(od.discountPercentage, 0) / 100.0) * (1 - COALESCE(sp.discountPercentage, 0) / 100.0))) " +
            "FROM OrderDetail od " +
            "LEFT JOIN od.servicePackage sp " +
            "LEFT JOIN (SELECT oda.orderDetail.id as orderDetailId, SUM(oda.price * oda.quantity) as totalAmenityPrice " +
            "           FROM OrderDetailAmenity oda GROUP BY oda.orderDetail.id) amenityTotal " +
            "ON od.id = amenityTotal.orderDetailId " +
            "WHERE DATE(od.startTime) = DATE(:startTime) " +
            "AND od.status = com.swp.PodBookingSystem.enums.OrderStatus.Successfully")
    RevenueChartDto calculateRevenueForSingleDay(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT NEW com.swp.PodBookingSystem.dto.respone.OrderDetail.RevenueChartDto(" +
            "CONCAT(YEAR(od.startTime), '-', LPAD(CAST(MONTH(od.startTime) AS string), 2, '0'), '-', LPAD(CAST(DAY(od.startTime) AS string), 2, '0')), " +
            "SUM((od.priceRoom + COALESCE(amenityTotal.totalAmenityPrice, 0)) * " +
            "(1 - COALESCE(od.discountPercentage, 0) / 100.0) * (1 - COALESCE(sp.discountPercentage, 0) / 100.0))) " +
            "FROM OrderDetail od " +
            "LEFT JOIN od.servicePackage sp " +
            "LEFT JOIN (SELECT oda.orderDetail.id as orderDetailId, SUM(oda.price * oda.quantity) as totalAmenityPrice " +
            "           FROM OrderDetailAmenity oda GROUP BY oda.orderDetail.id) amenityTotal " +
            "ON od.id = amenityTotal.orderDetailId " +
            "WHERE od.startTime BETWEEN :startTime AND :endTime " +
            "AND od.status = com.swp.PodBookingSystem.enums.OrderStatus.Successfully " +
            "GROUP BY CONCAT(YEAR(od.startTime), '-', LPAD(CAST(MONTH(od.startTime) AS string), 2, '0'), '-', LPAD(CAST(DAY(od.startTime) AS string), 2, '0')) " +
            "ORDER BY CONCAT(YEAR(od.startTime), '-', LPAD(CAST(MONTH(od.startTime) AS string), 2, '0'), '-', LPAD(CAST(DAY(od.startTime) AS string), 2, '0'))")
    List<RevenueChartDto> calculateRevenueByMonth(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);


    @Query("SELECT NEW com.swp.PodBookingSystem.dto.respone.OrderDetail.RevenueChartDto(" +
            "CONCAT(YEAR(MIN(od.startTime)), '-', LPAD(CAST(MONTH(MIN(od.startTime)) AS string), 2, '0'), '-01'), " +
            "SUM((od.priceRoom + COALESCE(amenityTotal.totalAmenityPrice, 0)) * " +
            "(1 - COALESCE(od.discountPercentage, 0) / 100.0) * " +
            "(1 - COALESCE(sp.discountPercentage, 0) / 100.0))) " +
            "FROM OrderDetail od " +
            "LEFT JOIN od.servicePackage sp " +
            "LEFT JOIN (SELECT oda.orderDetail.id as orderDetailId, SUM(oda.price * oda.quantity) as totalAmenityPrice " +
            "           FROM OrderDetailAmenity oda GROUP BY oda.orderDetail.id) amenityTotal " +
            "ON od.id = amenityTotal.orderDetailId " +
            "WHERE od.startTime BETWEEN :startTime AND :endTime " +
            "AND od.status = com.swp.PodBookingSystem.enums.OrderStatus.Successfully " +
            "GROUP BY YEAR(od.startTime), MONTH(od.startTime)")
    List<RevenueChartDto> calculateRevenueByQuarter(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);


    @Query("SELECT NEW com.swp.PodBookingSystem.dto.respone.Order.NumberOrderByBuildingDto(" +
            "od.building.id, od.building.address, COUNT(DISTINCT od.order.id)" +
            ") " +
            "FROM OrderDetail od " +
            "GROUP BY od.building.id, od.building.address")
    List<NumberOrderByBuildingDto> countOrdersByBuilding();



    @Modifying
    @Transactional
    @Query("UPDATE OrderDetail od " +
            "SET od.orderHandler.id = :staffId " +
            "WHERE WEEKDAY(od.startTime) = :weekDay " +
            "AND TIME(od.startTime) BETWEEN :slotStartTime AND :slotEndTime " +
            "AND od.building.id = :buildingNumber " +
            "AND od.startTime > CURRENT_TIMESTAMP " +
            "AND (od.orderHandler.id != :staffId OR od.orderHandler IS NULL)")
    void assignOrdersToStaff(@Param("staffId") String staffId,
                             @Param("weekDay") int weekDay,
                             @Param("slotStartTime") String slotStartTime,
                             @Param("slotEndTime") String slotEndTime,
                             @Param("buildingNumber") Integer buildingNumber);

}