package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    @Query("SELECT r FROM Room r " +
            "JOIN r.roomType rt " +
            "JOIN rt.building b " +
            "LEFT JOIN OrderDetail od ON r.id = od.room.id " +
            "WHERE (:address IS NULL OR b.address LIKE %:address%) " +
            "AND (:capacity IS NULL OR rt.capacity = :capacity) " +
            "AND (:startTime IS NULL OR :endTime IS NULL " +
            "     OR NOT EXISTS (SELECT 1 FROM OrderDetail od2 " +
            "                   WHERE od2.room.id = r.id " +
            "                   AND ((od2.startTime BETWEEN :startTime AND :endTime) " +
            "                        OR (od2.endTime BETWEEN :startTime AND :endTime))))")
    Page<Room> findFilteredRoomsOnLandingPage(@Param("address") String address,
                                 @Param("capacity") Integer capacity,
                                 @Param("startTime") LocalDateTime startTime,
                                 @Param("endTime") LocalDateTime endTime,
                                 Pageable pageable);

}
