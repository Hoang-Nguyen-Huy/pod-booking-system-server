package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.dto.respone.OrderDetail.RevenueByMonthDto;
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
            "ORDER BY od.createdAt DESC")
    Page<OrderDetail> findByCustomer_Id(@Param("customerId") String customerId, @Param("status") OrderStatus status, Pageable pageable);

    List<OrderDetail> findByOrderId(String orderId);

    List<OrderDetail> findByEndTime(LocalDateTime endTime);

    @Query(value = "SELECT od FROM OrderDetail od WHERE od.createdAt BETWEEN :startTime AND :endTime ORDER BY od.createdAt DESC")
    Page<OrderDetail> findAllWithTimeRange(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime,
                                           Pageable pageable);

    @Query(value = "SELECT od FROM OrderDetail od WHERE od.createdAt BETWEEN :startTime AND :endTime AND od.building.id = :buildingNumber ORDER BY od.createdAt DESC")
    Page<OrderDetail> findOrdersByBuildingNumberAndTimeRange(
            @Param("buildingNumber") int buildingNumber,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

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
    Double calculateRevenueBetweenDateTime(@Param("startTime") LocalDateTime startTime,
                            @Param("endTime") LocalDateTime endTime);

//    @Query("SELECT NEW com.swp.PodBookingSystem.dto.respone.OrderDetail.RevenueByMonthDto(" +
//            "CONCAT(YEAR(od.startTime), '-', MONTH(od.startTime), '-01') AS date, " +
//            "SUM((od.priceRoom + COALESCE(amenityTotal.totalAmenityPrice, 0)) * " +
//            "(1 - COALESCE(od.discountPercentage, 0) / 100.0) * (1 - COALESCE(sp.discountPercentage, 0) / 100.0)) AS revenue " +
//            ") " +
//            "FROM OrderDetail od " +
//            "LEFT JOIN od.servicePackage sp " +
//            "LEFT JOIN (SELECT oda.orderDetail.id as orderDetailId, SUM(oda.price * oda.quantity) as totalAmenityPrice " +
//            "           FROM OrderDetailAmenity oda GROUP BY oda.orderDetail.id) amenityTotal " +
//            "ON od.id = amenityTotal.orderDetailId " +
//            "WHERE FUNCTION('YEAR', od.startTime) = FUNCTION('YEAR', CURRENT_DATE) " +
//            "AND od.status = com.swp.PodBookingSystem.enums.OrderStatus.Successfully " +
//            "GROUP BY YEAR(od.startTime), MONTH(od.startTime)")
//    List<RevenueByMonthDto> calculateRevenueByMonthForCurrentYear();

//    @Query("SELECT NEW com.swp.PodBookingSystem.dto.respone.OrderDetail.RevenueByMonthDto(" +
//            "DATE(CONCAT(YEAR(od.startTime), '-', MONTH(od.startTime), '-01')), " +
//            "SUM((od.priceRoom + COALESCE(amenityTotal.totalAmenityPrice, 0)) * " +
//            "(1 - COALESCE(od.discountPercentage, 0) / 100.0) * (1 - COALESCE(sp.discountPercentage, 0) / 100.0))" +
//            ") " +
//            "FROM OrderDetail od " +
//            "LEFT JOIN od.servicePackage sp " +
//            "LEFT JOIN (SELECT oda.orderDetail.id as orderDetailId, SUM(oda.price * oda.quantity) as totalAmenityPrice " +
//            "           FROM OrderDetailAmenity oda GROUP BY oda.orderDetail.id) amenityTotal " +
//            "ON od.id = amenityTotal.orderDetailId " +
//            "WHERE FUNCTION('YEAR', od.startTime) = FUNCTION('YEAR', CURRENT_DATE) " +
//            "AND od.status = com.swp.PodBookingSystem.enums.OrderStatus.Successfully " +
//            "GROUP BY YEAR(od.startTime), MONTH(od.startTime)")
//    List<RevenueByMonthDto> calculateRevenueByMonthForCurrentYear();

    //    @Query("SELECT NEW com.swp.PodBookingSystem.dto.respone.OrderDetail.RevenueByMonthDto(FUNCTION('DATE', od.startTime), " +
//            "SUM((od.priceRoom + COALESCE(amenityTotal.totalAmenityPrice, 0)) * " +
//            "(1 - COALESCE(od.discountPercentage, 0) / 100.0) * (1 - COALESCE(sp.discountPercentage, 0) / 100.0))) " +
//            "FROM OrderDetail od " +
//            "LEFT JOIN od.servicePackage sp " +
//            "LEFT JOIN (SELECT oda.orderDetail.id as orderDetailId, SUM(oda.price * oda.quantity) as totalAmenityPrice " +
//            "           FROM OrderDetailAmenity oda GROUP BY oda.orderDetail.id) amenityTotal " +
//            "ON od.id = amenityTotal.orderDetailId " +
//            "WHERE FUNCTION('YEAR', od.startTime) = FUNCTION('YEAR', CURRENT_DATE) " +
//            "AND FUNCTION('DAY', od.startTime) = 1 " +
//            "AND od.status = com.swp.PodBookingSystem.enums.OrderStatus.Successfully " +
//            "GROUP BY FUNCTION('YEAR', od.startTime), FUNCTION('MONTH', od.startTime)")
//    List<RevenueByMonthDto> calculateRevenueByMonthForCurrentYearO();

//    @Query("SELECT NEW com.swp.PodBookingSystem.dto.respone.OrderDetail.RevenueByMonthDto(CONCAT(YEAR(od.startTime), '-', MONTH(od.startTime), '-01'), " +
//            "SUM(((od.priceRoom + COALESCE(amenityTotal.totalAmenityPrice, 0)) * " +
//            "(1 - COALESCE(od.discountPercentage, 0) / 100.0) * (1 - COALESCE(sp.discountPercentage, 0) / 100.0))) " +
//            "FROM OrderDetail od " +
//            "LEFT JOIN od.servicePackage sp " +
//            "LEFT JOIN (SELECT oda.orderDetail.id as orderDetailId, SUM(oda.price * oda.quantity) as totalAmenityPrice " +
//            "           FROM OrderDetailAmenity oda GROUP BY oda.orderDetail.id) amenityTotal " +
//            "ON od.id = amenityTotal.orderDetailId " +
//            "WHERE YEAR(od.startTime) = YEAR(CURRENT_DATE) " +
//            "AND DAY(od.startTime) = 1 " +
//            "AND od.status = com.swp.PodBookingSystem.enums.OrderStatus.Successfully " +
//            "GROUP BY YEAR(od.startTime), MONTH(od.startTime)")
//    List<RevenueByMonthDto> calculateRevenueByMonthForCurrentYear();

}