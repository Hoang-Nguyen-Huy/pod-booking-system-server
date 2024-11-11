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
            "WHERE (:address IS NULL OR b.address LIKE %:address%) " +
            "AND b.status = com.swp.PodBookingSystem.enums.BuildingStatus.Active " +
            "AND (:capacity IS NULL OR rt.capacity = :capacity) " +
            "AND (:startTime IS NULL AND :endTime IS NULL OR " +
            "     rt.quantity > (" +
            "         SELECT COUNT(DISTINCT r.id) FROM Room r " +
            "         JOIN OrderDetail od ON r.id = od.room.id " +
            "         WHERE r.roomType = rt " +
            "         AND ((od.startTime < :endTime AND od.endTime > :startTime) OR " +
            "              (od.startTime >= :startTime AND od.startTime < :endTime))" +
            "     ))" +
            "GROUP BY rt.id")
    Page<RoomType> findFilteredRoomTypes(@Param("address") String address,
                                         @Param("capacity") Integer capacity,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime,
                                         Pageable pageable);

    @Query("SELECT rt " +
            "FROM RoomType rt " +
            "WHERE rt.building.address = :buildingAddress")
    List<RoomType> findByBuildingAddress(String buildingAddress);

    @Query("SELECT rt " +
            "FROM RoomType rt " +
            "WHERE rt.building.id = :buildingId")
    List<RoomType> findByBuildingId(Integer buildingId);
}
