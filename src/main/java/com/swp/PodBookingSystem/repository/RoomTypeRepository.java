package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {
    @Query("SELECT rt FROM RoomType rt " +
            "JOIN rt.building b " +
            "LEFT JOIN Room r ON r.roomType.id = rt.id " +
            "LEFT JOIN OrderDetail od ON r.id = od.room.id " +
            "WHERE (:address IS NULL OR b.address LIKE %:address%) " +
            "AND (:capacity IS NULL OR rt.capacity = :capacity) " +
            "AND ((:startTime IS NULL AND :endTime IS NULL) " +
            "     OR NOT EXISTS (SELECT 1 FROM OrderDetail od2 " +
            "                   WHERE od2.room.id = r.id " +
            "                   AND ((od2.startTime BETWEEN :startTime AND :endTime) " +
            "                        OR (od2.endTime BETWEEN :startTime AND :endTime)))) " +
            "GROUP BY rt.id " +
            "HAVING (:startTime IS NOT NULL AND :endTime IS NOT NULL AND COUNT(r.id) >= 1) " +
            "     OR (:startTime IS NULL AND :endTime IS NULL)")
    Page<RoomType> findFilteredRoomTypes(@Param("address") String address,
                                         @Param("capacity") Integer capacity,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime,
                                         Pageable pageable);

    @Query("SELECT rt " +
            "FROM RoomType rt " +
            "WHERE rt.building.address = :buildingAddress")
    List<RoomType> findByBuildingAddress(String buildingAddress);
}
