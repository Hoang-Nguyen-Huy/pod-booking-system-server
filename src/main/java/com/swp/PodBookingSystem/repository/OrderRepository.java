package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.Order;
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
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByAccountId(String accountId);

    @Query("""
            SELECT DISTINCT o FROM Order o
            JOIN OrderDetail od ON o.id = od.order.id
            WHERE o.account.id = :accountId
              AND od.status = :status
            AND od.id IS NOT NULL
            ORDER BY o.createdAt DESC
            """)
    Page<Order> findByAccountCustomerId(@Param("accountId") String accountId, @Param("status") OrderStatus status, Pageable pageable);

    Page<Order> findAll(Pageable pageable);

    @Query("""
            SELECT DISTINCT o FROM Order o
            JOIN OrderDetail od ON o.id = od.order.id
            WHERE o.createdAt >= :startTime 
              AND o.createdAt <= :endTime
              AND (
                :status IS NULL
                OR (
                    (:status = 'Pending' OR :status = 'Rejected') 
                    AND EXISTS (
                        SELECT 1 FROM OrderDetail od2 WHERE od2.order.id = o.id AND od2.status = :status
                    )
                    OR (:status = 'Successfully'
                    AND NOT EXISTS (
                        SELECT 1 FROM OrderDetail od3 WHERE od3.order.id = o.id AND od3.status <> :status
                    ))
                 )
              )
            ORDER BY o.createdAt DESC
            """)
    Page<Order> findAllWithTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("status") OrderStatus status,
            Pageable pageable);

    ;

    @Query("""
       SELECT DISTINCT o FROM Order o
       JOIN OrderDetail od ON o.id = od.order.id
       WHERE od.building.id = :buildingNumber
         AND o.createdAt >= :startTime 
         AND o.createdAt <= :endTime
         AND (
                :status IS NULL
                OR (
                    (:status = 'Pending' OR :status = 'Rejected') 
                    AND EXISTS (
                        SELECT 1 FROM OrderDetail od2 WHERE od2.order.id = o.id AND od2.status = :status
                    )
                    OR (:status = 'Successfully'
                    AND NOT EXISTS (
                        SELECT 1 FROM OrderDetail od3 WHERE od3.order.id = o.id AND od3.status <> :status
                    ))
                 )
         )
       ORDER BY o.createdAt DESC
       """)
    Page<Order> findOrdersByBuildingNumberAndTimeRange(
            @Param("buildingNumber") int buildingNumber,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("status") OrderStatus status,
            Pageable pageable);

    @Query("""
                SELECT DISTINCT o FROM Order o
                JOIN OrderDetail od ON o.id = od.order.id
                WHERE od.orderHandler.id = :staffId
                  AND o.createdAt >= :startTime 
                  AND o.createdAt <= :endTime
                  AND (
                :status IS NULL
                OR (
                    (:status = 'Pending' OR :status = 'Rejected') 
                    AND EXISTS (
                        SELECT 1 FROM OrderDetail od2 WHERE od2.order.id = o.id AND od2.status = :status
                    )
                    OR (:status = 'Successfully'
                    AND NOT EXISTS (
                        SELECT 1 FROM OrderDetail od3 WHERE od3.order.id = o.id AND od3.status <> :status
                    ))
                 )
              )
                ORDER BY o.createdAt DESC
            """)
    Page<Order> findOrdersByStaffIdAndTimeRange(
            @Param("staffId") String staffId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("status") OrderStatus status,
            Pageable pageable);

    @Modifying
    @Transactional
    void deleteById(String id);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.updatedAt = :updatedAt WHERE o.id = :orderId")
    void updateOrderUpdatedAt(String orderId, LocalDateTime updatedAt);

    @Query("""
             SELECT o FROM Order o
             JOIN o.account a
             WHERE LOWER(o.id) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Order> searchByKeyword(String keyword, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT o.id) FROM Order o " +
            "JOIN OrderDetail  od ON od.order.id = o.id " +
            "WHERE CURRENT_DATE BETWEEN CAST(od.startTime AS DATE) AND CAST(od.endTime AS DATE)" +
            "AND od.status = com.swp.PodBookingSystem.enums.OrderStatus.Successfully")
    int countCurrentlyOrder();

    @Query("SELECT COUNT(DISTINCT o.id) FROM Order o " +
            "JOIN OrderDetail od ON od.order.id = o.id " +
            "WHERE :startTime <= od.endTime " +
            "AND :endTime >= od.startTime " +
            "AND od.status = com.swp.PodBookingSystem.enums.OrderStatus.Successfully")
    int countOrdersBetweenDatetime(@Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime);
}
