package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.dto.respone.Room.BookedRoomDto;
import com.swp.PodBookingSystem.entity.OrderDetail;
import com.swp.PodBookingSystem.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

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

    @Query("SELECT r FROM Room r WHERE r.roomType.id = :typeId")
    List<Room> findRoomsByTypeId(@Param("typeId") Integer typeId);

    @Query("SELECT CASE WHEN COUNT(o) = 0 THEN true ELSE false END " +
            "FROM OrderDetail o WHERE o.room.id = :roomId " +
            "AND (:startTime < o.endTime AND :endTime > o.startTime)")
    boolean isRoomAvailable(@Param("roomId") Integer roomId,
                            @Param("startTime") LocalDateTime startTime,
                            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT o " +
            "FROM OrderDetail o " +
            "WHERE o.startTime >= :startTime AND o.endTime <= :endTime")
    List<OrderDetail> findRoomAvailabilityWithinDateRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT NEW com.swp.PodBookingSystem.dto.respone.Room.BookedRoomDto(r.id, od.id, r.name, r.description, r.image, r.status, od.startTime, od.endTime, od.servicePackage, r.roomType) " +
            "FROM Room r " +
            "JOIN OrderDetail od ON r.id = od.room.id " +
            "WHERE od.endTime > :currentTime " +
            "AND od.status = com.swp.PodBookingSystem.enums.OrderStatus.Successfully " +
            "AND od.customer.id = :customerId " +
            "ORDER BY od.startTime")
    List<BookedRoomDto> findBookedRooms(@Param("currentTime") LocalDateTime currentTime, @Param("customerId") String customerId);
}
